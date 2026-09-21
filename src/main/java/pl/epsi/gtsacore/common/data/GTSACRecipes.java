package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import pl.epsi.gtsacore.api.condition.HeatCondition;
import pl.epsi.gtsacore.common.data.item.GTSACItems;
import pl.epsi.gtsacore.common.data.materials.GTSACMaterials;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.*;

public class GTSACRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
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

        CASTING_RECIPES.recipeBuilder("tin_casting_ingot")
                .inputFluids(GTMaterials.Tin, 144)
                .notConsumable(GTSACItems.INGOT_MOLD)
                .outputItems(ChemicalHelper.getIngot(GTMaterials.Tin, GTValues.M))
                .addData("pour_ticks", 40)
                .addData("solidify_ticks", 80)
                .duration(40 + 80)
                .save(provider);

        HEATING_RECIPES.recipeBuilder("ski")
                .notConsumable(new ItemStack(Items.FLINT))
                .addData("FU/t", 1)
                .duration(10*20).save(provider);

        createFoundryMeltingRecipe(provider, new ItemStack(Items.COPPER_INGOT), GTMaterials.Copper.getFluid(144));
        createFoundryMeltingRecipe(provider, ChemicalHelper.getIngot(GTMaterials.Tin, GTValues.M), GTMaterials.Tin.getFluid(144));



        createFoundryAlloyingRecipe(provider, GTMaterials.Bronze.getFluid(4), GTSACMaterials.SLAG.getFluid(4),
                GTMaterials.Copper.getFluid(3),
                GTMaterials.Tin.getFluid(1));

        createFoundryAlloyingRecipe(provider, GTMaterials.SolderingAlloy.getFluid(10), GTSACMaterials.SLAG.getFluid(10),
                GTMaterials.Tin.getFluid(6),
                GTMaterials.Lead.getFluid(3),
                GTMaterials.Antimony.getFluid(1));


    }

    private static void createFoundryMeltingRecipe(Consumer<FinishedRecipe> p, ItemStack input, FluidStack... output) {
        int baseDur = 6;
        int itemDurMult = input.getCount();

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(input.getItem());
        String idString = registryName.toString().replace(':', '_');

        Material inputMat = ChemicalHelper.getMaterialStack(input).material();
        int temp = inputMat.getFluid().getFluidType().getTemperature();

        FOUNDRY_MELTING_RECIPES.recipeBuilder(idString)
                .inputItems(input)
                .outputFluids(output)
                .duration(20 * baseDur * itemDurMult)
                .addCondition(new HeatCondition((temp - 295) * 10))
                .save(p);

    }

    private static void createFoundryAlloyingRecipe(Consumer<FinishedRecipe> p, FluidStack output, FluidStack failOutput, FluidStack... inputs) {
        ResourceLocation registryName = BuiltInRegistries.FLUID.getKey(output.getFluid());
        String idString = registryName.toString().replace(':', '_');

        FOUNDRY_ALLOYING_RECIPES.recipeBuilder(idString)
                .inputFluids(inputs)
                .outputFluids(output, failOutput)
                .duration(4)
                .save(p);
    }

}
