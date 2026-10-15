package pl.epsi.gtsacore.common.world.feature;

import com.gregtechceu.gtceu.common.data.GTFeatures;
import com.gregtechceu.gtceu.common.worldgen.feature.configurations.StoneBlobConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.GTSACBlocks;

public class GTSACConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> LIMESTONE_BLOB = ResourceKey.create(Registries.CONFIGURED_FEATURE, GTSubatomicCore.id("limestone_blob"));

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> ctx) {
        FeatureUtils.register(ctx, LIMESTONE_BLOB, GTFeatures.STONE_BLOB.get(),
                new StoneBlobConfiguration(
                        OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                                GTSACBlocks.LIMESTONE.getDefaultState()), UniformInt.of(20, 40)
                )
        );
    }
}
