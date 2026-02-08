package pl.epsi.gtsacore.api.machine.steam;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.widget.PredicatedImageWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.steam.SimpleSteamMachine;
import com.gregtechceu.gtceu.api.machine.steam.SteamMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

import com.google.common.collect.Tables;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FluidSimpleSteamMachine extends SimpleSteamMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FluidSimpleSteamMachine.class,
            SteamMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableFluidTank importFluids;
    @Persisted
    public final NotifiableFluidTank exportFluids;

    public FluidSimpleSteamMachine(IMachineBlockEntity holder, boolean isHighPressure, Object... args) {
        super(holder, isHighPressure, args);
        this.importFluids = createImportFluidHandler(args);
        this.exportFluids = createExportFluidHandler(args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    protected NotifiableFluidTank createImportFluidHandler(Object... args) {
        return new NotifiableFluidTank(this, getRecipeType().getMaxInputs(FluidRecipeCapability.CAP),
                4000, IO.IN);
    }

    protected NotifiableFluidTank createExportFluidHandler(Object... args) {
        return new NotifiableFluidTank(this, getRecipeType().getMaxOutputs(FluidRecipeCapability.CAP),
                4000, IO.OUT);
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        var storages = Tables.newCustomTable(new EnumMap<>(IO.class), LinkedHashMap<RecipeCapability<?>, Object>::new);
        storages.put(IO.IN, ItemRecipeCapability.CAP, importItems.storage);
        storages.put(IO.OUT, ItemRecipeCapability.CAP, exportItems.storage);
        storages.put(IO.IN, FluidRecipeCapability.CAP, importFluids);
        storages.put(IO.OUT, FluidRecipeCapability.CAP, exportFluids);

        var group = getRecipeType().getRecipeUI().createUITemplate(recipeLogic::getProgressPercent,
                storages,
                new CompoundTag(),
                Collections.emptyList(),
                true,
                isHighPressure);
        Position pos = new Position((Math.max(group.getSize().width + 4 + 8, 176) - 4 - group.getSize().width) / 2 + 4,
                32);
        group.setSelfPosition(pos);
        return new ModularUI(176, 166, this, entityPlayer)
                .background(GuiTextures.BACKGROUND_STEAM.get(isHighPressure))
                .widget(group)
                .widget(new LabelWidget(5, 5, getBlockState().getBlock().getDescriptionId()))
                .widget(new PredicatedImageWidget(pos.x + group.getSize().width / 2 - 9,
                        pos.y + group.getSize().height / 2 - 9, 18, 18,
                        GuiTextures.INDICATOR_NO_STEAM.get(isHighPressure))
                        .setPredicate(recipeLogic::isWaiting))
                .widget(UITemplate.bindPlayerInventory(entityPlayer.getInventory(),
                        GuiTextures.SLOT_STEAM.get(isHighPressure), 7, 84, true));
    }
}
