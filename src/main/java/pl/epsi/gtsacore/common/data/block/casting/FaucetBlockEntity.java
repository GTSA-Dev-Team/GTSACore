package pl.epsi.gtsacore.common.data.block.casting;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FaucetBlockEntity extends BlockEntity {

    @Getter
    private CastingState castingState = CastingState.IDLE;
    @Getter
    private ResourceLocation fluidID;

    public FaucetBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setCastingState(CastingState cs, @Nullable ResourceLocation fluidID) {
        this.castingState = cs;
        this.fluidID = fluidID;
        this.update();
        if (cs != CastingState.FILLING) {
            if (getLevel().getBlockEntity(getBlockPos().relative(getBlockState().getValue(FaucetBlock.FACING))) instanceof CrucibleAssemblyBlockEntity crucible) {
                crucible.update();
            }
        }
    }

    public void update() {
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

        if (fluidID != null) {
            tag.putString("fluidID", fluidID.toString());
        }

        tag.putInt("castingState", castingState.ordinal());

    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidID = null;

        if (tag.contains("fluidID")) {
            fluidID = ResourceLocation.parse(tag.getString("fluidID"));
        }

        if (tag.contains("castingState")) {
            castingState = CastingState.values()[tag.getInt("castingState")];
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
