package pl.epsi.gtsacore.api.capability;

import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.machine.IHeatSubmissive;

public class GTSACCapabilityHelper {
    public GTSACCapabilityHelper() {}

    public static @Nullable IHeatSubmissive getHeatSubmissive(Level level, BlockPos pos, @Nullable Direction side) {
        return getBlockEntityCapability(GTSACCapability.CAPABILITY_HEAT_SUBMISSIVE, level, pos, side);
    }

    private static <T> @Nullable T getBlockEntityCapability(Capability<T> capability, Level level, BlockPos pos, @Nullable Direction side) {
        if (level.getBlockState(pos).hasBlockEntity()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                return blockEntity.getCapability(capability, side).resolve().orElse(null);
            }
        }

        return null;
    }
}
