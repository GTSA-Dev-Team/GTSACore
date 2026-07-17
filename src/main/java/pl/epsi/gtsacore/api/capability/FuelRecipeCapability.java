package pl.epsi.gtsacore.api.capability;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pl.epsi.gtsacore.api.ingredient.fuel.FuelIngredient;
import pl.epsi.gtsacore.api.ingredient.fuel.MapFuelIngredient;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FuelRecipeCapability extends RecipeCapability<FuelIngredient> {

    public static final FuelRecipeCapability CAP = new FuelRecipeCapability();

    protected FuelRecipeCapability() {
        super("fuel", 0x777777, false, 5, FuelIngredient.Serializer.INSTANCE);
    }

    @Override
    public FuelIngredient copyInner(FuelIngredient content) {
        return content.copy();
    }

    @Override
    public @Nullable List<AbstractMapIngredient> getDefaultMapIngredient(Object ingredient) {
        List<AbstractMapIngredient> ingredients = new ObjectArrayList<>(1);
        if (ingredient instanceof FuelIngredient fuelIngredient) ingredients.add(new MapFuelIngredient(fuelIngredient));
        return ingredients;
    }

    @Override
    public List<Object> compressIngredients(@Unmodifiable Collection<Object> ingredients) {
        int fuelTotal = 0;
        for(Object ingredient : ingredients){
            if (ingredient instanceof FuelIngredient fuelIngredient){
                fuelTotal += fuelIngredient.getFuel();
            }
        }
        if(fuelTotal > 0){
            return new ObjectArrayList<>(Collections.singleton(new FuelIngredient(fuelTotal)));
        }
        return Collections.emptyList();
    }

    /*@Override
    public void addXEIInfo(WidgetGroup group, int xOffset, GTRecipe recipe, List<Content> contents, boolean perTick, boolean isInput, MutableInt yOffset) {
        for (var content : contents) {
            var bonkIngredient = FuelRecipeCapability.CAP.of(content);
            if(isInput){
                group.addWidget(new LabelWidget(3-xOffset, yOffset.addAndGet(10), "Fuel usage: " + bonkIngredient.getFuel() + "u/t"));
            }
            // Bonk output not supported for now
        }
    }*/
}
