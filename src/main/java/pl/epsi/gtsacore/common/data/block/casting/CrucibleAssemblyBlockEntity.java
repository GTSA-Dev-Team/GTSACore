package pl.epsi.gtsacore.common.data.block.casting;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CrucibleAssemblyBlockEntity extends BlockEntity {

    private static final int CAPACITY = 1008;

    private final Deque<FluidStack> fluids = new ArrayDeque<>();

    @Getter
    private ResourceLocation topFluidID;
    @Getter
    private float percentFilled = 0;

    public CrucibleAssemblyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public boolean addFluid(FluidStack added) {
        int current = getTotalAmount();

        if (current + added.getAmount() > CAPACITY) {
            return false;
        }

        FluidStack top = fluids.peek();

        if (top != null && top.isFluidEqual(added)) {
            top.grow(added.getAmount());
        } else {
            fluids.push(added.copy());
        }

        update();
        return true;
    }

    public int getTotalAmount() {
        int total = 0;

        for (FluidStack stack : fluids) {
            total += stack.getAmount();
        }

        return total;
    }

    public int getAmount(Fluid fluid) {
        int amount = 0;

        for (FluidStack stack : fluids) {
            if (stack.getFluid() == fluid) {
                amount += stack.getAmount();
            }
        }

        return amount;
    }

    public int removeFluid(Fluid fluid, int amount) {
        int remaining = amount;

        Iterator<FluidStack> iterator = fluids.iterator();

        while (iterator.hasNext() && remaining > 0) {
            FluidStack stack = iterator.next();

            if (stack.getFluid() != fluid) {
                continue;
            }

            int removed = Math.min(stack.getAmount(), remaining);

            stack.shrink(removed);

            remaining -= removed;

            if (stack.isEmpty()) {
                iterator.remove();
            }
        }

        update();
        return amount - remaining;
    }

    public void serverTick() {

    }

    public void update() {
        this.topFluidID = BuiltInRegistries.FLUID.getKey(fluids.peek().getFluid());;
        this.percentFilled = (float) getTotalAmount() / CAPACITY;
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

        tag.putFloat("percentFilled", percentFilled);
        tag.putString("topFluidID", topFluidID.toString());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("percentFilled")) {
            this.percentFilled = tag.getFloat("percentFilled");
        }

        if (tag.contains("topFluidID")) {
            this.topFluidID = ResourceLocation.parse(tag.getString("topFluidID"));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
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
