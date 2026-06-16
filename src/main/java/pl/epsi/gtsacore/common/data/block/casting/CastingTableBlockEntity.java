package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;

public class CastingTableBlockEntity extends BlockEntity {

    @Getter
    private ItemStack moldItem = ItemStack.EMPTY;
    @Getter
    private ItemStack returnItem = ItemStack.EMPTY;
    @Getter
    private ResourceLocation fluidID = null;
    @Getter
    private CastingState castingState = CastingState.IDLE;

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setMoldItem(ItemStack moldItem) {
        this.moldItem = moldItem;
        update();
    }

    public void setReturnItem(ItemStack item) {
        this.returnItem = item;
        update();
    }

    public void setFluid(ResourceLocation fluid) {
        this.fluidID = fluid;
        update();
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

    public void startRecipe(FluidHatchPartMachine fluidHatch) {
        if (!this.returnItem.isEmpty() || this.moldItem.isEmpty()) return;
        FluidStack fluid = fluidHatch.tank.getFluidInTank(0);
        ResourceLocation fluidID = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());

        this.castingState = CastingState.FILLING;
        FaucetBlockEntity faucet = ((FaucetBlockEntity) this.getLevel().getBlockEntity(this.getBlockPos().above()));
        faucet.setCastingState(CastingState.FILLING, fluidID);

        this.setFluid(fluidID);
        if (fluidID == null) return;

        ResourceLocation itemName = ResourceLocation.fromNamespaceAndPath(fluidID.getNamespace(),
                fluidID.getPath() + "_" + ((AbstractCastItem) moldItem.getItem()).getItemSuffix());
        Item item = ForgeRegistries.ITEMS.getValue(itemName);
        if (item == null) return;

        setReturnItem(new ItemStack(item));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!moldItem.isEmpty()) {
            tag.put("moldItem", moldItem.save(new CompoundTag()));
        }

        if (!returnItem.isEmpty()) {
            tag.put("returnItem", returnItem.save(new CompoundTag()));
        }

        if (fluidID != null) {
            tag.putString("fluidID", fluidID.toString());
        }

        tag.putInt("castingState", castingState.ordinal());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        moldItem = ItemStack.EMPTY;
        returnItem = ItemStack.EMPTY;

        if (tag.contains("moldItem")) {
            moldItem = ItemStack.of(tag.getCompound("moldItem"));
        }

        if (tag.contains("returnItem")) {
            returnItem = ItemStack.of(tag.getCompound("returnItem"));
        }

        if (tag.contains("fluidID")) {
            fluidID = ResourceLocation.bySeparator(tag.getString("fluidID"), ':');
        }

        if (tag.contains("castingState")) {
            castingState = CastingState.values()[tag.getInt("castingState")];
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
