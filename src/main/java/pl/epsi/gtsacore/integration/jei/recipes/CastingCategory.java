package pl.epsi.gtsacore.integration.jei.recipes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.recipes.CastingRecipe;

import java.util.List;

public class CastingCategory implements IRecipeCategory<CastingRecipe> {

    public static final RecipeType<CastingRecipe> TYPE =
            RecipeType.create(GTSubatomicCore.MOD_ID, "casting", CastingRecipe.class);

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated flame;

    public CastingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(120, 60);
        this.icon = guiHelper.createDrawableIngredient(
                VanillaTypes.ITEM_STACK,
                new ItemStack(GTSACBlocks.CASTING_TABLE.get())
        );

        this.arrow = guiHelper.createAnimatedRecipeArrow(200);
        this.flame = guiHelper.createAnimatedRecipeFlame(200);
    }

    @Override
    public RecipeType<CastingRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Casting");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    @SuppressWarnings("all")
    public @Nullable IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder,
                          CastingRecipe recipe,
                          IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 20, 20)
                .addItemStack(recipe.getMoldItem());

        builder.addSlot(RecipeIngredientRole.INPUT, 40, 20)
                .addIngredients(ForgeTypes.FLUID_STACK,
                        List.of(recipe.getFluidStack()));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 90, 20)
                .addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(CastingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 60, 20);
        this.flame.draw(guiGraphics, 91, 38);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(20, 17, 0);
        guiGraphics.pose().scale(0.70f, 0.70f, 0.70f);
        guiGraphics.drawString(Minecraft.getInstance().font, "NC", 0, 0, 0xFFFF0000, false);
        guiGraphics.pose().popPose();
    }
}
