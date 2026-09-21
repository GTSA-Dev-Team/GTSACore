package pl.epsi.gtsacore.common.machine.multiblock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.mojang.datafixers.util.Either;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;
import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlockEntity;
import pl.epsi.gtsacore.common.data.item.GTSACItems;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.machine.WorkablePrimitiveMultiblockMachine;
import pl.epsi.gtsacore.util.SACUtils;

import java.util.*;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

public class StrandCasterMachine extends WorkablePrimitiveMultiblockMachine {

    @Getter
    private Collection<CastingTableBlockEntity> castingTables = new HashSet<>();

    public StrandCasterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object @NotNull ... args) {
        return new StrandCasterLogic(this);
    }

    @Override
    public @NotNull StrandCasterLogic getRecipeLogic() {
        return (StrandCasterLogic) super.getRecipeLogic();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        this.castingTables = null;

        Set<CastingTableBlockEntity> tables = this.getMultiblockState().getMatchContext().getOrCreate("castingTable", Sets::newHashSet);

        this.castingTables = ImmutableSet.copyOf(tables);
    }

    @Override
    public void onStructureInvalid() {
        this.castingTables.forEach(castingTable -> castingTable.setOnRecipeFinished((c) -> {}));

        super.onStructureInvalid();

        this.castingTables = null;
    }

    protected @NotNull TraceabilityPredicate innerPredicate() {
        return new TraceabilityPredicate(blockWorldState -> {
            Set<CastingTableBlockEntity> targets = blockWorldState.getMatchContext().getOrCreate("castingTable", Sets::newHashSet);
            BlockEntity blockEntity = blockWorldState.getTileEntity();

            if (blockEntity instanceof CastingTableBlockEntity cbe) {
                targets.add(cbe);
                cbe.setOnRecipeFinished(getRecipeLogic()::finished);
            }

            return true;
        }, null) {
            public boolean isAny() {
                return true;
            }

            public boolean addCache() {
                return true;
            }
        };
    }

    @Override
    public BlockPattern getPattern() {
        return FactoryBlockPattern.start()
                .aisle("BOOOB", "BBBBB", " BHB ")
                .aisle("BDDDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BBBBB", "BB@BB", " BHB ")
                .where(" ", Predicates.any())
                .where("@", Predicates.controller(Predicates.blocks(this.getDefinition().get())))
                .where("B", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get()))
                .where("D", blocks(GTSACBlocks.CASING_BRONZE_DUCT.get()))
                .where("C", innerPredicate())
                .where("F", blocks(GTSACBlocks.FAUCET.get()))
                .where("P", blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                .where("H", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)))
                .where("O", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get())
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)))
                .where("P", blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                .build();
    }



    public class StrandCasterLogic extends RecipeLogic {

        public StrandCasterLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        public void finished(CastingTableBlockEntity be) {
            if (!(getMachine() instanceof StrandCasterMachine)) {
                return;
            }

            ItemStack output = be.getReturnItem().copy();

            if (tryOutputItem(output))
                be.takeOutReturnItem(be.getBlockPos());
        }

        private boolean tryOutputItem(ItemStack stack) {
            if (stack.isEmpty()) return true;

            var itemHandlers = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP);
            if (itemHandlers.isEmpty()) return false;

            ItemStack remainder = stack.copy();
            for (var handler : itemHandlers) {
                if (!(handler instanceof NotifiableItemStackHandler itemHandler)) continue;

                for (int slot = 0; slot < itemHandler.getSlots() && !remainder.isEmpty(); slot++) {
                    remainder = itemHandler.insertItemInternal(slot, remainder, false);
                }
                if (remainder.isEmpty()) return true;
            }

            return remainder.isEmpty();
        }

        @Override
        public void serverTick() {
            if (isSuspend()) return;

            if (!(getMachine() instanceof StrandCasterMachine multiblock)) {
                return;
            }

            Collection<CastingTableBlockEntity> castingTables = multiblock.getCastingTables();
            if (castingTables == null || castingTables.isEmpty()) {
                return;
            }

            var fluidHandlers = multiblock.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP);
            if (fluidHandlers.isEmpty()) {
                return;
            }

            var itemHandlers = multiblock.getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP);
            if (itemHandlers.isEmpty()) {
                return;
            }

            for (IRecipeHandler<?> handler : fluidHandlers) {
                if (!(handler instanceof NotifiableFluidTank fluidHandler)) continue;

                for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
                    FluidStack currentFluid = fluidHandler.getFluidInTank(tank);
                    if (currentFluid.isEmpty()) continue;

                    for (CastingTableBlockEntity table : castingTables) {
                        currentFluid = fluidHandler.getFluidInTank(tank);
                        if (currentFluid.isEmpty()) break;

                        FluidStack fluidToOffer = currentFluid.copy();
                        int initialAmount = fluidToOffer.getAmount();

                        table.startRecipe(fluidToOffer);

                        int consumedAmount = initialAmount - fluidToOffer.getAmount();
                        if (consumedAmount > 0) {
                            FluidStack drainStack = currentFluid.copy();
                            drainStack.setAmount(consumedAmount);
                            fluidHandler.drainInternal(drainStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                }
            }
        }

    }
}
