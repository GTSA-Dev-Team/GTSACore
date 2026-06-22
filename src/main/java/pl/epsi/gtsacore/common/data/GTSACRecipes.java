package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import pl.epsi.gtsacore.common.data.item.GTSACItems;

import java.util.function.Consumer;

import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.*;

public class GTSACRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        PRIMITIVE_SMELTER_RECIPES.recipeBuilder("test")
                .inputItems(new ItemStack(Items.IRON_ORE))
                .outputItems(new ItemStack(Items.IRON_INGOT))
                .duration(30*20)
                .save(provider);
        CLARIFIER_RECIPES.recipeBuilder("somethingsth")
                .inputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .inputFluids(GTMaterials.Radon, 1)
                .outputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .EUt(8)
                .duration(20 * 60)
                .save(provider);

        CRUCIBLE_ASSEMBLY_RECIPES.recipeBuilder("crucible_bronze_alloying")
                .inputFluids(GTMaterials.Copper, 432)
                .inputFluids(GTMaterials.Tin, 144)
                .outputFluids(GTMaterials.Bronze.getFluid(576))
                .duration(20)
                .save(provider);

        CASTING_RECIPES.recipeBuilder("bronze_casting_ingot")
                .inputFluids(GTMaterials.Bronze, 144)
                .notConsumable(GTSACItems.INGOT_MOLD)
                .outputItems(ChemicalHelper.getIngot(GTMaterials.Bronze, GTValues.M))
                .addData("pour_ticks", 40)
                .addData("solidify_ticks", 80)
                .duration(40 + 80)
                .save(provider);

    }

}
