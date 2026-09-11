package pl.epsi.gtsacore.common.machine.multiblock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.api.capability.GTSACCapability;
import pl.epsi.gtsacore.api.capability.GTSACCapabilityHelper;
import pl.epsi.gtsacore.common.data.GTSACPartAbilities;
import pl.epsi.gtsacore.common.machine.IHeatDominant;
import pl.epsi.gtsacore.common.machine.IHeatSubmissive;
import pl.epsi.gtsacore.common.machine.WorkableFueledMultiblockMachine;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

public class LargeBronzeFireboxMachine extends WorkableFueledMultiblockMachine implements IHeatDominant {
    private static final int maxHeat = 14230;

    @Getter
    private int heat = 0;
    private Collection<IHeatSubmissive> heatTargets;


    public LargeBronzeFireboxMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(LargeBronzeFireboxMachine.class,
            WorkableFueledMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        if (this.heatTargets != null) {
            this.heatTargets.forEach((target) -> target.setHeatSource(null));
            this.heatTargets = null;
        }

        Set<IHeatSubmissive> receivers = this.getMultiblockState().getMatchContext().getOrCreate("heatSubmissive", Sets::newHashSet);

        this.heatTargets = ImmutableSet.copyOf(receivers);
        this.heatTargets.forEach((target) -> target.setHeatSource(this));
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureFormed();
        if (this.heatTargets != null) {
            this.heatTargets.forEach((target) -> target.setHeatSource(null));
            this.heatTargets = null;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.literal("Heat: " + (heat/10 + 295) + "K/" + (maxHeat/10 + 295) + "K"));
    }

    @Override
    public boolean onWorking() {
        heat = (int) Math.ceil (heat + (double) (maxHeat - heat) / 100L);
        heat = Math.min(heat, maxHeat);
        return super.onWorking();
    }

    @Override
    public int getHeatLevel() {
        return heat;
    }

    protected @NotNull TraceabilityPredicate innerPredicate() {
        return new TraceabilityPredicate(blockWorldState -> {
            Set<IHeatSubmissive> targets = blockWorldState.getMatchContext().getOrCreate("heatSubmissive", Sets::newHashSet);
            BlockEntity blockEntity = blockWorldState.getTileEntity();

            if (blockEntity != null && blockEntity instanceof MetaMachineBlockEntity mbe) {
                //IHeatSubmissive receiver = GTSACCapabilityHelper.getHeatSubmissive(blockWorldState.getWorld(), blockWorldState.getPos(),null);

                if (mbe.getMetaMachine() instanceof IHeatSubmissive reciever) {
                    targets.add(reciever);
                    System.out.println(blockEntity.getBlockPos() + "asdadasdffafasf");
                }
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
        return  FactoryBlockPattern.start()
                .aisle("BBBBB", "BFFFB", "BBBBB", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ")
                .aisle("BBBBB", "BF@FB", "BBBBB", "     ")
                .where(" ", this.innerPredicate())
                .where("@", Predicates.controller(Predicates.blocks(this.getDefinition().get())))
                .where("F", blocks(GTBlocks.FIREBOX_BRONZE.get()))
                .where("B", blocks(GTBlocks.CASING_BRONZE_BRICKS.get()).setMinGlobalLimited(15)
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                        .or(Predicates.abilities(GTSACPartAbilities.FUEL_HATCH).setExactLimit(1))
                )
                .build();
    }
}
