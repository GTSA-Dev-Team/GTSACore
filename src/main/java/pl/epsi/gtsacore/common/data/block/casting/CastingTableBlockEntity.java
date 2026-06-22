package pl.epsi.gtsacore.common.data.block.casting;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.GTSACVanillaRecipes;
import pl.epsi.gtsacore.common.data.recipes.CastingRecipe;

public class CastingTableBlockEntity extends BlockEntity {

    @Getter
    private ItemStack moldItem = ItemStack.EMPTY;
    @Getter
    private ItemStack returnItem = ItemStack.EMPTY;
    @Getter
    private ResourceLocation fluidID = null;
    @Getter
    private CastingState castingState = CastingState.IDLE;
    @Getter
    private long recipeStartTime;
    @Getter
    private int fillingTime, solidifyingTime;

    private CastingRecipe currentRecipe = null;
    @Getter
    private int progress = 0;

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

    @SuppressWarnings("all")
    public void startRecipe(FluidStack fluidStack) {
        if (!this.returnItem.isEmpty() || this.moldItem.isEmpty() || this.castingState != CastingState.IDLE) return;
        FluidStack fluid = fluidStack;

        CastingRecipe recipe = level.getRecipeManager()
                .getAllRecipesFor(GTSACVanillaRecipes.CASTING.get())
                .stream()
                .filter(r -> r.matches(fluid, this.moldItem))
                .findFirst()
                .orElse(null);

        if (recipe == null) return;

        this.currentRecipe = recipe;
        this.progress = 0;

        ResourceLocation fluidID = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        if (fluidID == null) return;

        fluidStack.shrink(recipe.getFluidStack().getAmount());

        this.setFluid(fluidID);
        this.recipeStartTime = level.getGameTime();
        this.fillingTime = recipe.getPourTicks();
        this.solidifyingTime = recipe.getSolidifyTicks();
        updateState(CastingState.FILLING);
        update();
    }

    public void updateState(CastingState state) {
        this.castingState = state;
        FaucetBlockEntity faucet = ((FaucetBlockEntity) this.getLevel().getBlockEntity(this.getBlockPos().above()));
        if (faucet != null) {
            faucet.setCastingState(state, fluidID);
        }
        update();
    }

    public void serverTick() {
        if (currentRecipe == null) return;
        progress++;
        update();

        if (progress > currentRecipe.getPourTicks() && castingState == CastingState.FILLING) {
            updateState(CastingState.SOLIDIFYING);
            progress = 0;
        }

        if (progress > currentRecipe.getSolidifyTicks() && castingState == CastingState.SOLIDIFYING) {
            updateState(CastingState.FINISHED);
            this.setReturnItem(currentRecipe.getResult());
            currentRecipe = null;
        }
    }

    public void takeOutReturnItem() {
        this.returnItem = ItemStack.EMPTY;
        this.setFluid(null);
        updateState(CastingState.IDLE);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        if (!moldItem.isEmpty()) {
            tag.put("moldItem", moldItem.save(new CompoundTag()));
        }

        if (returnItem != null && !returnItem.isEmpty()) {
            tag.put("returnItem", returnItem.save(new CompoundTag()));
        }

        if (fluidID != null) {
            tag.putString("fluidID", fluidID.toString());
        }

        tag.putInt("castingState", castingState.ordinal());
        tag.putInt("progress", progress);
        tag.putInt("fillingTicks", fillingTime);
        tag.putInt("solidifyingTick", solidifyingTime);
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

        if (tag.contains("progress")) {
            progress = tag.getInt("progress");
        }

        if (tag.contains("fillingTicks")) {
            fillingTime = tag.getInt("fillingTicks");
        }

        if (tag.contains("solidifyingTick")) {
            solidifyingTime = tag.getInt("solidifyingTick");
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
