package pl.epsi.gtsacore.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.GTSACVanillaRecipes;
import pl.epsi.gtsacore.common.data.recipes.CastingRecipe;
import pl.epsi.gtsacore.integration.jei.recipes.CastingCategory;

import java.util.List;

@JeiPlugin
public class GTSACJEIPlugin implements IModPlugin {

    private static final ResourceLocation ID = GTSubatomicCore.id("jei_plugin");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CastingCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager recipeManager =
                Minecraft.getInstance().level.getRecipeManager();

        List<CastingRecipe> recipes =
                recipeManager.getAllRecipesFor(GTSACVanillaRecipes.CASTING.get());

        registration.addRecipes(CastingCategory.TYPE, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(GTSACBlocks.CASTING_TABLE.get()),
                CastingCategory.TYPE
        );
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

}
