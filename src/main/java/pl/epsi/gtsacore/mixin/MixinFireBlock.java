package pl.epsi.gtsacore.mixin;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class MixinFireBlock {

    @Inject(
            method = "tryCatchFire",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/world/level/Level.removeBlock(Lnet/minecraft/core/BlockPos;Z)Z",
                    shift = At.Shift.AFTER
            )
    )
    private void gtsac$blockRemovedByFire(Level level, BlockPos pos, int chance, RandomSource rs, int age, Direction direction, CallbackInfo ci, @Local BlockState currentState) {
        gtsac$logBurningCheck(level, pos, currentState, rs);
    }

    private void gtsac$logBurningCheck(Level level, BlockPos pos, BlockState state, RandomSource rs) {
        if (state.is(BlockTags.LOGS)) {
            if (
                    !level.getBlockState(pos.relative(Direction.NORTH)).isSolid() &&
                            !level.getBlockState(pos.relative(Direction.SOUTH)).isSolid() &&
                            !level.getBlockState(pos.relative(Direction.WEST)).isSolid() &&
                            !level.getBlockState(pos.relative(Direction.EAST)).isSolid() &&
                            !level.getBlockState(pos.below()).isSolid()
            ) {
                if (rs.nextFloat() >= 0.15) {
                    level.setBlock(pos, GTBlocks.BRITTLE_CHARCOAL.getDefaultState(), 3);
                }
            }
        }
    }

}
