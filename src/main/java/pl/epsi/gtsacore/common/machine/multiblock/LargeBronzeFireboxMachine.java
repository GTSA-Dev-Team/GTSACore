package pl.epsi.gtsacore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import pl.epsi.gtsacore.common.machine.part.PrimitiveFuelHatchPartMachine;

import java.util.List;

public class LargeBronzeFireboxMachine extends WorkableMultiblockMachine implements IDisplayUIMachine {
    private final ConditionalSubscriptionHandler fuelHatchSubscription;
    private TickableSubscription tickSubscription;

    private static final int maxFuel = 70000;
    private static final int maxHeat = 1423;

    @Persisted
    @Getter
    private int fuelTimer = 0;

    @Persisted
    @Getter
    private int heatTImer = 0;

    @Persisted
    @Getter
    private int fuel = 0;

    @Getter
    private int heat = 0;



     public LargeBronzeFireboxMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
         this.fuelHatchSubscription = new ConditionalSubscriptionHandler(this, this::fuelHatchTick, () -> true);
    }

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(LargePrimitiveSmelterMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private void fuelHatchTick() {
        if (fuelTimer == 0) {

            int stored = 0;
            ItemStack contents = ItemStack.EMPTY;
            int itemValue = 0;

            if (isFormed()) {
                var array = getParts().stream()
                        .filter(PrimitiveFuelHatchPartMachine.class::isInstance)
                        .map(PrimitiveFuelHatchPartMachine.class::cast)
                        .toArray(PrimitiveFuelHatchPartMachine[]::new);

                if (array.length == 1) {
                    var fuelHatch = array[0];

                    var fuelItemHandler = (NotifiableItemStackHandler) fuelHatch.getRecipeHandlers().get(0)
                            .getCapability(ItemRecipeCapability.CAP).get(0);
                    if (!fuelItemHandler.isEmpty()) {
                        contents = (ItemStack) fuelItemHandler.getContents().get(0);
                        stored = ((ItemStack) fuelItemHandler.getContents().get(0)).getCount();
                        itemValue = contents.is(Items.COAL) ? 1600 : contents.getBurnTime(RecipeType.SMELTING);
                    }

                    if (fuel + itemValue <= maxFuel && stored >= 1) {
                        fuel += itemValue;
                        contents.setCount(stored - 1);
                    }

                }
            }
        }

        fuelTimer = (fuelTimer + 1) % 60;
    }

    private void heatTick() {
        if (heatTImer == 0) {
            if (fuel == 0) {
                updateActiveBlocks(false);
                heat -= (int) Math.ceil((double) heat / 100);

                return;
            }

            updateActiveBlocks(true);
            if (heat <= maxHeat) {
                int diff = maxHeat - heat;
                heat += (int) Math.ceil((double) diff / 100);
                heat = Math.min(heat, maxHeat);

                fuel -= (int) Math.ceil((double) heat / 100);
                fuel = Math.max(fuel, 0);
            }
        }

        heatTImer = (heatTImer + 1) % 5;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        fuelHatchSubscription.updateSubscription();
        if (!isRemote()) {
            tickSubscription = this.subscribeServerTick(this::heatTick);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        fuelHatchSubscription.updateSubscription();
        fuelTimer = 0;
        if (!isRemote()) {
            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        var screen = new DraggableScrollableWidgetGroup(7, 4, 182, 121).setBackground(getScreenTexture());
        screen.addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()));
        screen.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .setMaxWidthLimit(150)
                .clickHandler(this::handleDisplayClick));
        return new ModularUI(196, 216, this, entityPlayer)
                .background(GuiTextures.BACKGROUND_STEAM.get(false))
                .widget(screen)
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(),
                        GuiTextures.SLOT_STEAM.get(true), 7, 134,
                        true));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);
        textList.add(Component.literal("Fuel: " + fuel + "/" + maxFuel));
        textList.add(Component.literal("Heat: " + (heat + 295) + "K/" + (maxHeat + 295) + "K"));
    }
}
