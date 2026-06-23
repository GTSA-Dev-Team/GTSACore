package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.data.item.casting.ICastingTableable;
import pl.epsi.gtsacore.common.data.item.casting.IronBloomItem;

import java.util.stream.Stream;

public class CastingTableBlock extends BaseEntityBlock {

    private static final VoxelShape SHAPE = Stream.of(
                    Block.box(0, 0, 0, 3, 12, 3),
                    Block.box(0, 0, 13, 3, 12, 16),
                    Block.box(13, 0, 0, 16, 12, 3),
                    Block.box(13, 0, 13, 16, 12, 16),
                    Block.box(0, 12, 0, 3, 16, 16),
                    Block.box(3, 12, 0, 13, 16, 3),
                    Block.box(3, 12, 3, 13, 15, 13),
                    Block.box(3, 12, 13, 13, 16, 16),
                    Block.box(13, 12, 0, 16, 16, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public CastingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return GTSACBlocks.CASTING_TABLE_BE.get().create(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> ((CastingTableBlockEntity) be).serverTick();
    }

    public void tryGiveBack(Player player, CastingTableBlockEntity be) {
        if (be.getMoldItem().is(Items.AIR)) return;
        player.addItem(be.getMoldItem());
        be.setMoldItem(ItemStack.EMPTY);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof CastingTableBlockEntity be) {
                ItemStack holding = player.getItemInHand(hand);
                if (be.getReturnItem() != null && !be.getReturnItem().isEmpty()) {
                    player.addItem(be.getReturnItem());
                    be.takeOutReturnItem();
                    int used = AbstractCastItem.onUsed(level.random, be.getMoldItem());
                    level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1f, 0.7f);
                    if (used == 1) {
                        be.setMoldItem(ItemStack.EMPTY);
                        level.playSound(null, pos, SoundEvents.ANVIL_DESTROY, SoundSource.BLOCKS, 1f, 1f);
                    } else if (used == 2) {
                        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 1f, 1f);
                    }
                    return InteractionResult.SUCCESS;
                }

                if (be.getCastingState() != CastingState.IDLE) return InteractionResult.SUCCESS;

                if (holding.getItem() instanceof ICastingTableable) {
                    if (holding.getItem() instanceof IronBloomItem && level.getBlockEntity(pos.above()) instanceof FaucetBlockEntity)
                        return InteractionResult.FAIL;
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1f, 1.25f);
                    tryGiveBack(player, be);
                    be.setMoldItem(holding.copyWithCount(1));
                    holding.shrink(1);
                    return InteractionResult.sidedSuccess(false);
                }

                if (holding.is(Items.AIR)) {
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1f, 1.1f);
                    tryGiveBack(player, be);
                }

                if (ToolHelper.is(holding, GTToolType.HARD_HAMMER)) {
                    be.usedHardHammer();
                }
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);

            if (be instanceof CastingTableBlockEntity table) {
                if (level.getBlockEntity(pos.above()) instanceof FaucetBlockEntity fe) {
                    fe.setCastingState(CastingState.IDLE, null);
                }

                ItemStack item = table.getMoldItem();

                if (!item.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
                ItemStack result = table.getReturnItem();
                if (!result.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
                }
                table.setMoldItem(ItemStack.EMPTY);
                table.setReturnItem(ItemStack.EMPTY);
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

}
