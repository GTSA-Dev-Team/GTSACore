package pl.epsi.gtsacore.integration.jade;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

public class CrucibleAssemblyBlockProvider extends CapabilityBlockProvider<CrucibleAssemblyBlockEntity> {

    protected CrucibleAssemblyBlockProvider() {
        super(GTSubatomicCore.id("crucible_provider"));
    }

    @Override
    protected @Nullable CrucibleAssemblyBlockEntity getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getBlockEntity(pos) instanceof CrucibleAssemblyBlockEntity ce ? ce : null;
    }

    @Override
    protected void write(CompoundTag data, CrucibleAssemblyBlockEntity capability) {
        data.putInt("Progress", capability.getProgress());
        data.putInt("MaxProgress", capability.getMaxProgress());
        data.putBoolean("WorkingEnabled", capability.hasCampfire);
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor accessor, BlockEntity blockEntity, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof CrucibleAssemblyBlockEntity be) {
            tooltip.add(tooltip.getElementHelper().progress(
                    getProgress(be.getTotalAmount(), CrucibleAssemblyBlockEntity.CAPACITY),
                    Component.literal("%s / %s mB".formatted(be.getTotalAmount(), CrucibleAssemblyBlockEntity.CAPACITY)),
                    tooltip.getElementHelper().progressStyle().color(0xFF1269C7).textColor(-1),
                    Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                    true
            ));

            int maxProgress = capData.getInt("MaxProgress");
            int currentProgress = capData.getInt("Progress");
            if (maxProgress != 0) {
                Component text;
                if (maxProgress < 20) {
                    text = Component.translatable("gtceu.jade.progress_tick", currentProgress, maxProgress);
                } else {
                    text = Component.translatable("gtceu.jade.progress_sec", Math.round(currentProgress / 20.0F),
                            Math.round(maxProgress / 20.0F));
                }

                int color = capData.getBoolean("WorkingEnabled") ? 0xFF4CBB17 : 0xFFBB1C28;
                tooltip.add(
                        tooltip.getElementHelper().progress(
                                getProgress(currentProgress, maxProgress),
                                text,
                                tooltip.getElementHelper().progressStyle().color(color).textColor(-1),
                                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                                true));
            }
        }
    }
}
