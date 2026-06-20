package pl.epsi.gtsacore.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class CrucibleAssemblyRecipeSerializer implements RecipeSerializer<CrucibleAssemblyRecipe> {

    @Override
    public CrucibleAssemblyRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
        return null;
    }

    @Override
    public @Nullable CrucibleAssemblyRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
        return null;
    }

    @Override
    public void toNetwork(FriendlyByteBuf friendlyByteBuf, CrucibleAssemblyRecipe crucibleAssemblyRecipe) {

    }

}
