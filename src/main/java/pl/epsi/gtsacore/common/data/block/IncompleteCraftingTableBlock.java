package pl.epsi.gtsacore.common.data.block;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.lowdragmc.lowdraglib.syncdata.managed.IManagedVar;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACBlocks;

public class IncompleteCraftingTableBlock extends Block {
    public static final int MAX_PROGRESS = 3;
    public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 3);

    public IncompleteCraftingTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(this.getProgressProperty(), 0));
    }

    public IntegerProperty getProgressProperty() {
        return PROGRESS;
    }

    public BlockState getStateForProgress(int progress) {
        return this.defaultBlockState().setValue(this.getProgressProperty(), progress);
    }

    public int getProgress(BlockState state) {
        return state.getValue(this.getProgressProperty());
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        int newProgress = this.getProgress(state) + 1;
        boolean progressFinished = newProgress > MAX_PROGRESS;

        if (ToolHelper.is(player.getItemInHand(hand), GTToolType.KNIFE) && level.getBlockState(pos.above()).is(Blocks.AIR)) {
            if (!level.isClientSide()) {
                BlockState toPlace = progressFinished ? Blocks.CRAFTING_TABLE.defaultBlockState() : this.getStateForProgress(newProgress);
                level.setBlock(pos, toPlace, 3);
                player.getItemInHand(hand).hurtAndBreak(5, player, p -> p.broadcastBreakEvent(hand));
                return InteractionResult.SUCCESS;

            }
            if (level.isClientSide()) {
                spawnWoodParticles(state, level, pos, progressFinished);
                if (!progressFinished) {
                    level.playLocalSound(pos, SoundEvents.WOOD_STEP, SoundSource.BLOCKS, 1, 1, false);
                } else {
                    level.playLocalSound(pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 10, 1, false);
                }
                return InteractionResult.SUCCESS;
            }
        }


        return super.use(state, level, pos, player, hand, hit);
    }

    private void spawnWoodParticles(BlockState state, Level level, BlockPos pos, boolean strong) {
        if (strong) {
            for (int i = 0; i < 4000; i++) {
                float vx = 2 * (float) (Math.random() - 0.5);
                float vy = 2 * (float) (Math.random() - 0.5);
                float vz = 2 * (float) (Math.random() - 0.5);
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 10000 * vx, 10000, 10000 * vz);
            }
        } else {
            for (int i = 0; i < 40; i++) {
                float vx = 2 * (float) (Math.random() - 0.5);
                float vy = 2 * (float) (Math.random() - 0.5);
                float vz = 2 * (float) (Math.random() - 0.5);
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, vx, vy, vz);
            }
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PROGRESS);
    }


}
