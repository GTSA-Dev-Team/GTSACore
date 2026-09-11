package pl.epsi.gtsacore.common.machine.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.api.recipes.NotifiableFuelHandler;

import javax.annotation.Nullable;
import java.util.List;

public class FuelHatchPartMachine extends TieredIOPartMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER;


    @Persisted
    public final NotifiableFuelHandler fuelHandler;
    @Persisted
    public final NotifiableItemStackHandler inventory;
    protected @Nullable ISubscription inventorySubs;
    protected @Nullable TickableSubscription consumeFuelSubs;

    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public FuelHatchPartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.inventory = this.createInventory();
        // On creation the NotifiableBonkHandler attaches itself to the machine
        this.fuelHandler = new NotifiableFuelHandler(this, io);
    }

    @Override
    protected InteractionResult onHardHammerClick(Player playerIn, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if(isRemote()) return InteractionResult.SUCCESS;
        consumeFuel();
        if(fuelHandler.addFuel(1, false)){
            playerIn.sendSystemMessage(Component.literal("Fuel! Total fuel stored: " + fuelHandler.getFuel()));
            return InteractionResult.CONSUME;
        }

        return super.onHardHammerClick(playerIn, hand, gridSide, hitResult);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level var2 = this.getLevel();
        if (var2 instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateInventorySubscription));
        }

        this.inventorySubs = this.inventory.addChangedListener(this::updateInventorySubscription);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.inventorySubs != null) {
            this.inventorySubs.unsubscribe();
            this.inventorySubs = null;
        }

    }

    @Override
    public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        tag.putInt("CustomFuelAmount", this.fuelHandler.getFuel());
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        if (tag.contains("CustomFuelAmount")) {
            this.fuelHandler.setFuel(tag.getInt("CustomFuelAmount"), false);
        }
    }

    protected void updateInventorySubscription() {
        if (this.isWorkingEnabled() && !this.inventory.isEmpty() && this.io.support(IO.IN)) {
            this.consumeFuelSubs = this.subscribeServerTick(this.consumeFuelSubs, this::consumeFuel);
        } else if (this.consumeFuelSubs != null) {
            this.consumeFuelSubs.unsubscribe();
            this.consumeFuelSubs = null;
        }
    }

    private void consumeFuel() {
        if (this.getOffsetTimer() % 5L == 0L) {
            if (this.isWorkingEnabled()) {

                ItemStack fuelStack = this.inventory.getStackInSlot(0);

                int itemValue = ForgeHooks.getBurnTime(new ItemStack(fuelStack.getItem()), RecipeType.SMELTING);

                if (fuelStack.getCount() > 0 && this.fuelHandler.addFuel(itemValue, true)) {
                    fuelStack.setCount(fuelStack.getCount() - 1);
                    this.fuelHandler.addFuel(itemValue, false);
                }

            }

            this.updateInventorySubscription();
        }
    }

    private NotifiableItemStackHandler createInventory() {
        return new NotifiableItemStackHandler(this, 1, IO.IN, IO.NONE);
    }

    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 18 + 16, 18 + 16);
        WidgetGroup container = new WidgetGroup(4, 4, 18 + 8, 18 + 8);
        if (this.io == IO.OUT) {
            return new Widget(1,2,3,4);
        }

        container.addWidget((new SlotWidget(this.inventory.storage, 0, 4, 4, true, this.io.support(IO.IN))).setBackgroundTexture(GuiTextures.SLOT).setIngredientIO(this.io == IO.IN ? IngredientIO.INPUT : IngredientIO.OUTPUT));
        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);
        return group;
    }

    @Override
    public void addMultiText(List<Component> textList) {
        super.addMultiText(textList);
        textList.add(Component.literal("Fuel: " + this.fuelHandler.getFuel() + "/" + NotifiableFuelHandler.MAX_FUEl));
    }

    static {
        MANAGED_FIELD_HOLDER = new ManagedFieldHolder(FuelHatchPartMachine.class, TieredIOPartMachine.MANAGED_FIELD_HOLDER);
    }



}
