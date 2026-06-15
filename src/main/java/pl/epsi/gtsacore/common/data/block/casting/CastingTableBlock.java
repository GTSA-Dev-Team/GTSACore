package pl.epsi.gtsacore.common.data.block.casting;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;

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

    public void tryGiveBack(Player player, CastingTableBlockEntity be) {
        if (be.getItem().is(Items.AIR)) return;
        player.addItem(be.getItem());
        be.setItem(ItemStack.EMPTY);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof CastingTableBlockEntity be) {
                ItemStack holding = player.getItemInHand(hand);

                if (holding.getItem() instanceof AbstractCastItem cast) {
                    tryGiveBack(player, be);
                    be.setItem(new ItemStack(cast));
                    holding.shrink(1);
                    return InteractionResult.sidedSuccess(false);
                }

                if (holding.is(Items.AIR)) {
                    tryGiveBack(player, be);
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

                ItemStack item = table.getItem();

                if (!item.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), item);
                }
            }

            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

}
