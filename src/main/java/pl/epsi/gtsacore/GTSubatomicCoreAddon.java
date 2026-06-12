package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import pl.epsi.gtsacore.common.data.GTSACRecipes;
import pl.epsi.gtsacore.common.data.materials.GTSACElements;
import pl.epsi.gtsacore.common.data.materials.GTSACPeriodicTableMaterials;

import java.util.function.Consumer;

@GTAddon
public class GTSubatomicCoreAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTSubatomicCore.GTSAC_REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GTSubatomicCore.MOD_ID;
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTSACRecipes.init(provider);
    }

    @Override
    public void registerElements() {
        IGTAddon.super.registerElements();
        GTSACElements.init();
    }

}
