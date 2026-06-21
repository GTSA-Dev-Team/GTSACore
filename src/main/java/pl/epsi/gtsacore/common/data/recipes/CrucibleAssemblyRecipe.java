package pl.epsi.gtsacore.common.data.recipes;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import pl.epsi.gtsacore.common.data.GTSACVanillaRecipes;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlockEntity;

import java.util.List;

public class CrucibleAssemblyRecipe implements Recipe<Container> {

    private final ResourceLocation id;

    @Getter
    private final List<FluidStack> inputs;
    @Getter
    private final FluidStack result;
    @Getter
    private final int duration;

    public CrucibleAssemblyRecipe(ResourceLocation id, List<FluidStack> inputs, FluidStack result, int duration) {
        this.id = id;
        this.inputs = inputs;
        this.result = result;
        this.duration = duration;
    }

    public boolean matches(CrucibleAssemblyBlockEntity be) {
        for (FluidStack input : inputs) {
            if (be.getAmount(input.getFluid()) < input.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public FluidStack getCraftingResult() {
        return result.copy();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GTSACVanillaRecipes.CRUCIBLE_ASM_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GTSACVanillaRecipes.CRUCIBLE_ASSEMBLY.get();
    }
}
