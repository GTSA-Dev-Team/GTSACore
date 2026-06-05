package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.gregtechceu.gtceu.data.pack.GTDynamicDataPack;
import net.minecraft.data.recipes.FinishedRecipe;
import pl.epsi.gtsacore.api.renderer.machine.CustomObjDynamicMultiRenderer;
import pl.epsi.gtsacore.common.data.GTSACRecipes;

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
    }

    @Override
    public String addonModId() {
        return GTSubatomicCore.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTSACRecipes.init(provider);
    }

}
