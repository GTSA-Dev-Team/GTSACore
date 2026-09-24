package pl.epsi.gtsacore.common.machine.multiblock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.syncdata.ISubscription;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.GTSACPartAbilities;
import pl.epsi.gtsacore.api.machine.feature.IHeatProvider;
import pl.epsi.gtsacore.api.machine.feature.IHeatReceiver;
import pl.epsi.gtsacore.common.machine.WorkableFueledMultiblockMachine;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

public class LargeBronzeFireboxMachine extends WorkableFueledMultiblockMachine implements IHeatProvider {

    private static final int MAX_HEAT = 21000;

    @Getter
    @Persisted
    @DescSynced
    private int heat = 0;
    private Collection<IHeatReceiver> heatTargets;

    protected ISubscription heatSubs;
    protected TickableSubscription dissipateHeatSubs;

    public LargeBronzeFireboxMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, true, args);
    }

    private void updateHeatSubs() {
        dissipateHeatSubs = subscribeServerTick(dissipateHeatSubs, this::dissipateHeat);
    }

    private void dissipateHeat() {
        if (getOffsetTimer() % 5 == 0) {
            heat -= (heat / 180);
            clampHeat();
            updateHeatSubs();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Level var2 = this.getLevel();
        if (var2 instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateHeatSubs));
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (this.heatSubs != null) {
            this.heatSubs.unsubscribe();
            this.heatSubs = null;
        }

    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        if (this.heatTargets != null) {
            this.heatTargets.forEach((target) -> target.setHeatSource(null));
            this.heatTargets = null;
        }

        Set<IHeatReceiver> receivers = this.getMultiblockState().getMatchContext().getOrCreate("heatSubmissive", Sets::newHashSet);

        this.heatTargets = ImmutableSet.copyOf(receivers);
        this.heatTargets.forEach((target) -> target.setHeatSource(this));
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.updateActiveBlocks(false);
        if (this.heatTargets != null) {
            this.heatTargets.forEach((target) -> target.setHeatSource(null));
            this.heatTargets = null;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("gtsac.machine.firebox.heat").append((heat / 10) + "K/" + (MAX_HEAT / 10) + "K") );
    }

    @Override
    public boolean onWorking() {
        heat = (int) Math.ceil (heat + (double) (MAX_HEAT - heat) / 100L);
        clampHeat();
        return super.onWorking();
    }

    public void clampHeat() {
        heat = Math.min(heat, MAX_HEAT);
        heat = Math.max(2930, heat);
    }

    @Override
    public int getHeatLevel() {
        return heat;
    }

    protected @NotNull TraceabilityPredicate innerPredicate() {
        return new TraceabilityPredicate(blockWorldState -> {
            Set<IHeatReceiver> targets = blockWorldState.getMatchContext().getOrCreate("heatSubmissive", Sets::newHashSet);
            BlockEntity blockEntity = blockWorldState.getTileEntity();

            if (blockEntity instanceof MetaMachineBlockEntity mbe) {
                if (mbe.getMetaMachine() instanceof IHeatReceiver receiver) {
                    targets.add(receiver);
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
                .aisle("BBBBB", "BFFFB", "BBBBB", "     ", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ", "     ")
                .aisle("BBBBB", "FFFFF", "B   B", "     ", "     ")
                .aisle("BBBBB", "BF@FB", "BBBBB", "     ", "     ")
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
