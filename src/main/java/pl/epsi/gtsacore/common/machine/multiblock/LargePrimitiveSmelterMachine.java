package pl.epsi.gtsacore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.machine.part.PrimitiveFuelHatchPartMachine;

import java.util.List;

public class LargePrimitiveSmelterMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler fuelHatchSubscription;

    public static final int maxParallels = 4;

    private static final int maxFuel = 70000;

    @Persisted
    @Getter
    private int fuel = 0;

    @Persisted
    @Getter
    private int timer = 0;

    @Getter
    @Persisted
    @DescSynced
    private GTRecipe lastSavedRecipe = null;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(LargePrimitiveSmelterMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);


    public LargePrimitiveSmelterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.fuelHatchSubscription = new ConditionalSubscriptionHandler(this, this::fuelHatchTick, () -> true);
    }

    private void fuelHatchTick() {
        if (timer == 0) {

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

        timer = (timer + 1) % 60;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        fuelHatchSubscription.updateSubscription();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        fuelHatchSubscription.updateSubscription();
        timer = 0;
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


        if (fuel > 0) {
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
        if (recipe != null) {

            if (!(fuel > 0)) {
                if (recipeLogic.getProgress() > 1) {
                    recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - 2));
                }
            } else {
                if (recipeLogic.getProgress() > 0) {
                    fuel -= 1;
                }
            }


        }
        return true;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.literal("Fuel: " + fuel + "/" + maxFuel));

    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        int parallel = ParallelLogic.getParallelAmount(machine, recipe, maxParallels);
        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallel))
                .outputModifier(ContentModifier.multiplier(parallel))
                .durationMultiplier(1)
                .parallels(parallel)
                .build();
    }
}
