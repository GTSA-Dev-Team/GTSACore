package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.MapIngredientTypeManager;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import net.minecraft.data.recipes.FinishedRecipe;
import pl.epsi.gtsacore.api.data.GTSACMaterialIconType;
import pl.epsi.gtsacore.api.data.GTSACTagPrefix;
import pl.epsi.gtsacore.api.ingredient.FuelIngredient;
import pl.epsi.gtsacore.api.ingredient.MapFuelIngredient;
import pl.epsi.gtsacore.api.recipes.GTSACMaterialRecipeHandlers;
import pl.epsi.gtsacore.api.renderer.machine.CustomObjDynamicMultiRenderer;
import pl.epsi.gtsacore.common.data.GTSACRecipeCapabilities;
import pl.epsi.gtsacore.common.data.GTSACRecipes;
import pl.epsi.gtsacore.common.data.materials.GTSACElements;

import java.util.function.Consumer;

@GTAddon
public class GTSubatomicCoreAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTSubatomicCore.GTSAC_REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        DynamicRenderManager.register(GTSubatomicCore.id("obj_renderer"), CustomObjDynamicMultiRenderer.TYPE);
        MapIngredientTypeManager.registerMapIngredient(FuelIngredient.class, MapFuelIngredient::convertToMapIngredient);
    }

    @Override
    public String addonModId() {
        return GTSubatomicCore.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTSACRecipes.init(provider);

        for (var material : GTCEuAPI.materialManager.getRegisteredMaterials()) {
            GTSACMaterialRecipeHandlers.init(provider, material);
        }

    }

    @Override
    public void registerElements() {
        IGTAddon.super.registerElements();
        GTSACElements.init();
    }

    @Override
    public void registerTagPrefixes() {
        GTSACMaterialIconType.init();
        GTSACTagPrefix.initTagPrefixes();
    }

    @Override
    public void registerRecipeCapabilities() {
        GTSACRecipeCapabilities.init();

    }
}
