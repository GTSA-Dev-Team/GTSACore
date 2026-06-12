package pl.epsi.gtsacore.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import lombok.Setter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
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

    private static final int[][] D_OFFSETS = {
            { -1, 0, 0 },
            { -3, 0, 0 },
            { -7, 0, 0 },
            { -9, 0, 0 },

            { -5, 0, 2 },
            { -5, 0, 4 },
            { -5, 0, -2 },
            { -5, 0, -4 },

            { -1, 1, 0 },
            { -2, 1, 0 },
            { -3, 1, 0 },
            { -4, 1, 0 },
            { -5, 1, 0 },
            { -6, 1, 0 },
            { -7, 1, 0 },
            { -8, 1, 0 },
            { -9, 1, 0 },

            { -5, 1, -4 },
            { -5, 1, -3 },
            { -5, 1, -2 },
            { -5, 1, -1 },
            { -5, 1, 0 },
            { -5, 1, 1 },
            { -5, 1, 2 },
            { -5, 1, 3 },
            { -5, 1, 4 },
    };

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
        replaceDBlocks(Blocks.BARRIER.defaultBlockState());
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        IFluidRenderMulti.super.onStructureInvalid();
        replaceDBlocks(GTBlocks.CASING_STEEL_PIPE.getDefaultState());
    }

    private void replaceDBlocks(BlockState blockState) {
        Direction up = RelativeDirection.UP.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());

        Direction back = getFrontFacing().getOpposite();

        Direction right = RelativeDirection.RIGHT.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());

        for (int[] off : D_OFFSETS) {
            int dx = off[0];
            int dy = off[1];
            int dz = off[2];

            BlockPos pos = getPos()
                    .relative(back, -dx)
                    .relative(up, dy)
                    .relative(right, -dz);

            getLevel().setBlock(pos, blockState, 3);
        }
    }

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
        Matrix4f base = getCenteredMatrix()
                .translate(0, 2, 0)
                .translate(getBackNormal().mul(5))
                .scale(0.5f, 0.5f, 0.5f);

        if (this.recipeLogic.isActive()) {
            return base.rotateY(System.nanoTime() * 1e-9f);
        }

        return base;
    }
}
