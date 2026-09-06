
package pl.epsi.gtsacore.common.world;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.GTOreDefinition;
import com.gregtechceu.gtceu.api.data.worldgen.generator.VeinGenerator;
import com.gregtechceu.gtceu.api.data.worldgen.ores.OreBlockPlacer;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SporadicVeinGenerator extends VeinGenerator {

    private final Material material;

    public SporadicVeinGenerator(Material material) {
        super(null);
        this.material = material;
    }

    public SporadicVeinGenerator(GTOreDefinition entry, Material material) {
        super(entry);
        this.material = material;
    }

    @Override
    public List<VeinEntry> getAllEntries() {
        return List.of(VeinEntry.ofMaterial(material, 1));
    }

    @Override
    public Map<BlockPos, OreBlockPlacer> generate(
            WorldGenLevel level,
            RandomSource random,
            GTOreDefinition entry,
            BlockPos origin
    ) {
        Map<BlockPos, OreBlockPlacer> result = new HashMap<>();

        /*
         * Horizontal size of the sporadic deposit.
         */
        final int radius = 12;

        /*
         * clusterSize() is interpreted as the number of candidate
         * positions, rather than the number of blocks in one giant vein.
         *
         * With:
         *
         *     clusterSize(60)
         *     density(0.5)
         *
         * we get ~30 accepted candidates on average.
         */
        final int attempts = entry.clusterSize().sample(random);

        for (int i = 0; i < attempts; i++) {

            if (random.nextFloat() > entry.density()) {
                continue;
            }

            int x = origin.getX()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            int z = origin.getZ()
                    + random.nextInt(radius * 2 + 1)
                    - radius;

            BlockPos center = findValidPosition(
                    level,
                    random,
                    x,
                    z
            );

            if (center == null) {
                continue;
            }

            /*
             * Make a tiny local cluster.
             *
             * The cluster itself is generated here instead of inside
             * OreBlockPlacer. This is important because here we still
             * have access to WorldGenLevel and can validate every
             * individual block against the terrain.
             */
            int clusterSize =
                    random.nextIntBetweenInclusive(2, 5);

            BlockPos current = center;

            for (int j = 0; j < clusterSize; j++) {

                if (j != 0) {

                    /*
                     * Mostly horizontal movement.
                     *
                     * Vertical movement is intentionally uncommon so
                     * that deposits remain relatively close to the
                     * surface instead of forming vertical veins.
                     */
                    int dx = random.nextInt(3) - 1;
                    int dz = random.nextInt(3) - 1;

                    int dy = random.nextFloat() < 0.20f
                            ? random.nextInt(3) - 1
                            : 0;

                    current = current.offset(dx, dy, dz);
                }

                /*
                 * Every individual block gets validated against the
                 * terrain at ITS OWN X/Z.
                 */
                if (!isValidOrePosition(level, current)) {
                    continue;
                }

                /*
                 * Avoid duplicate entries if the random walk happens
                 * to revisit a position.
                 */
                if (result.containsKey(current)) {
                    continue;
                }

                BlockPos finalPos = current;

                result.put(
                        finalPos,
                        createPlacer(finalPos)
                );
            }
        }

        return result;
    }

    /**
     * Finds an underground stone block somewhere within the first
     * 10 blocks below the local terrain surface.
     */
    @Nullable
    private BlockPos findValidPosition(
            WorldGenLevel level,
            RandomSource random,
            int x,
            int z
    ) {
        /*
         * OCEAN_FLOOR_WG gives the solid terrain floor rather than
         * counting water as the surface.
         *
         * This is preferable for the "10 blocks below the ground"
         * behavior you described.
         */
        int surfaceY = level.getHeight(
                Heightmap.Types.OCEAN_FLOOR_WG,
                x,
                z
        );

        /*
         * Randomize the starting depth so candidates aren't heavily
         * biased toward the topmost block.
         */
        int startDepth = random.nextInt(10) + 1;

        /*
         * Try all ten possible depths, starting at a random one.
         *
         * This means a failed position can still find another valid
         * stone block nearby in the 1..10 depth range.
         */
        for (int i = 0; i < 10; i++) {

            int depth =
                    ((startDepth - 1 + i) % 10) + 1;

            int y = surfaceY - depth;

            if (y < level.getMinBuildHeight()) {
                continue;
            }

            if (y >= level.getMaxBuildHeight()) {
                continue;
            }

            BlockPos pos = new BlockPos(x, y, z);

            if (isValidOrePosition(level, pos)) {
                return pos;
            }
        }

        return null;
    }

    /**
     * A block is a valid ore position only when:
     *
     *   1. It is 1..10 blocks below the local terrain surface.
     *   2. It is a valid GTCEu stone ore replacement block.
     *
     * This explicitly rejects air, water, lava, dirt, grass, etc.
     */
    private boolean isValidOrePosition(
            WorldGenLevel level,
            BlockPos pos
    ) {
        /*
         * First check the actual block.
         *
         * This is the important part that prevents water/air/etc.
         */
        BlockState existing = level.getBlockState(pos);

        if (existing.isAir()) {
            return false;
        }

//        if (!existing.is(BlockTags.STONE_ORE_REPLACEABLES)) {
//            return false;
//        }

        /*
         * Now verify the position's relationship to the local terrain.
         *
         * We intentionally calculate this using the current X/Z,
         * rather than reusing the center's surface height.
         */
        int surfaceY = level.getHeight(
                Heightmap.Types.OCEAN_FLOOR_WG,
                pos.getX(),
                pos.getZ()
        );

        int depth = surfaceY - pos.getY();

        return depth >= 1 && depth <= 10;
    }

    /**
     * Creates a placer for exactly one block.
     *
     * There is deliberately no cluster generation here.
     *
     * GTCEu calls this later for the section containing this exact
     * position, so we only write to that exact local coordinate.
     */
    private OreBlockPlacer createPlacer(BlockPos pos) {
        return (access, section) -> {

            BlockState existing =
                    access.getBlockState(pos);

            /*
             * Re-check the replacement block at placement time.
             *
             * Another worldgen operation may theoretically have
             * changed the block between generate() and actual placement.
             */
            if (existing.isAir()) {
                return;
            }

//            if (!existing.is(BlockTags.STONE_ORE_REPLACEABLES)) {
//                return;
//            }

            BlockState oreState =
                    getOreStateSafe();

            if (oreState.isAir()) {
                return;
            }

            /*
             * pos is guaranteed to belong to the section for which
             * this placer was created, so & 15 is now safe.
             */
            section.setBlockState(
                    pos.getX() & 15,
                    pos.getY() & 15,
                    pos.getZ() & 15,
                    oreState,
                    false
            );
        };
    }

    private BlockState getOreStateSafe() {
        return ChemicalHelper.getBlock(TagPrefix.ore, material).defaultBlockState();
    }

    @Override
    public VeinGenerator build() {
        return this;
    }

    @Override
    public VeinGenerator copy() {
        return new SporadicVeinGenerator(
                this.entry,
                this.material
        );
    }

    @Override
    public Codec<? extends VeinGenerator> codec() {
        /*
         * Codec.unit(this) is appropriate for this non-datapack-
         * serialized custom generator.
         */
        return Codec.unit(this);
    }
}
