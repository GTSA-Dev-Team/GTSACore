package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;

public class GTSACRecipeCapabilities {
    public static final FuelRecipeCapability FUEL = FuelRecipeCapability.CAP;

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(FUEL.name, FUEL);
    }
}
