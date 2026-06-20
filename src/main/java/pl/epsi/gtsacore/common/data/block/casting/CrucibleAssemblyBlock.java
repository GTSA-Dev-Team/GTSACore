package pl.epsi.gtsacore.common.data.block.casting;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACBlocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static pl.epsi.gtsacore.util.SACUtils.rotateShape;

public class CrucibleAssemblyBlock extends BaseEntityBlock {

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(2, -2, 2, 14, 4, 3),
            Block.box(2, -2, 3, 3, 8, 13),
            Block.box(3, -2, 3, 13, 0, 13),
            Block.box(2, -2, 13, 13, 8, 14),
            Block.box(13, -2, 3, 14, 8, 14),
            Block.box(2, 4, 2, 5, 8, 3),
            Block.box(5, 4, 0, 6, 7, 3),
            Block.box(6, 4, 0, 10, 5, 3),
            Block.box(10, 4, 0, 11, 7, 3),
            Block.box(11, 4, 2, 14, 8, 3),
            Shapes.join(Block.box(23, -15, 7, 25, 14, 9), Block.box(21, -16, 5, 27, -15, 11), BooleanOp.OR),
            Shapes.join(Block.box(-9, -15, 7, -7, 14, 9), Block.box(-11, -16, 5, -5, -15, 11), BooleanOp.OR),
            Block.box(-7, 12, 7, 23, 14, 9),
            Shapes.join(Shapes.join(Block.box(1, 2, 7, 2, 11, 9), Block.box(1, 11, 6, 2, 15, 10), BooleanOp.OR), Shapes.join(Block.box(14, 2, 7, 15, 11, 9), Block.box(14, 11, 6, 15, 15, 10), BooleanOp.OR), BooleanOp.OR)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final Map<Direction, VoxelShape> SHAPES =
            Util.make(new EnumMap<>(Direction.class), map -> {
                VoxelShape shape = NORTH_SHAPE;

                map.put(Direction.NORTH, shape);

                shape = rotateShape(shape);
                map.put(Direction.EAST, shape);

                shape = rotateShape(shape);
                map.put(Direction.SOUTH, shape);

                shape = rotateShape(shape);
                map.put(Direction.WEST, shape);
            });

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public CrucibleAssemblyBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return GTSACBlocks.CRUCIBLE_ASSEMBLY_BE.get().create(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : (lvl, pos, st, be) -> ((CrucibleAssemblyBlockEntity) be).serverTick();
    }
}
