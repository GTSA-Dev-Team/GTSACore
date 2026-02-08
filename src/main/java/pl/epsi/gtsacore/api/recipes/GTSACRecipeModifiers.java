package pl.epsi.gtsacore.api.recipes;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import org.jetbrains.annotations.NotNull;

import static com.gregtechceu.gtceu.api.recipe.OverclockingLogic.NON_PERFECT_OVERCLOCK;

public class GTSACRecipeModifiers {

    public static @NotNull ModifierFunction electricArcFurnaceCoilOverclock(@NotNull MetaMachine machine,
                                                                            @NotNull GTRecipe recipe) {
        if (!(machine instanceof CoilWorkableElectricMultiblockMachine coilMachine)) {
            return RecipeModifier.nullWrongType(CoilWorkableElectricMultiblockMachine.class, machine);
        }

        int maxParallel = 2 * coilMachine.getCoilType().getTier();
        int parallels = ParallelLogic.getParallelAmount(machine, recipe, maxParallel);
        if (parallels == 0) return ModifierFunction.NULL;

        var ocModifier = NON_PERFECT_OVERCLOCK.getModifier(machine, recipe, coilMachine.getOverclockVoltage());
        var parallelModifier = ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(maxParallel))
                .parallels(maxParallel)
                .build();

        return ocModifier.andThen(parallelModifier);
    }
}
