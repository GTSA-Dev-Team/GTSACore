package pl.epsi.gtsacore.integration.jade;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.block.casting.CastingState;
import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;

public class CastingTableBlockProvider extends CapabilityBlockProvider<CastingTableBlockEntity> {

    protected CastingTableBlockProvider() {
        super(GTSubatomicCore.id("casting_table_provider"));
    }

    @Override
    protected @Nullable CastingTableBlockEntity getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return level.getBlockEntity(pos) instanceof CastingTableBlockEntity ce ? ce : null;
    }

    @Override
    protected void write(CompoundTag data, CastingTableBlockEntity capability) {
        data.putInt("progress", capability.getProgress());
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor Accessor, BlockEntity blockEntity, IPluginConfig config) {
        if (Accessor.getBlockEntity() instanceof CastingTableBlockEntity be) {
            if (be.getHammeringProgress() != 0) {
                tooltip.add(tooltip.getElementHelper().progress(
                        getProgress(be.getHammeringProgress(), CastingTableBlockEntity.HAMMER_HIT_COUNT),
                        Component.literal("%s / %s Hits".formatted(be.getHammeringProgress(), CastingTableBlockEntity.HAMMER_HIT_COUNT)),
                        tooltip.getElementHelper().progressStyle().color(0xFF1269C7).textColor(-1),
                        Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                        true
                ));
            }

            int progress = capData.getInt("progress");
            CastingState cs = be.getCastingState();
            int color = 0xFF4CBB17;
            int maxProgress = -1;
            if (cs == CastingState.FILLING) {
                tooltip.add(Component.literal("Filling"));
                maxProgress = be.getFillingTime();
            } else if (cs == CastingState.SOLIDIFYING) {
                tooltip.add(Component.literal("Solidifying"));
                maxProgress = be.getSolidifyingTime();
            }

            if (maxProgress != -1) {
                Component text;
                if (maxProgress < 20) {
                    text = Component.translatable("gtceu.jade.progress_tick", progress, maxProgress);
                } else {
                    text = Component.translatable("gtceu.jade.progress_sec", Math.round(progress / 20.0F),
                            Math.round(maxProgress / 20.0F));
                }

                tooltip.add(
                        tooltip.getElementHelper().progress(
                                getProgress(progress, maxProgress),
                                text,
                                tooltip.getElementHelper().progressStyle().color(color).textColor(-1),
                                Util.make(BoxStyle.DEFAULT, style -> style.borderColor = 0xFF555555),
                                true));

            }
        }
    }

}
