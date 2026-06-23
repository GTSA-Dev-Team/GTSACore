package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;

import java.util.*;

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

    private GTRecipe currentRecipe = null;
    @Getter
    private int progress = 0;
    @Getter
    private int hammeringProgress = 0;

    public CastingTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void setMoldItem(ItemStack moldItem) {
        hammeringProgress = 0;
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

    public @Nullable GTRecipe findMatch(FluidStack fluid) {
        if (moldItem.isEmpty() || fluid.isEmpty()) return null;

        Map<RecipeCapability<?>, List<Object>> map = new HashMap<>();
        map.put(ItemRecipeCapability.CAP, List.of(moldItem));
        map.put(FluidRecipeCapability.CAP, List.of(fluid));

        return GTSACRecipeTypes.CASTING_RECIPES.db().find(map, recipe -> {
            for (Content content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
                FluidIngredient required = FluidRecipeCapability.CAP.of(content.getContent());
                FluidStack[] stacks = required.getStacks();
                if (stacks.length == 0) continue;
                if (fluid.getAmount() < required.getAmount()) return false;
            }

            for (Content content : recipe.getInputContents(ItemRecipeCapability.CAP)) {
                Ingredient required = ItemRecipeCapability.CAP.of(content.getContent());
                ItemStack[] stacks = required.getItems();
                if (stacks.length == 0) continue;
                if (Arrays.stream(stacks).anyMatch((s) -> !s.is(moldItem.getItem()))) return false;
            }
            return true;
        });
    }

    @SuppressWarnings("all")
    public void startRecipe(FluidStack fluidStack) {
        if (!this.returnItem.isEmpty() || this.moldItem.isEmpty() || this.castingState != CastingState.IDLE) return;
        FluidStack fluid = fluidStack;

        this.currentRecipe = findMatch(fluidStack);
        if (this.currentRecipe == null) return;
        this.progress = 0;

        ResourceLocation fluidID = ForgeRegistries.FLUIDS.getKey(fluid.getFluid());
        if (fluidID == null) return;

        for (Content content : currentRecipe.getInputContents(FluidRecipeCapability.CAP)) {
            FluidIngredient required = FluidRecipeCapability.CAP.of(content.getContent());
            FluidStack[] stacks = required.getStacks();
            if (stacks.length > 0) {
                fluidStack.shrink(stacks[0].getAmount());
            }
        }

        this.setFluid(fluidID);
        this.recipeStartTime = level.getGameTime();
        this.fillingTime = currentRecipe.data.getInt("pour_ticks");
        this.solidifyingTime = currentRecipe.data.getInt("solidify_ticks");
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

        if (progress > this.fillingTime && castingState == CastingState.FILLING) {
            updateState(CastingState.SOLIDIFYING);
            progress = 0;
        }

        if (progress > this.solidifyingTime && castingState == CastingState.SOLIDIFYING) {
            updateState(CastingState.FINISHED);
            for (Content content : currentRecipe.getOutputContents(ItemRecipeCapability.CAP)) {
                Ingredient output = ItemRecipeCapability.CAP.of(content.getContent());
                ItemStack[] stacks = output.getItems();
                if (stacks.length > 0) {
                    this.setReturnItem(stacks[0].copy());
                }
            }
            currentRecipe = null;
        }
    }

    public void usedHardHammer() {
        hammeringProgress++;

        if (hammeringProgress > 2) {
            hammeringProgress = 0;
            moldItem = ItemStack.EMPTY;
            var bp = getBlockPos();
            Containers.dropItemStack(level, bp.getX(), bp.getY() + 1, bp.getZ(),
                    ChemicalHelper.getIngot(GTMaterials.WroughtIron, GTValues.M * 2));
        }

        update();
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
        tag.putInt("hammeringProgress", hammeringProgress);
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

        if (tag.contains("hammeringProgress")) {
            hammeringProgress = tag.getInt("hammeringProgress");
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
