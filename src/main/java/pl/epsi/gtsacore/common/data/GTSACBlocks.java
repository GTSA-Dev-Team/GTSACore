package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.models.GTModels;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.block.IncompleteCraftingTableBlock;
import pl.epsi.gtsacore.common.data.block.casting.*;
import pl.epsi.gtsacore.common.data.item.casting.IronBloomItem;

import java.util.function.Supplier;

import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_CREATIVE_TAB;
import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACBlocks {
    public static void init() {}

    static {
        GTSAC_REGISTRATE.creativeModeTab(() -> GTSAC_CREATIVE_TAB);
    }

    public static BlockEntry<Block> createCasingBlock(String name, ResourceLocation texture) {
        return createCasingBlock(name, Block::new, texture, () -> Blocks.IRON_BLOCK,
                () -> RenderType::cutoutMipped);
    }

    public static BlockEntry<Block> createCasingBlock(String name,
                                                      NonNullFunction<BlockBehaviour.Properties, Block> blockSupplier,
                                                      ResourceLocation texture,
                                                      NonNullSupplier<? extends Block> properties,
                                                      Supplier<Supplier<RenderType>> type) {
        return GTSAC_REGISTRATE.block(name, blockSupplier)
                .initialProperties(properties)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .addLayer(type)
                .exBlockstate(GTModels.cubeAllModel(texture))
                .tag(CustomTags.MINEABLE_WITH_CONFIG_VALID_PICKAXE_WRENCH)
                .item(BlockItem::new)
                .build()
                .register();
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

    private static @NotNull BlockEntry<Block> registerSimpleBlockProperties(String name, String id, String texture, Block block,
                                                                  NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return GTSAC_REGISTRATE
                .block(id, Block::new)
                .initialProperties(() -> block)
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

    public static final BlockEntry<Block> GTSA_BLOCK = registerSimpleBlock(
            "GregTech: Subatomic Age Block", "gtsa_block", "gtsa_logo", BlockItem::new);

    public static final BlockEntry<Block> BRONZE_PLATED_BRICKS = createCasingBlock("bronze_plated_bricks",
            GTSubatomicCore.id("block/casings/solid/bronze_plated_bricks"));

    public static final BlockEntry<Block> CASING_BRONZE_DUCT = createCasingBlock("bronze_duct_casing",
            GTSubatomicCore.id("block/casings/duct/machine_casing_duct_bronze"));

    public static final BlockEntry<Block> CERAMIC_BLOCK = registerSimpleBlockProperties(
            "Ceramic Block", "ceramic_block", "ceramic_block",Blocks.BRICKS, BlockItem::new);

    public static final BlockEntry<CastingTableBlock> CASTING_TABLE = GTSAC_REGISTRATE
            .block("casting_table", CastingTableBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .properties(p -> p.strength(5.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion())
            .blockstate((ctx, prov) -> {
                prov.simpleBlock(ctx.getEntry(), prov.models().getExistingFile(GTSubatomicCore.id("block/casting_table")));
            })
            .lang("Casting Table")
            .item(BlockItem::new).build()
            .register();
    public static final BlockEntityEntry<CastingTableBlockEntity> CASTING_TABLE_BE = GTSAC_REGISTRATE
            .blockEntity("casting_table", CastingTableBlockEntity::new)
            .validBlocks(CASTING_TABLE)
            .renderer(() -> ctx -> new CastingTableBlockEntityRenderer())
            .register();

    public static final BlockEntry<CrucibleAssemblyBlock> CRUCIBLE_ASSEMBLY = GTSAC_REGISTRATE
            .block("crucible_assembly", CrucibleAssemblyBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .properties(p -> p.strength(5.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion())
            .blockstate((ctx, prov) -> {
                prov.horizontalBlock(ctx.getEntry(), prov.models().getExistingFile(GTSubatomicCore.id("block/crucible_assembly")));
            })
            .lang("Crucible Assembly")
            .item(BlockItem::new).build()
            .register();

    public static final BlockEntityEntry<CrucibleAssemblyBlockEntity> CRUCIBLE_ASSEMBLY_BE = GTSAC_REGISTRATE
            .blockEntity("crucible_assembly", CrucibleAssemblyBlockEntity::new)
            .validBlocks(CRUCIBLE_ASSEMBLY)
            .renderer(() -> ctx -> new CrucibleAssemblyBlockEntityRenderer())
            .register();

    public static final BlockEntry<FaucetBlock> FAUCET = GTSAC_REGISTRATE
            .block("faucet", FaucetBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .properties(p -> p.strength(5.0f, 6.0f).requiresCorrectToolForDrops().noOcclusion())
            .blockstate((ctx, prov) -> {
                prov.horizontalBlock(ctx.getEntry(), prov.models().getExistingFile(GTSubatomicCore.id("block/faucet")));
            })
            .lang("Faucet")
            .item(BlockItem::new).build()
            .register();

    public static final BlockEntityEntry<FaucetBlockEntity> FAUCET_BE = GTSAC_REGISTRATE
            .blockEntity("faucet", FaucetBlockEntity::new)
            .validBlocks(FAUCET)
            .renderer(() -> ctx -> new FaucetBlockEntityRenderer())
            .register();

    public static final BlockEntry<IncompleteCraftingTableBlock> INCOMPLETE_CRAFTING_TABLE = GTSAC_REGISTRATE
            .block("incomplete_crafting_table", IncompleteCraftingTableBlock::new)
            .initialProperties(() -> Blocks.CRAFTING_TABLE)
            .tag(BlockTags.MINEABLE_WITH_AXE)
            .blockstate((ctx, prov) -> {
                var block = ctx.get();

                var models = new ModelFile[4];

                for (int i = 1; i <= 4; i++) {
                    models[i-1] = prov.models().cubeBottomTop(
                            "incomplete_crafting_table_" + i,
                            GTSubatomicCore.id("block/ict/ict_side_" + i),
                            GTSubatomicCore.id("block/ict/ict_bottom_" + i),
                            GTSubatomicCore.id("block/ict/ict_top_" + i)
                    );
                }

                prov.getVariantBuilder(block)
                        .forAllStates(state -> {
                            int stage = state.getValue(IncompleteCraftingTableBlock.PROGRESS);

                            return ConfiguredModel.builder()
                                    .modelFile(models[stage])
                                    .build();
                        });
            })
            .lang("Crafting Table [Incomplete]")
            .item(BlockItem::new)
            .model((ctx, prov) -> prov.withExistingParent(
                    ctx.getName(), GTSubatomicCore.id("block/incomplete_crafting_table_1"))).build()
            .register();

    public static final BlockEntry<Block> IRON_BLOOM = GTSAC_REGISTRATE
            .block("iron_bloom", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .properties(p -> p.strength(5.0f, 6.0f).requiresCorrectToolForDrops())
            .lang("Iron Bloom")
            .item(IronBloomItem::new).build()
            .register();

}
