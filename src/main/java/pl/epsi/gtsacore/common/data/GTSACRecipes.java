package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;
import pl.epsi.gtsacore.api.ingredient.FuelIngredient;

import java.util.function.Consumer;

import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.*;

public class GTSACRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        PRIMITIVE_SMELTER_RECIPES.recipeBuilder("test")
                .inputItems(new ItemStack(Items.IRON_ORE))
                .outputItems(new ItemStack(Items.IRON_INGOT))
                .duration(30*20).save(provider);
        CLARIFIER_RECIPES.recipeBuilder("somethingsth")
                .inputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .inputFluids(GTMaterials.Radon, 1)
                .outputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .EUt(8)
                .duration(20 * 60).save(provider);

        TEST_FUEL_RECIPES.recipeBuilder(
                        GTCEu.id("test"))
                .inputItems(Items.STONE)
                .input(FuelRecipeCapability.CAP, new FuelIngredient(2))
                .outputItems(Items.COBBLESTONE)
                .duration(100)
                .EUt(8)
                .save(provider);

    }

}
