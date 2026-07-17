package pl.epsi.gtsacore.api.ingredient.fuel;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.ICapabilityTrait;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.api.capability.FuelRecipeCapability;

import java.util.List;

public class NotifiableFuelHandler extends NotifiableRecipeHandlerTrait<FuelIngredient> implements ICapabilityTrait {

    @Getter
    public final IO handlerIO;
    @Getter
    public final IO capabilityIO;


    // TODO: RETURN TO THIS LATER AND ADD MAX FUEL
    @Getter
    private int maxFuel;
    @Getter
    private int fuel;

    public NotifiableFuelHandler(MetaMachine machine, IO io) {
        this(machine, io, io);
    }

    public NotifiableFuelHandler(MetaMachine machine, IO handlerIO, IO capabilityIO) {
        super(machine);
        this.handlerIO = handlerIO;
        this.capabilityIO = capabilityIO;
    }

    public boolean addFuel(int fuelToAdd, boolean simulate){
        if(fuelToAdd < 0) return false;
        if((long) fuelToAdd + (long) this.fuel > Integer.MAX_VALUE) return false;
        if(simulate) return true;
        fuel += fuelToAdd;
        this.notifyListeners();
        return true;
    }

    public boolean removeFuel(int fuelToRemove, boolean simulate){
        if(fuelToRemove < 0) return false;
        if(fuelToRemove > this.fuel) return false;
        if(simulate) return true;
        fuel -= fuelToRemove;
        this.notifyListeners();
        return true;
    }

    @Override
    public IO getHandlerIO() {
        return null;
    }

    @Override
    public List<FuelIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<FuelIngredient> left, boolean simulate) {
        for (int i = 0; i < left.size(); i++) {
            FuelIngredient fuelIngredient = left.get(i);
            if (fuel >= fuelIngredient.getFuel()) {
                if (!simulate) {
                    fuel -= fuelIngredient.getFuel();
                }
                left.remove(i);
                break;
            }
        }
        return left.isEmpty() ? null : left;
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(new FuelIngredient(fuel));
    }

    @Override
    public double getTotalContentAmount() {
        return 1;
    }

    @Override
    public RecipeCapability<FuelIngredient> getCapability() {
        return FuelRecipeCapability.CAP;
    }
}
