package pl.epsi.gtsacore.api.ingredient.fuel;

import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

@Getter
public class FuelIngredient {

    public static final FuelIngredient EMPTY = new FuelIngredient(0);

    public static final Codec<FuelIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("fuel").forGetter(FuelIngredient::getFuel)
    ).apply(instance, FuelIngredient::new));

    private int fuel;

    public FuelIngredient(int fuel) {
        this.fuel = fuel;
    }

    public FuelIngredient copy(){
        return new FuelIngredient(fuel);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof FuelIngredient other)) return false;
        return this.fuel == other.fuel;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(fuel);
    }

    @Override
    public String toString() {
        return "FuelIngredient{fuel=" + fuel + "}";
    }

    public static final class Serializer implements IContentSerializer<FuelIngredient> {
        public static final FuelIngredient.Serializer INSTANCE = new FuelIngredient.Serializer();

        @Override
        public FuelIngredient of(Object o) {
            if (o instanceof Integer integer) {
                return new FuelIngredient(integer);
            } else if (o instanceof FuelIngredient fuelIngredient) {
                return fuelIngredient;
            }
            return null;
        }

        @Override
        public FuelIngredient defaultValue() {
            return EMPTY;
        }

        @Override
        public Class<FuelIngredient> contentClass() {
            return FuelIngredient.class;
        }

        @Override
        public Codec<FuelIngredient> codec() {
            return CODEC;
        }
    }
}
