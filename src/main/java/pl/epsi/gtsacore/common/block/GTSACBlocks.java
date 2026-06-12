package pl.epsi.gtsacore.common.block;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.GTSubatomicCore;

import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACBlocks {
    public static void init() {}

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

    public static final BlockEntry<Block> PRIMITIVE_BRICKS = registerSimpleBlock(
            "Primitive Bricks", "primitive_bricks", "primitive_bricks", BlockItem::new);
}
