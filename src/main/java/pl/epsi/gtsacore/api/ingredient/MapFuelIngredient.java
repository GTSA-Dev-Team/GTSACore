package pl.epsi.gtsacore.api.ingredient;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;

import java.util.Collections;
import java.util.List;

public class MapFuelIngredient extends AbstractMapIngredient {
    public final FuelIngredient ingredient;

    public MapFuelIngredient(FuelIngredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    protected int hash() {
        return ingredient.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapFuelIngredient other)) return false;
        return other.ingredient.equals(this.ingredient);
    }

    @Override
    public String toString() {
        return "MapFuelIngredient{" + "fuel=" + ingredient + '}';
    }

    public static List<AbstractMapIngredient> convertToMapIngredient(FuelIngredient ingredient) {
        return Collections.singletonList(new MapFuelIngredient(ingredient));
    }
}
