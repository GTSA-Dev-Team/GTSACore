package pl.epsi.gtsacore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import lombok.Setter;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.epsi.gtsacore.api.machine.feature.multiblock.ICustomObjRendererMulti;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ClarifierMachine extends WorkableElectricMultiblockMachine implements IFluidRenderMulti, ICustomObjRendererMulti {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ClarifierMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Setter
    @Getter
    @DescSynced
    @RequireRerender
    private @NotNull Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    public ClarifierMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        IFluidRenderMulti.super.onStructureFormed();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        IFluidRenderMulti.super.onStructureInvalid();
    }

    @NotNull
    @Override
    public Set<BlockPos> saveOffsets() {
        Direction up = RelativeDirection.UP.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());

        Direction back = getFrontFacing().getOpposite();

        Direction right = RelativeDirection.RIGHT.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());

        BlockPos center = getPos()
                .relative(up)
                .relative(back)
                .relative(back, 4);

        Set<BlockPos> offsets = new HashSet<>();

        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if ((Math.abs(x) == 4) && (Math.abs(z) == 4))
                    continue;

                BlockPos p = center
                        .relative(right, x)
                        .relative(back, z);

                offsets.add(p.subtract(getPos()));
            }
        }
        return offsets;
    }

    @Override
    public Matrix4f getModelMatrix() {
        return getCenteredMatrix()
                .translate(0, 5, 0)
                .rotateY((float) (System.nanoTime() * 1e-9));
    }
}
