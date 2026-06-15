package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.CLARIFIER_RECIPES;
import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.PRIMITIVE_SMELTER_RECIPES;

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
    }

}
