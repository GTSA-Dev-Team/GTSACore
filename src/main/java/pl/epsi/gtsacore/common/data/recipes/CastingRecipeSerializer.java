package pl.epsi.gtsacore.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public class CastingRecipeSerializer implements RecipeSerializer<CastingRecipe> {

    @Override
    public CastingRecipe fromJson(ResourceLocation id, JsonObject json) {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(
                ResourceLocation.parse(GsonHelper.getAsString(json, "fluid")));

        int amount = GsonHelper.getAsInt(json, "amount");
        int pourTicks = GsonHelper.getAsInt(json, "pour_ticks");
        int solidifyTicks = GsonHelper.getAsInt(json, "solidify_ticks");

        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

        Item mold = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(GsonHelper.getAsString(json, "mold")));

        return new CastingRecipe(id, new FluidStack(fluid, amount), new ItemStack(mold, 1), result, pourTicks, solidifyTicks);
    }

    @Override
    public @Nullable CastingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        FluidStack fluid = buf.readFluidStack();
        ItemStack mold = buf.readItem();
        ItemStack result = buf.readItem();
        int pourTicks = buf.readInt();
        int solidifyTicks = buf.readInt();

        return new CastingRecipe(id, fluid, mold, result, pourTicks, solidifyTicks);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CastingRecipe recipe) {
        buf.writeFluidStack(recipe.getFluidStack());
        buf.writeItem(recipe.getMoldItem());
        buf.writeItem(recipe.getResult());
        buf.writeInt(recipe.getPourTicks());
        buf.writeInt(recipe.getSolidifyTicks());
    }
}
