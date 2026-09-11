package pl.epsi.gtsacore.integration.jei;

import com.gregtechceu.gtceu.integration.emi.recipe.GTRecipeEMICategory;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;
import pl.epsi.gtsacore.integration.jei.element.ElementCategory;

@EmiEntrypoint
public class GTSACEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(ElementCategory.CATEGORY);

        emiRegistry.addWorkstation(GTRecipeEMICategory.CATEGORIES.apply(GTSACRecipeTypes.CASTING_RECIPES.getCategory()),
                EmiStack.of(GTSACBlocks.CASTING_TABLE));
        emiRegistry.addWorkstation(GTRecipeEMICategory.CATEGORIES.apply(GTSACRecipeTypes.CRUCIBLE_ASSEMBLY_RECIPES.getCategory()),
                EmiStack.of(GTSACBlocks.CRUCIBLE_ASSEMBLY));

        ElementCategory.registerDisplays(emiRegistry);
    }

}
