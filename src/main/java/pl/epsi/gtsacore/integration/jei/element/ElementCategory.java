package pl.epsi.gtsacore.integration.jei.element;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.item.GTSACItems;

public class ElementCategory extends EmiRecipeCategory {

    public static final ElementCategory CATEGORY = new ElementCategory();

    public ElementCategory() {
        super(GTSubatomicCore.id("element_drawer"), EmiStack.of(GTSACItems.ZETA_FLUXON));
    }

    public static void registerDisplays(EmiRegistry registry) {
        var a = ElementRenderer.INSTANCE;

        for (Material mat : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            registry.addRecipe(new ElementRecipe(mat));
        }
    }

}
