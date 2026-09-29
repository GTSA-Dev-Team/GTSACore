package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
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

        HEATING_RECIPES.recipeBuilder("firebox_heating_default")
                .notConsumable(new ItemStack(Items.FLINT))
                .addData("fuel_per_tick", 5)
                .addData("duration", 10*20)
                .duration(10*20).save(provider);

        createFoundryMeltingRecipe(provider, GTMaterials.Copper);
        createFoundryMeltingRecipe(provider, GTMaterials.Tin);
        createFoundryMeltingRecipe(provider, GTMaterials.Iron);
        createFoundryMeltingRecipe(provider, GTMaterials.Nickel);

        createFoundryMeltingRecipe(provider, GTMaterials.Iron, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Magnetite), GTMaterials.Iron.getFluid(144), GTSACMaterials.SLAG.getFluid(36));
        createFoundryMeltingRecipe(provider, GTMaterials.Iron, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Hematite), GTMaterials.Iron.getFluid(144), GTSACMaterials.SLAG.getFluid(36));
        createFoundryMeltingRecipe(provider, GTMaterials.Iron, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Pyrite), GTMaterials.Iron.getFluid(144), GTSACMaterials.SLAG.getFluid(36));

        createFoundryMeltingRecipe(provider, GTMaterials.Copper, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Chalcopyrite), GTMaterials.Copper.getFluid(144), GTSACMaterials.SLAG.getFluid(36));
        createFoundryMeltingRecipe(provider, GTMaterials.Copper, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Malachite), GTMaterials.Copper.getFluid(144), GTSACMaterials.SLAG.getFluid(36));

        createFoundryMeltingRecipe(provider, GTMaterials.Tin, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Cassiterite), GTMaterials.Tin.getFluid(144), GTSACMaterials.SLAG.getFluid(36));
        createFoundryMeltingRecipe(provider, GTMaterials.Tin, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.CassiteriteSand), GTMaterials.Tin.getFluid(144), GTSACMaterials.SLAG.getFluid(36));

        createFoundryMeltingRecipe(provider, GTMaterials.Nickel, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Garnierite), GTMaterials.Nickel.getFluid(144), GTSACMaterials.SLAG.getFluid(36));
        createFoundryMeltingRecipe(provider, GTMaterials.Nickel, ChemicalHelper.get(TagPrefix.crushed, GTMaterials.Pentlandite), GTMaterials.Nickel.getFluid(144), GTSACMaterials.SLAG.getFluid(36));

        createFoundryAlloyingRecipe(provider, GTMaterials.Bronze.getFluid(4), GTSACMaterials.SLAG.getFluid(4),
                GTMaterials.Copper.getFluid(3),
                GTMaterials.Tin.getFluid(1));

        createFoundryAlloyingRecipe(provider, GTMaterials.SolderingAlloy.getFluid(10), GTSACMaterials.SLAG.getFluid(10),
                GTMaterials.Tin.getFluid(6),
                GTMaterials.Lead.getFluid(3),
                GTMaterials.Antimony.getFluid(1));


    }

    private static void createFoundryMeltingRecipe(Consumer<FinishedRecipe> p, ItemStack input, FluidStack... output) {
        int baseDur = 3;

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(input.getItem());
        String idString = registryName.toString().replace(':', '_');

        Material inputMat = ChemicalHelper.getMaterialStack(input).material();
        int temp = inputMat.getFluid().getFluidType().getTemperature();
        float tempDurMulti = (float) temp / 500;

        FOUNDRY_MELTING_RECIPES.recipeBuilder(idString)
                .inputItems(input)
                .outputFluids(output)
                .duration((int) (20 * baseDur * tempDurMulti))
                .addCondition(new HeatCondition(temp  * 10))
                .save(p);

    }

    private static void createFoundryMeltingRecipe(Consumer<FinishedRecipe> p, Material refMat, ItemStack input, FluidStack... output) {
        int baseDur = 3;

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(input.getItem());
        String idString = registryName.toString().replace(':', '_');

        int temp = refMat.getFluid().getFluidType().getTemperature();
        float tempDurMulti = (float) temp / 500;

        FOUNDRY_MELTING_RECIPES.recipeBuilder(idString)
                .inputItems(input)
                .outputFluids(output)
                .duration((int) (20 * baseDur * tempDurMulti))
                .addCondition(new HeatCondition(temp * 10))
                .save(p);

    }

    private static void createFoundryMeltingRecipe(Consumer<FinishedRecipe> p, Material material) {
        ItemStack matIngot = ChemicalHelper.getIngot(material, GTValues.M);
        int baseDur = 3;

        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(matIngot.getItem());
        String idString = registryName.toString().replace(':', '_');

        int temp = material.getFluid().getFluidType().getTemperature();
        float tempDurMulti = (float) temp / 500;

        FOUNDRY_MELTING_RECIPES.recipeBuilder(idString)
                .inputItems(matIngot)
                .outputFluids(material.getFluid(144))
                .duration((int) (20 * baseDur * tempDurMulti))
                .addCondition(new HeatCondition(temp * 10))
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
