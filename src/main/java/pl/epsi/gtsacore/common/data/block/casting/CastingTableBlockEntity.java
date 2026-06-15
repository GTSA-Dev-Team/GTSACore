package pl.epsi.gtsacore.common.data.block.casting;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;

public class CastingTableBlockEntity extends BlockEntity {

    @Getter
    private ItemStack item = ItemStack.EMPTY;

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setItem(AbstractCastItem item) {
        this.item = new ItemStack(item);
        this.setChanged();
        level.sendBlockUpdated(
                worldPosition,
                getBlockState(),
                getBlockState(),
                Block.UPDATE_ALL
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!item.isEmpty()) {
            tag.put("Item", item.save(new CompoundTag()));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("Item")) {
            item = ItemStack.of(
                    tag.getCompound("Item")
            );
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
