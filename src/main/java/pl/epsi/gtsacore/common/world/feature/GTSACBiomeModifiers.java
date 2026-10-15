package pl.epsi.gtsacore.common.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import pl.epsi.gtsacore.GTSubatomicCore;

public class GTSACBiomeModifiers {

    public static final ResourceKey<BiomeModifier> STONE_BLOB = ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS,
            GTSubatomicCore.id("stone_blob"));


    public static void bootstrap(BootstapContext<BiomeModifier> ctx) {
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placedFeatureRegistry = ctx.lookup(Registries.PLACED_FEATURE);

        HolderSet<Biome> overworld = biomeLookup.getOrThrow(BiomeTags.IS_OVERWORLD);
        Holder<PlacedFeature> limestoneBlob = placedFeatureRegistry.getOrThrow(GTSACPlacedFeatures.LIMESTONE_BLOB);

        ctx.register(STONE_BLOB, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworld,
                HolderSet.direct(limestoneBlob),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }
}
