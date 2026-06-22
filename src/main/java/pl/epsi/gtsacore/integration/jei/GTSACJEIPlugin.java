package pl.epsi.gtsacore.integration.jei;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;

import static pl.epsi.gtsacore.common.data.GTSACBlocks.CASTING_TABLE;
import static pl.epsi.gtsacore.common.data.GTSACBlocks.CRUCIBLE_ASSEMBLY;

@JeiPlugin
public class GTSACJEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN = GTSubatomicCore.id("jei_plugin");

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (GTCEu.Mods.isREILoaded() || GTCEu.Mods.isEMILoaded()) return;

        registration.addRecipeCatalyst(
                CASTING_TABLE.get(),
                GTRecipeJEICategory.TYPES.apply(GTSACRecipeTypes.CASTING_RECIPES.getCategory())
        );

        registration.addRecipeCatalyst(
                CRUCIBLE_ASSEMBLY.get(),
                GTRecipeJEICategory.TYPES.apply(GTSACRecipeTypes.CRUCIBLE_ASSEMBLY_RECIPES.getCategory())
        );
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN;
    }
}
