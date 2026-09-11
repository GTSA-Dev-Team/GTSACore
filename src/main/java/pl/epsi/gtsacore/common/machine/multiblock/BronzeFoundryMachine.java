package pl.epsi.gtsacore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.*;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.RecipeDB;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.MapIngredientTypeManager;
import com.gregtechceu.gtceu.api.transfer.fluid.CustomFluidTank;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.GTSACMaterialRegistry;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;
import pl.epsi.gtsacore.common.data.materials.GTSACMaterials;
import pl.epsi.gtsacore.common.machine.IHeatDominant;
import pl.epsi.gtsacore.common.machine.IHeatSubmissive;
import pl.epsi.gtsacore.common.machine.WorkablePrimitiveMultiblockMachine;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class BronzeFoundryMachine extends WorkablePrimitiveMultiblockMachine implements IHeatSubmissive {
    private static final int maxHeat = 14230;

    @Nullable
    private IHeatDominant heatSource;

    @Persisted
    @DescSynced
    private FoundryFluidTank foundryTank;



    private int totalCapacity = 27000;

    @Getter
    private int heat = 0;

    public BronzeFoundryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);

        List<CustomFluidTank> storages = List.of(new CustomFluidTank(totalCapacity));
        this.foundryTank = new FoundryFluidTank(this, 8*144);

        //this.foundryTank.fillInternal(GTMaterials.Copper.getFluid(3), IFluidHandler.FluidAction.EXECUTE);
        //this.foundryTank.fillInternal(GTMaterials.Tin.getFluid(2352), IFluidHandler.FluidAction.EXECUTE);
    }



    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(BronzeFoundryMachine.class,
            WorkablePrimitiveMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new BronzeFoundryLogic(this);
    }

    @Override
    protected InteractionResult onHardHammerClick(Player playerIn, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if (playerIn.level().isClientSide()) return InteractionResult.FAIL;
        pour();
        System.out.println(this.foundryTank.drain(GTMaterials.Tin.getFluid(200), IFluidHandler.FluidAction.SIMULATE).getAmount());
        return super.onHardHammerClick(playerIn, hand, gridSide, hitResult);
    }

    @Override
    public BronzeFoundryLogic getRecipeLogic() {
        return (BronzeFoundryLogic) super.getRecipeLogic();
    }

    @Override
    public IHeatDominant getHeatSource() {
        return null;
    }

    @Override
    public void setHeatSource(@Nullable IHeatDominant source) {
        this.heatSource = source;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        ArrayList<String> fluidText = new ArrayList<>();
        for (FluidStack fluidStack : foundryTank.fluids) {
            fluidText.add(fluidStack.getDisplayName().toString());
        }
        textList.add(Component.literal(fluidText.toString()));
        super.addDisplayText(textList);
    }

    public void pour() {
        this.setRecipeType(GTSACRecipeTypes.FOUNDRY_ALLOYING_RECIPES);
    }





    public static class FoundryFluidTank extends MachineTrait implements IFluidHandler{
        protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(FoundryFluidTank.class);
        private int capacity;
        @Persisted
        @DescSynced
        @Getter
        private ArrayList<FluidStack> fluids = new ArrayList<>();

        public FoundryFluidTank(MetaMachine machine, int capacity) {
            super(machine);
            this.capacity = capacity;
        }

        @Override
        public void saveCustomPersistedData(@NotNull CompoundTag tag, boolean forDrop) {
            super.saveCustomPersistedData(tag, forDrop);
            tag.putInt("capacity", this.capacity);
            tag.putInt("list_size", this.fluids.size());
            for (int i = 0; i < this.fluids.size(); i++) {
                CompoundTag subTag = new CompoundTag();
                FluidStack fluidStack = fluids.get(i);
                ResourceLocation registryName = BuiltInRegistries.FLUID.getKey(fluidStack.getFluid());
                String idString = registryName.toString().replace("gtceu:", "");

                subTag.putInt("amount_" + Integer.toString(i), fluidStack.getAmount());
                subTag.putString("fluid_" + Integer.toString(i), idString);
                tag.put(String.valueOf(i), subTag);
            }
        }

        @Override
        public void loadCustomPersistedData(@NotNull CompoundTag tag) {
            super.loadCustomPersistedData(tag);
            this.capacity = tag.getInt("capacity");
            int listSize = tag.getInt("list_size");

            if (listSize > 0) {
                for (int i = 0; i < listSize; i++) {
                    CompoundTag subTag = tag.getCompound(String.valueOf(i));

                    int amount = subTag.getInt("amount_" + Integer.toString(i));
                    String matName = subTag.getString("fluid_" + Integer.toString(i));

                    fluids.add(GTMaterials.get(matName).getFluid(amount));

                }
            }

        }

        @Override
        public ManagedFieldHolder getFieldHolder() {
            return MANAGED_FIELD_HOLDER;
        }

        @Override
        public int getTanks() {
            return fluids.size();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int i) {
            return fluids.get(i);
        }

        @Override
        public int getTankCapacity(int i) {
            return fluids.get(i).getAmount();
        }

        @Override
        public boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
            return fluids.get(Math.min(i, fluids.size() - 1)).getFluid().isSame(fluidStack.getFluid());
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            int currTotal = getStored();
            int free = capacity - currTotal;

            int inserted = Math.min(resource.getAmount(), free);

            if (action == IFluidHandler.FluidAction.EXECUTE) {
                if (!hasFluid(resource)) {
                    fluids.add(new FluidStack(resource.getFluid(), inserted));
                } else {
                    int idx = getIndexOfFluid(resource.getFluid());
                    fluids.get(idx).setAmount(fluids.get(idx).getAmount() + inserted);
                }

            }

            return inserted;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
            int drained = 0;
            int toDrainIdx = 999999;

            for (FluidStack stackInTank : fluids) {
                if (stackInTank.isFluidEqual(fluidStack)) {
                    drained = Math.min(fluidStack.getAmount(), stackInTank.getAmount());

                    if (fluidAction == FluidAction.EXECUTE) {
                        toDrainIdx = getIndexOfFluid(stackInTank.getFluid());
                    }

                }

            }

            if (toDrainIdx != 999999) {
                FluidStack drainedStack = fluids.get(toDrainIdx);
                if (drainedStack.getAmount() - drained == 0) {
                    fluids.remove(drainedStack);
                } else {
                    drainedStack.setAmount(drainedStack.getAmount() - drained);
                }
            }

            return new FluidStack(fluidStack.getFluid(), drained);
        }

        @Override
        public @NotNull FluidStack drain(int i, FluidAction fluidAction) {
            return null;
        }

        public int getStored() {
            int sum = 0;
            for (FluidStack fluidStack : fluids) {
                sum += fluidStack.getAmount();
            }

            return sum;
        }

        public boolean hasFluid(FluidStack fluid) {
            for (FluidStack fluidStack : fluids) {
                if (fluidStack.isFluidEqual(fluid)) return true;
            }
            return false;
        }

        public boolean hasFluid(Fluid fluid) {
            for (FluidStack fluidStack : fluids) {
                if (fluidStack.getFluid().isSame(fluid)) return true;
            }
            return false;
        }

        public FluidStack getFluidStackOfFluid(Fluid fluid) {
            for (FluidStack fluidStack : fluids) {
                if (fluidStack.getFluid().isSame(fluid)) return fluidStack;
            }

            return FluidStack.EMPTY;
        }

        public int getIndexOfFluid(Fluid fluid) {
            for (FluidStack fluidStack : fluids) {
                if (fluidStack.getFluid().isSame(fluid)) return fluids.indexOf(fluidStack);
            }
            return 0;
        }

        public float getPercentageOfFluid(Fluid fluid) {
            if (!this.hasFluid(fluid)) return 0;
            return (float) this.getFluidStackOfFluid(fluid).getAmount() / this.getStored();
        }
    }

    private class BronzeFoundryLogic extends RecipeLogic {

        public @NotNull BronzeFoundryMachine getMachine() {
            return (BronzeFoundryMachine) super.getMachine();
        }

        public BronzeFoundryLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        public List<List<AbstractMapIngredient>> getList() {
            List<List<AbstractMapIngredient>> list = new ObjectArrayList<>(8);
            var cap = FluidRecipeCapability.CAP;
            var compressed = cap.compressIngredients(Arrays.asList(BronzeFoundryMachine.this.foundryTank.fluids.toArray()));
            for (var ingredient : compressed) {
                list.add(MapIngredientTypeManager.getFrom(ingredient, cap));
            }

            if (list.isEmpty()) return null;

            return list;
        }

        public RecipeDB.RecipeIterator getIter(GTRecipeType type, Predicate<GTRecipe> predicate) {
            var list = getList();
            if (list == null) return null;
            return new RecipeDB.RecipeIterator(type.db(), list, predicate);
        }

        public @NotNull Iterator<GTRecipe> searchRecipe(IRecipeCapabilityHolder holder, Predicate<GTRecipe> canHandle) {
            var recipeType = this.getMachine().getRecipeType();
            if (!holder.hasCapabilityProxies()) return Collections.emptyIterator();
            var iterator = getIter(recipeType, canHandle);
            if (iterator == null) {
                return Collections.emptyIterator();
            }
            boolean any = false;
            while (iterator.hasNext()) {
                GTRecipe recipe = iterator.next();
                if (recipe == null) continue;
                any = true;
                break;
            }

            if (any) {
                iterator.reset();
                return iterator;
            }

            for (GTRecipeType.ICustomRecipeLogic logic : recipeType.getCustomRecipeLogicRunners()) {
                GTRecipe recipe = logic.createCustomRecipe(holder);
                if (recipe != null && canHandle.test(recipe)) return Collections.singleton(recipe).iterator();
            }
            return Collections.emptyIterator();
        }

        @Override
        public @NotNull Iterator<GTRecipe> searchRecipe() {
            System.out.println("called func");

            return searchRecipe(this.getMachine(), r -> {
                System.out.println(r.recipeType.toString());
                for (Content content : r.getInputContents(FluidRecipeCapability.CAP)) {
                    FluidStack fluidStack = FluidRecipeCapability.CAP.of(content.getContent()).getStacks()[0];

                    if (this.getMachine().foundryTank.drain(fluidStack, IFluidHandler.FluidAction.SIMULATE).getAmount() != fluidStack.getAmount()) {
                        System.out.println(false);
                        return false;
                    }
                }
                System.out.println(true);
                return true;
            });
        }

        @Override
        protected ActionResult matchRecipe(GTRecipe recipe) {
            if (recipe.recipeType == GTSACRecipeTypes.FOUNDRY_MELTING_RECIPES) {
                return this.matchFoundryMeltingRecipe(recipe);
            }
            if (recipe.recipeType == GTSACRecipeTypes.FOUNDRY_ALLOYING_RECIPES) {
                return this.matchFoundryAlloyingRecipe(recipe);
            }
            return ActionResult.FAIL_NO_REASON;
        }

        @Override
        protected ActionResult handleRecipeIO(GTRecipe recipe, IO io) {
            if (recipe.recipeType == GTSACRecipeTypes.FOUNDRY_MELTING_RECIPES) {
                return this.handleFoundryMeltingRecipe(recipe, io);
            }
            if (recipe.recipeType == GTSACRecipeTypes.FOUNDRY_ALLOYING_RECIPES) {
                return this.handleFoundryAlloyingRecipe(recipe, io);
            }
            return ActionResult.FAIL_NO_REASON;
        }

        private ActionResult handleFoundryMeltingRecipe(GTRecipe recipe, IO io) {
            if (io != IO.OUT) {
                return super.handleRecipeIO(recipe, io);
            }

            var items = recipe.getOutputContents(ItemRecipeCapability.CAP);
            if (!items.isEmpty()) {
                Map<RecipeCapability<?>, List<Content>> out = Map.of(ItemRecipeCapability.CAP, items);
                RecipeHelper.handleRecipe(this.machine, recipe, io, out, chanceCaches, false, false);
            }

            if (applyTankOutput(recipe, IFluidHandler.FluidAction.EXECUTE)) {
                return ActionResult.SUCCESS;
            }

            return ActionResult.fail(Component.translatable("gtceu.recipe_logic.insufficient_out")
                    .append(": ")
                    .append(FluidRecipeCapability.CAP.getName()), FluidRecipeCapability.CAP, IO.OUT);
        }

        private ActionResult handleFoundryAlloyingRecipe(GTRecipe recipe, IO io) {
            FoundryFluidTank foundryTank =  this.getMachine().foundryTank;
            int storedAmountInTank = foundryTank.getStored();

            //Material outputMat = ChemicalHelper.getMaterial(FluidRecipeCapability.CAP.of(recipe.getOutputContents(FluidRecipeCapability.CAP).get(0).getContent()).getStacks()[0].getFluid());
            Material outputMat = ChemicalHelper.getMaterial(RecipeHelper.getOutputFluids(recipe).get(0).getFluid());
            GTRecipe alloyRecipe = GTRecipeBuilder.ofRaw().outputFluids(outputMat.getFluid(storedAmountInTank)).buildRawRecipe();
            GTRecipe slagRecipe = GTRecipeBuilder.ofRaw().outputFluids(GTSACMaterials.SLAG.getFluid(storedAmountInTank)).buildRawRecipe();

            System.out.println(outputMat.getName());

            if (io != IO.IN) {
                return super.handleRecipeIO(alloyRecipe, io);
            }

            List<FluidStack> inputFluids = RecipeHelper.getOutputFluids(recipe);
            int amountSum = inputFluids.stream().mapToInt(FluidStack::getAmount).sum();

            HashMap<Fluid, Float> tankFractionMap = new HashMap<>();
            HashMap<Fluid, Float> reqFractionMap = new HashMap<>();

            for (FluidStack fluidStack : inputFluids) {
                float tankFraction = foundryTank.getPercentageOfFluid(fluidStack.getFluid());
                float reqFraction = (float) fluidStack.getAmount() / amountSum;

                tankFractionMap.put(fluidStack.getFluid(), tankFraction);
                reqFractionMap.put(fluidStack.getFluid(), reqFraction);
            }

            for (Fluid fluid : reqFractionMap.keySet()) {
                if (Math.abs(tankFractionMap.get(fluid) - reqFractionMap.get(fluid)) > 0.05) {
                    return super.handleRecipeIO(slagRecipe, IO.OUT);
                }
            }

            return super.handleRecipeIO(alloyRecipe, IO.OUT);
        }

        private ActionResult matchFoundryMeltingRecipe(GTRecipe recipe) {
            ActionResult result = RecipeHelper.handleRecipe(this.machine, recipe, IO.IN, recipe.inputs, Collections.emptyMap(), false, true);

            if (!result.isSuccess()) {
                return result;
            } else {
                return !applyTankOutput(recipe, IFluidHandler.FluidAction.SIMULATE) ? ActionResult.fail(Component.literal("Insufficient tank space!"), FluidRecipeCapability.CAP, IO.OUT) : ActionResult.SUCCESS;
            }
        }

        private ActionResult matchFoundryAlloyingRecipe(GTRecipe recipe) {
            if (!recipe.getOutputContents(ItemRecipeCapability.CAP).isEmpty()) return ActionResult.FAIL_NO_REASON;
            ActionResult result = RecipeHelper.handleRecipe(this.machine, recipe, IO.OUT, recipe.outputs, Collections.emptyMap(), false, true);

            if (!result.isSuccess()) {
                return result;
            } else {
                return !applyTankInput(recipe, IFluidHandler.FluidAction.SIMULATE) ? ActionResult.fail(Component.literal("Not enough fluids!"), FluidRecipeCapability.CAP, IO.IN) : ActionResult.SUCCESS;
            }
        }

        private boolean applyTankInput(GTRecipe recipe, IFluidHandler.FluidAction action) {
            List<Content> contents = recipe.getInputContents(FluidRecipeCapability.CAP);

            if (contents.isEmpty()) {
                return true;
            }

            for (Content content : contents) {
                FluidIngredient ingredient = FluidRecipeCapability.CAP.of(content.getContent());
                FluidStack fluidStack = ingredient.getStacks()[0];

                if (!(getMachine().foundryTank.drain(fluidStack, action).getAmount() == fluidStack.getAmount())) return false;
            }
            return true;
        }

        private boolean applyTankOutput(GTRecipe recipe, IFluidHandler.FluidAction action) {
            List<Content> fluids = recipe.getOutputContents(FluidRecipeCapability.CAP);

            if (fluids.isEmpty()) {
                return true;
            }

            for (Content content : fluids) {
                FluidIngredient ingredient = FluidRecipeCapability.CAP.of(content.getContent());
                FluidStack fluid = ingredient.getStacks()[0];

                if (!(getMachine().foundryTank.fill(fluid, action) == fluid.getAmount())) {
                    return false;
                }
            }

            return true;
        }
    }
}
