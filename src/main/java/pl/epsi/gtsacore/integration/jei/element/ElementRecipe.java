package pl.epsi.gtsacore.integration.jei.element;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.GTSubatomicCore;

import java.util.List;

public class ElementRecipe implements EmiRecipe {

    private final Material mat;
    private final ElementWidget widget;

    public ElementRecipe(Material mat) {
        this.mat = mat;
        this.widget = new ElementWidget(new ElementInfo("carbon", 118, 14));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return ElementCategory.CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return GTSubatomicCore.id("element_recipe_" + mat.getName());
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of();
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of();
    }

    @Override
    public int getDisplayWidth() {
        return 176;
    }

    @Override
    public int getDisplayHeight() {
        return 166;
    }

    @Override
    public void addWidgets(WidgetHolder widgetHolder) {
        widgetHolder.add(widget);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }
}
