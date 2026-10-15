package pl.epsi.gtsacore.common.world.feature;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import pl.epsi.gtsacore.GTSubatomicCore;

public class GTSACPlacedFeatures {
    public static final ResourceKey<PlacedFeature> LIMESTONE_BLOB = ResourceKey.create(Registries.PLACED_FEATURE, GTSubatomicCore.id("limestone_blob"));

    public static void bootstrap(BootstapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> featureLookup = ctx.lookup(Registries.CONFIGURED_FEATURE);
        HolderGetter<Biome> biomeLookup = ctx.lookup(Registries.BIOME);


        PlacementUtils.register(ctx, LIMESTONE_BLOB, featureLookup.getOrThrow(GTSACConfiguredFeatures.LIMESTONE_BLOB),
                RarityFilter.onAverageOnceEvery(30),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-8), VerticalAnchor.top()));
    }

}
