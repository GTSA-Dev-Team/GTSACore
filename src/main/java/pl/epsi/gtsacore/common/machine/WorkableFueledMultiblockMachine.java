package pl.epsi.gtsacore.common.machine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.api.ingredient.FuelIngredient;
import pl.epsi.gtsacore.common.data.GTSACRecipeCapabilities;
import pl.epsi.gtsacore.common.machine.multiblock.LargePrimitiveSmelterMachine;

import java.util.List;

public class WorkableFueledMultiblockMachine extends WorkablePrimitiveMultiblockMachine {
    private FuelIngredient FUEL_STACK = new FuelIngredient(20);

    @Getter
    @Persisted
    @DescSynced
    private GTRecipe lastSavedRecipe = null;
    private int runningTimer = 0;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableFueledMultiblockMachine.class, WorkablePrimitiveMultiblockMachine.MANAGED_FIELD_HOLDER);

    public WorkableFueledMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    protected GTRecipe getFuelRecipe() {
        return GTRecipeBuilder.ofRaw().input(GTSACRecipeCapabilities.FUEL, FUEL_STACK).buildRawRecipe();
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) {
            return false;
        }
        if (recipe == null) {
            return false;
        }

        if (RecipeHelper.handleRecipeIO(this, this.getFuelRecipe(), IO.IN, this.recipeLogic.getChanceCaches()).isSuccess()) {
            lastSavedRecipe = recipe;
            return true;
        } else {
            RecipeLogic.putFailureReason(this, recipe, Component.literal("Not enough fuel! (Maybe feed it some Oh-How-Delicous Coal?)"));
            return false;
        }
    }

    @Override
    public boolean onWorking() {
        var recipe = getRecipeLogic().getLastRecipe();
        if (recipe != null && recipe.data.contains("fuel_per_tick")) {
            FUEL_STACK = new FuelIngredient(recipe.data.getInt("fuel_per_tick") * 20);
        }
        if (this.runningTimer % 20 == 0 && !RecipeHelper.handleRecipeIO(this, this.getFuelRecipe(), IO.IN, this.recipeLogic.getChanceCaches()).isSuccess()) {
            this.recipeLogic.setProgress(0);
        } else {
            ++this.runningTimer;
            if (this.runningTimer > 20000) {
                this.runningTimer %= 20000;
            }
        }
        return super.onWorking();
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe, int parallels) {
        if (machine instanceof WorkableFueledMultiblockMachine fueledMachine) {
            List<Content> itemOutput = recipe.getOutputContents(GTRecipeCapabilities.ITEM);
            List<Content> fluidOutput = recipe.getOutputContents(GTRecipeCapabilities.FLUID);
            //List<Content> itemOutput = recipe.getOutputContents(GTRecipeCapabilities.ITEM);
            int parallel = ParallelLogic.getParallelAmount(machine, recipe, parallels);

            if (!itemOutput.isEmpty() && RecipeHelper.matchRecipe(fueledMachine, fueledMachine.getFuelRecipe()).isSuccess()) {
                return ModifierFunction.builder()
                        .inputModifier(ContentModifier.multiplier(parallel))
                        .outputModifier(ContentModifier.multiplier(parallel))
                        .parallels(parallel)
                        .build();
            } else {
                return ModifierFunction.NULL;
            }
        } else {
            return RecipeModifier.nullWrongType(LargePrimitiveSmelterMachine.class, machine);
        }
    }
}
