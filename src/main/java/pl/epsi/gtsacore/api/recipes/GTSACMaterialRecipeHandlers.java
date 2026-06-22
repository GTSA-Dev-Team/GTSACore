package pl.epsi.gtsacore.api.recipes;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.api.data.GTSACTagPrefix;

import java.util.function.Consumer;


public class GTSACMaterialRecipeHandlers {

    public static void init(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
        processLargeSpool(provider, material);
    }

    private static void processLargeSpool(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
        if (!material.hasProperty(PropertyKey.WIRE)) return;

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder("assemble_" + material.getName() + "_to_large_spool")
                .inputItems(ItemStack.EMPTY)
                .inputItems(TagPrefix.wireGtDouble, material, 4)
                .inputItems(ItemStack.EMPTY)
                .inputItems(TagPrefix.wireGtDouble, material, 4)
                .inputItems(ItemStack.EMPTY)
                .inputItems(TagPrefix.wireGtDouble, material, 4)
                .inputItems(ItemStack.EMPTY)
                .inputItems(TagPrefix.wireGtDouble, material, 4)
                .outputItems(GTSACTagPrefix.largeSpool, material)
                .duration((int) (material.getMass() * 2))
                .EUt(GTValues.VA[GTValues.HV])
                .circuitMeta(3)
                .save(provider);

    }
}
