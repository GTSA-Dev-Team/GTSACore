package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.common.data.GTFluids;
import com.gregtechceu.gtceu.common.data.GTMaterialItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;
import pl.epsi.gtsacore.api.condition.HeatCondition;
import pl.epsi.gtsacore.api.ingredient.FuelIngredient;
import pl.epsi.gtsacore.common.data.materials.GTSACMaterials;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static pl.epsi.gtsacore.common.data.GTSACRecipeTypes.*;

public class GTSACRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        PRIMITIVE_SMELTER_RECIPES.recipeBuilder("test")
                .inputItems(new ItemStack(Items.IRON_ORE))
                .outputItems(new ItemStack(Items.IRON_INGOT))
                .addData("FU/t", 2)
                .addData("duration", 30*20)
                .duration(30*20).save(provider);
        CLARIFIER_RECIPES.recipeBuilder("somethingsth")
                .inputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .inputFluids(GTMaterials.Radon, 1)
                .outputItems(new ItemStack[]{ new ItemStack(Items.STICK) })
                .EUt(8)
                .duration(20 * 60).save(provider);

        HEATING_RECIPES.recipeBuilder("ski")
                .notConsumable(new ItemStack(Items.FLINT))
                .addData("FU/t", 1)
                .duration(10*20).save(provider);

        TEST_FUEL_RECIPES.recipeBuilder(
                        GTCEu.id("test"))
                .inputItems(Items.STONE)
                .input(FuelRecipeCapability.CAP, new FuelIngredient(2))
                .outputItems(Items.COBBLESTONE)
                .duration(100)
                .EUt(8)
                .save(provider);

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
