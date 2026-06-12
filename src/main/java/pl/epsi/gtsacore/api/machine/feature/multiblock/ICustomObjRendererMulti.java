package pl.epsi.gtsacore.api.machine.feature.multiblock;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public interface ICustomObjRendererMulti extends IWorkableMultiController, IMachineFeature {

    default Vector3f getRightNormal() {
        Direction right = RelativeDirection.RIGHT.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());
        Vec3i norm = right.getNormal();
        return new Vector3f(norm.getX(), norm.getY(), norm.getZ()).normalize();
    }

    default Vector3f getUpNormal() {
        Direction up = RelativeDirection.UP.getRelative(
                getFrontFacing(), getUpwardsFacing(), isFlipped());
        Vec3i norm = up.getNormal();
        return new Vector3f(norm.getX(), norm.getY(), norm.getZ()).normalize();
    }

    default Vector3f getBackNormal() {
        Direction back = getFrontFacing().getOpposite();
        Vec3i norm = back.getNormal();
        return new Vector3f(norm.getX(), norm.getY(), norm.getZ()).normalize();
    }

    default Matrix4f getModelMatrix() { return new Matrix4f(); }

    default Matrix4f getCenteredMatrix() {
        return new Matrix4f().translate(0.5f, -0.5f, 0.5f);
    }

    // These exist on MetaMachine so it *should* be fine
    Direction getFrontFacing();
    Direction getUpwardsFacing();
    boolean isFlipped();

}
