package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;

import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.world.item.ItemStack;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;

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

    public static final GTRecipeType PRIMITIVE_SMELTER_RECIPES = registerFuelRecipeType("large_primitive_smelter", 2, 2, 0, 0);

    public static final GTRecipeType HEATING_RECIPES = registerFuelRecipeType("heating", 1, 0, 0, 0);


    public static final GTRecipeType TEST_FUEL_RECIPES = GTRecipeTypes
            .register("large_bonk_reactor", "multiblock")
            .setMaxIOSize(3, 3, 5, 4)
            .setMaxSize(IO.IN, FuelRecipeCapability.CAP, 1)
            .setEUIO(IO.IN);

    public static final GTRecipeType CRUCIBLE_ASSEMBLY_RECIPES = GTRecipeTypes
            .register("crucible_assembly", "dummy")
            .setMaxIOSize(0, 0, 4, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setIconSupplier(() -> new ItemStack(GTSACBlocks.CRUCIBLE_ASSEMBLY.get()));
    public static final GTRecipeType FOUNDRY_MELTING_RECIPES = GTRecipeTypes
            .register("foundry_melting", "multiblock")
            .setMaxIOSize(1, 0, 0, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARC_FURNACE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.FIRE);

    public static final GTRecipeType CASTING_RECIPES = GTRecipeTypes
            .register("casting", "dummy")
            .setMaxIOSize(1, 1, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setIconSupplier(() -> new ItemStack(GTSACBlocks.CASTING_TABLE.get()));
    public static final GTRecipeType FOUNDRY_ALLOYING_RECIPES = GTRecipeTypes
            .register("foundry_alloying", "multiblock")
            .setMaxIOSize(0, 0, 3, 2)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.JET_ENGINE);



    private static GTRecipeType registerFuelRecipeType(String name, int maxIInputs, int maxIOutputs, int maxFInputs, int maxFOutputs) {
        return GTRecipeTypes.register(name, "multiblock")
                .setMaxIOSize(maxIInputs, maxIOutputs, maxFInputs, maxFOutputs)
                .setMaxSize(IO.IN, GTSACRecipeCapabilities.FUEL, 1)
                .setSteamProgressBar(GuiTextures.PROGRESS_BAR_BOILER_FUEL, ProgressTexture.FillDirection.DOWN_TO_UP)
                .addDataInfo(data -> {
                    if (data.contains("FU/t")) {
                        int fuelPerTick = data.getInt("FU/t");
                        int fuelUsed = fuelPerTick * data.getInt("duration");
                        String total = LocalizationUtils.format("emi_info.gtsac.fuel.1", fuelUsed) + "\n";
                        String usage = LocalizationUtils.format("emi_info.gtsac.fuel.2", fuelPerTick);
                        return total + usage;
                    } else {
                        return "";
                    }
                });

    }

    public static void init() {}


}
