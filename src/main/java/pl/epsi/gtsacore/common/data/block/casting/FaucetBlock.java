package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACBlocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import static pl.epsi.gtsacore.util.SACUtils.rotateShape;

public class FaucetBlock extends BaseEntityBlock {

    private static final VoxelShape NORTH_SHAPE = Stream.of(
            Block.box(5, 4, 0, 11, 5, 7),
            Block.box(5, 5, 0, 6, 7, 7),
            Block.box(10, 5, 0, 11, 7, 7)
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

    public FaucetBlock(Properties properties) {
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
                .setValue(FACING, context.getHorizontalDirection());
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
        return GTSACBlocks.FAUCET_BE.get().create(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos hatchPos = pos.relative(state.getValue(FACING), 1);
            BlockPos castingTablePos = pos.below();

            if (level.getBlockEntity(hatchPos) instanceof IMachineBlockEntity mbe &&
                    mbe.getMetaMachine() instanceof FluidHatchPartMachine fluidHatch &&
                    level.getBlockEntity(castingTablePos) instanceof CastingTableBlockEntity castingTable) {
                castingTable.startRecipe(fluidHatch.tank.getFluidInTank(0));
            } else if (level.getBlockEntity(hatchPos) instanceof CrucibleAssemblyBlockEntity crucible &&
                    level.getBlockEntity(castingTablePos) instanceof CastingTableBlockEntity castingTable) {
                FluidStack stack = crucible.getTopFluidStack();
                if (stack != null) {
                    castingTable.startRecipe(stack);
                    crucible.update();
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
