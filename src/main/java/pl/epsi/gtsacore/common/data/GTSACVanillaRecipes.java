package pl.epsi.gtsacore.common.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.recipes.CastingRecipe;
import pl.epsi.gtsacore.common.data.recipes.CastingRecipeSerializer;
import pl.epsi.gtsacore.common.data.recipes.CrucibleAssemblyRecipe;
import pl.epsi.gtsacore.common.data.recipes.CrucibleAssemblyRecipeSerializer;

public class GTSACVanillaRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, GTSubatomicCore.MOD_ID);

    public static final RegistryObject<RecipeSerializer<CastingRecipe>> CASTING_SERIALIZER =
            RECIPE_SERIALIZERS.register("casting", CastingRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<CrucibleAssemblyRecipe>> CRUCIBLE_ASM_SERIALIZER =
            RECIPE_SERIALIZERS.register("crucible_assembly", CrucibleAssemblyRecipeSerializer::new);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, GTSubatomicCore.MOD_ID);

    public static final RegistryObject<RecipeType<CastingRecipe>> CASTING =
            RECIPE_TYPES.register("casting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return GTSubatomicCore.MOD_ID + ":casting";
                }
            });

    public static final RegistryObject<RecipeType<CrucibleAssemblyRecipe>> CRUCIBLE_ASSEMBLY =
            RECIPE_TYPES.register("crucible_assembly", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return GTSubatomicCore.MOD_ID + ":crucible_assembly";
                }
            });

    public static void init(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }

}
