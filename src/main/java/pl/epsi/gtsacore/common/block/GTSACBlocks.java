package pl.epsi.gtsacore.common.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.GTSubatomicCore;

import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_CREATIVE_TAB;
import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACBlocks {
    public static void init() {}

    static {
        GTSAC_REGISTRATE.creativeModeTab(() -> GTSAC_CREATIVE_TAB);
    }

    private static @NotNull BlockEntry<Block> registerSimpleBlock(String name, String id, String texture,
                                                                  NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return GTSAC_REGISTRATE
                .block(id, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .strength(5.0f, 6.0f)
                        .requiresCorrectToolForDrops())
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeAll(ctx.getName(), GTSubatomicCore.id("block/" + texture))))
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    private static @NotNull BlockEntry<StairBlock> registerStairBlock(String name, String id, String texture,
                                                                  NonNullBiFunction<StairBlock, Item.Properties, ? extends BlockItem> func) {
        return GTSAC_REGISTRATE
                .block(id, p -> new StairBlock(
                        Blocks.IRON_BLOCK::defaultBlockState,
                        p))
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .strength(5.0f, 6.0f)
                        .requiresCorrectToolForDrops())
                .blockstate((ctx, prov) -> prov.stairsBlock(ctx.getEntry(),
                        GTSubatomicCore.id("block/" + texture), GTSubatomicCore.id("block/" + texture), GTSubatomicCore.id("block/" + texture)))
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    private static @NotNull BlockEntry<FenceBlock> registerFenceBlock(String name, String id, String texture,
                                                                      NonNullBiFunction<FenceBlock, Item.Properties, ? extends BlockItem> func) {
        return GTSAC_REGISTRATE
                .block(id, FenceBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .strength(5.0f, 6.0f)
                        .requiresCorrectToolForDrops())
                .blockstate((ctx, prov) -> prov.fenceBlock(ctx.getEntry(), GTSubatomicCore.id("block/" + texture)))
                .lang(name)
                .item(func)
                .model((ctx, prov) -> prov.fenceInventory(ctx.getName(), GTSubatomicCore.id("block/" + texture)))
                .build()
                .register();
    }

    private static @NotNull BlockEntry<SlabBlock> registerSlabBlock(String name, String id, String texture,
                                                                      NonNullBiFunction<SlabBlock, Item.Properties, ? extends BlockItem> func) {
        return GTSAC_REGISTRATE
                .block(id, SlabBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .strength(5.0f, 6.0f)
                        .requiresCorrectToolForDrops())
                .blockstate((ctx, prov) -> prov.slabBlock(ctx.getEntry(),
                        GTSubatomicCore.id("block/" + texture), GTSubatomicCore.id("block/" + texture), GTSubatomicCore.id("block/" + texture), GTSubatomicCore.id("block/" + texture)))
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static final BlockEntry<Block> PRIMITIVE_BRICKS = registerSimpleBlock(
            "Primitive Bricks", "primitive_bricks", "primitive_bricks", BlockItem::new);

    public static final BlockEntry<StairBlock> PRIMITIVE_BRICK_STAIRS = registerStairBlock(
            "Primitive Brick Stairs", "primitive_brick_stairs", "primitive_bricks", BlockItem::new);

    public static final BlockEntry<FenceBlock> PRIMITIVE_BRICK_FENCE = registerFenceBlock(
            "Primitive Brick Fence", "primitive_brick_fence", "primitive_bricks", BlockItem::new);

    public static final BlockEntry<SlabBlock> PRIMITIVE_BRICK_SLAB = registerSlabBlock(
            "Primitive Brick Slab", "primitive_brick_slab", "primitive_bricks", BlockItem::new);


}
