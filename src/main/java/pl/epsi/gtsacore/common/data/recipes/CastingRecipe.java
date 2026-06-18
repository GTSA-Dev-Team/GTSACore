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

public class CastingRecipe implements Recipe<Container> {

    private final ResourceLocation id;

    @Getter
    private final FluidStack fluidStack;
    @Getter
    private final ItemStack moldItem;
    private final ItemStack result;

    @Getter
    private final int pourTicks, solidifyTicks;

    public CastingRecipe(ResourceLocation id, FluidStack fluidStack, ItemStack moldItem, ItemStack result, int pourTicks, int solidifyTicks) {
        this.id = id;
        this.fluidStack = fluidStack;
        this.moldItem = moldItem;
        this.result = result;
        this.pourTicks = pourTicks;
        this.solidifyTicks = solidifyTicks;
    }


    public boolean matches(FluidStack stack, ItemStack moldItem) {
        return stack.getFluid() == fluidStack.getFluid()
                && stack.getAmount() >= fluidStack.getAmount()
                && moldItem.is(this.getMoldItem().getItem());
    }

    public ItemStack getResult() {
        return result.copy();
    }

    @Override
    public boolean matches(Container container, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return GTSACVanillaRecipes.CASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return GTSACVanillaRecipes.CASTING.get();
    }
}