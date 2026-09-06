package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGenLayers;
import com.gregtechceu.gtceu.api.data.worldgen.generator.indicators.SurfaceIndicatorGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.ores.OreVeinUtil;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTOres;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.world.SporadicVeinGenerator;

import java.util.List;
import java.util.Set;

public class GTSACWorldGen {


    public static void init() {
        removeGtOres();

        sporadic(GTMaterials.Malachite);
        sporadic(GTMaterials.Chalcopyrite);

        sporadic(GTMaterials.Cassiterite);

        sporadic(GTMaterials.Magnetite);
        sporadic(GTMaterials.Hematite);

        sporadic(GTMaterials.Pentlandite);
        sporadic(GTMaterials.Garnierite);

        sporadic(GTMaterials.Sphalerite);

        sporadic(GTMaterials.Coal);
    }

    public static void removeGtOres() {
        Set<ResourceLocation> keys = Set.copyOf(GTRegistries.ORE_VEINS.keys());
        keys.forEach(GTRegistries.ORE_VEINS::remove);
    }

    public static void sporadic(Material mat) {
        var vein = GTOres.blankOreDefinition();
        vein.weight(1000);
        vein.clusterSize(60);
        vein.density(0.8f);
        vein.discardChanceOnAirExposure(0);

        vein.layer(WorldGenLayers.STONE);
        vein.dimensions(Set.of(
                ResourceKey.create(
                        Registries.DIMENSION,
                        ResourceLocation.parse("minecraft:overworld")
                )
        ));
        vein.biomes(
                OreVeinUtil.resolveBiomes(List.of("#minecraft:is_overworld"))
        );

        vein.heightRangeUniform(0, 256);

        vein.veinGenerator(
                new SporadicVeinGenerator(mat)
        );

        vein.surfaceIndicatorGenerator(gen -> {
            gen.surfaceRock(mat);
            gen.placement(SurfaceIndicatorGenerator.IndicatorPlacement.ABOVE);
            gen.density(0.5f);
            gen.radius(7);
        });

        vein.register(GTSubatomicCore.id("sporadic_" + mat.getName()));
    }

}
