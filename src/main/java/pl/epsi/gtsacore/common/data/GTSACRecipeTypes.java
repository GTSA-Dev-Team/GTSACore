package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeType;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;

import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.MULTIBLOCK;

public class GTSACRecipeTypes {

    public static final GTRecipeType CLARIFIER_RECIPES = GTRecipeTypes.register("clarifier", "multiblock")
            .setMaxIOSize(3, 3, 3, 3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType NEUTRALIZATION_TANK_RECIPES = GTRecipeTypes
            .register("neutralization", "multiblock")
            .setMaxIOSize(6, 6, 6, 6)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.BATH);

    public static final GTRecipeType PRIMITIVE_SMELTER_RECIPES = GTRecipeTypes
            .register("large_primitive_smelter", "multiblock")
            .setMaxIOSize(2, 2, 0, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType TEST_FUEL_RECIPES = GTRecipeTypes.register("test_fuel_recipes", "multiblock")
            .setMaxIOSize(2, 2, 2, 2)
            .setMaxSize(IO.IN, FuelRecipeCapability.CAP, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FURNACE);



    public static void init() {}


}
