package pl.epsi.gtsacore.integration.jade;

import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.block.IncompleteCraftingTableBlock;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class IncompleteCraftingTableBlockProvider implements IBlockComponentProvider {
    public static final IncompleteCraftingTableBlockProvider INSTANCE = new IncompleteCraftingTableBlockProvider();

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlock() instanceof IncompleteCraftingTableBlock ict) {
            int progress = ict.getProgress(blockAccessor.getBlockState()) + 1;

            iTooltip.add(Component.literal(progress + "/" + (IncompleteCraftingTableBlock.MAX_PROGRESS + 2) + Component.translatable("gtsac.jade.ict").getString()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return GTSubatomicCore.id("incomplete_crafting_table_provider");
    }

}
