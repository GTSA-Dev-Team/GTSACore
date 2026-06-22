package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.fluid.FluidTagMapIngredient;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;

import java.util.*;
import java.util.stream.Collectors;

public class CrucibleAssemblyBlockEntity extends BlockEntity {

    public static final int CAPACITY = 1008;
    private static final int RECIPE_CHECK_WAIT_TICKS = 20;

    private final Deque<FluidStack> fluids = new ArrayDeque<>();

    @Getter
    private ResourceLocation topFluidID;
    @Getter
    private FluidStack topFluidStack;
    @Getter
    private float percentFilled = 0;

    private GTRecipe currentRecipe, lastRecipe;

    @Getter
    private int progress;
    @Getter
    private int maxProgress;
    private int recipeCheckTimer;

    public boolean hasCampfire;

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

    public @Nullable GTRecipe findMatch() {
        if (fluids.isEmpty()) return null;

        List<List<AbstractMapIngredient>> ingredients = fluids.stream()
                .map(FluidTagMapIngredient::from)
                .filter(list -> !list.isEmpty())
                .collect(Collectors.toList());

        if (ingredients.isEmpty()) return null;

        return GTSACRecipeTypes.CRUCIBLE_ASSEMBLY_RECIPES.db().find(ingredients, recipe -> {
            for (Content content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
                FluidIngredient required = FluidRecipeCapability.CAP.of(content.getContent());
                FluidStack[] stacks = required.getStacks();
                if (stacks.length == 0) continue;
                if (getAmount(stacks[0].getFluid()) < required.getAmount()) return false;
            }
            return true;
        });
    }

    public void serverTick() {
        boolean hasCampfire = getLevel().getBlockEntity(getBlockPos().below()) instanceof CampfireBlockEntity;
        this.hasCampfire = hasCampfire;

        if (!hasCampfire) {
            progress = 0;
            return;
        }

        if (currentRecipe == null) {
            if (lastRecipe != null) {
                currentRecipe = findMatch();
                lastRecipe = null;
            } else {
                recipeCheckTimer++;

                if (recipeCheckTimer >= RECIPE_CHECK_WAIT_TICKS) {
                    recipeCheckTimer = 0;

                    currentRecipe = findMatch();

                    if (currentRecipe != null) {
                        progress = 0;
                    }
                }
            }
        }

        if (currentRecipe != null) {
            maxProgress = currentRecipe.duration;
            progress++;

            if (progress >= currentRecipe.duration) {
                for (Content content : currentRecipe.getInputContents(FluidRecipeCapability.CAP)) {
                    FluidIngredient required = FluidRecipeCapability.CAP.of(content.getContent());
                    FluidStack[] stacks = required.getStacks();
                    if (stacks.length > 0) {
                        removeFluid(stacks[0].getFluid(), required.getAmount());
                    }
                }

                for (Content content : currentRecipe.getOutputContents(FluidRecipeCapability.CAP)) {
                    FluidIngredient output = FluidRecipeCapability.CAP.of(content.getContent());
                    FluidStack[] stacks = output.getStacks();
                    if (stacks.length > 0) {
                        addFluid(stacks[0].copy());
                    }
                }

                lastRecipe = currentRecipe;
                currentRecipe = null;
                maxProgress = 0;
                progress = 0;
            }
        }
    }

    public void update() {
        if (!fluids.isEmpty()) {
            this.topFluidID = BuiltInRegistries.FLUID.getKey(fluids.peek().getFluid());
        } else {
            this.topFluidID = null;
        }
        this.percentFilled = (float) getTotalAmount() / CAPACITY;
        this.setChanged();
        this.topFluidStack = fluids.peek();
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

        ListTag fluidsTag = new ListTag();
        for (FluidStack stack : fluids) {
            fluidsTag.add(stack.writeToNBT(new CompoundTag()));
        }
        tag.put("fluids", fluidsTag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        fluids.clear();
        if (tag.contains("fluids", Tag.TAG_LIST)) {
            ListTag fluidsTag = tag.getList("fluids", Tag.TAG_COMPOUND);
            for (int i = 0; i < fluidsTag.size(); i++) {
                FluidStack stack = FluidStack.loadFluidStackFromNBT(fluidsTag.getCompound(i));
                if (!stack.isEmpty()) {
                    fluids.addLast(stack);
                }
            }
        }

        if (!fluids.isEmpty()) {
            this.topFluidID = BuiltInRegistries.FLUID.getKey(fluids.peek().getFluid());
            this.topFluidStack = fluids.peek();
        } else {
            this.topFluidID = null;
            this.topFluidStack = null;
        }
        this.percentFilled = (float) getTotalAmount() / CAPACITY;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        tag.putFloat("percentFilled", percentFilled);
        if (topFluidID != null) {
            tag.putString("topFluidID", topFluidID.toString());
        }
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
        if (tag.contains("percentFilled")) {
            this.percentFilled = tag.getFloat("percentFilled");
        }
        if (tag.contains("topFluidID")) {
            this.topFluidID = ResourceLocation.parse(tag.getString("topFluidID"));
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

}
