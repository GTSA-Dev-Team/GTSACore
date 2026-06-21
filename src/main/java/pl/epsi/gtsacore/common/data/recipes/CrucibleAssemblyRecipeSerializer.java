package pl.epsi.gtsacore.common.data.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static pl.epsi.gtsacore.GTSubatomicCore.LOGGER;

public class CrucibleAssemblyRecipeSerializer implements RecipeSerializer<CrucibleAssemblyRecipe> {

    @Override
    public CrucibleAssemblyRecipe fromJson(ResourceLocation id, JsonObject json) {
        int duration = GsonHelper.getAsInt(json, "duration");
        FluidStack result = FluidStack.CODEC.parse(
                JsonOps.INSTANCE,
                GsonHelper.getAsJsonObject(json, "result")
        ).getOrThrow(false, LOGGER::error);

        ArrayList<FluidStack> inputs = new ArrayList<>();

        for (JsonElement jsonElement : GsonHelper.getAsJsonArray(json, "inputs").asList()) {
            inputs.add(FluidStack.CODEC.parse(
                    JsonOps.INSTANCE,
                    jsonElement
            ).getOrThrow(false, LOGGER::error));
        }
        return new CrucibleAssemblyRecipe(id, inputs, result, duration);
    }

    @Override
    public @Nullable CrucibleAssemblyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        int duration = buf.readInt();
        FluidStack result = buf.readFluidStack();
        List<FluidStack> inputs = buf.readList(FriendlyByteBuf::readFluidStack);

        return new CrucibleAssemblyRecipe(id, inputs, result, duration);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, CrucibleAssemblyRecipe recipe) {
        buf.writeInt(recipe.getDuration());
        buf.writeFluidStack(recipe.getResult());
        buf.writeCollection(
                recipe.getInputs(),
                FriendlyByteBuf::writeFluidStack
        );
    }

}
