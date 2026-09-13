package pl.epsi.gtsacore.common.machine;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class WorkablePrimitiveMultiblockMachine extends WorkableMultiblockMachine implements IDisplayUIMachine {


    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkablePrimitiveMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    public WorkablePrimitiveMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText
                .builder(textList, this.isFormed())
                .setWorkingStatus(this.recipeLogic.isWorkingEnabled(), this.recipeLogic.isActive())
                .addWorkingStatusLine().addProgressLine(this.recipeLogic)
                .addRecipeFailReasonLine(this.recipeLogic)
                .addOutputLines(this.recipeLogic.getLastRecipe());
        this.getDefinition().getAdditionalDisplay().accept(this, textList);

        IDisplayUIMachine.super.addDisplayText(textList);
    }

    /*@Override
    public Widget createUI() {
        WidgetGroup group = new WidgetGroup(0, 0, 190, 125);
        group.addWidget((new DraggableScrollableWidgetGroup(4, 4, 182, 117))
                .setBackground(GuiTextures.PRIMITIVE_BACKGROUND)
                .addWidget(new LabelWidget(4, 5, this.self().getBlockState().getBlock().getDescriptionId()))
                .addWidget((new ComponentPanelWidget(4, 17, this::addDisplayText))
                        .textSupplier(this.getLevel().isClientSide ? null : this::addDisplayText)
                        .setMaxWidthLimit(200)
                        .clickHandler(this::handleDisplayClick)));
        group.setBackground(GuiTextures.PRIMITIVE_BACKGROUND);
        return group;
    }*/


    /*@Override
    public ModularUI createUI(Player entityPlayer) {
        return (new ModularUI(198, 208, this, entityPlayer)).widget(new FancyMachineUIWidget(this, 198, 208));
    }*/

    @Override
    public IGuiTexture getScreenTexture() {
        return GuiTextures.PRIMITIVE_BACKGROUND;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        var screen = new DraggableScrollableWidgetGroup(7, 4, 182, 121).setBackground(getScreenTexture());
        screen.addWidget(new LabelWidget(4, 5, self().getBlockState().getBlock().getDescriptionId()));
        screen.addWidget(new ComponentPanelWidget(4, 17, this::addDisplayText)
                .setMaxWidthLimit(150)
                .clickHandler(this::handleDisplayClick));
        return new ModularUI(196, 216, this, entityPlayer)
                .background(GuiTextures.PRIMITIVE_BACKGROUND)
                .widget(screen)
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(),
                        GuiTextures.SLOT_STEAM.get(true), 7, 134,
                        true));
    }
}
