package pl.epsi.gtsacore.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlockEntity;

@Mixin(CampfireBlockEntity.class)
public class MixinCampfireBlockEntity {

    @Inject(method = "particleTick", at = @At(target = "net/minecraft/world/level/Level.random : Lnet/minecraft/util/RandomSource;", value = "FIELD", opcode = Opcodes.GETFIELD), cancellable = true)
    private static void gtsac$disableCampfireSmoke(Level level, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci) {
        if (level.getBlockEntity(pos.above()) instanceof CrucibleAssemblyBlockEntity) ci.cancel();
    }

}
