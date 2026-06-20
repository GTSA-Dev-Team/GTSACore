package pl.epsi.gtsacore.common.data.block.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class CrucibleAssemblyBlockEntityRenderer implements BlockEntityRenderer<CrucibleAssemblyBlockEntity> {

    @Override
    public void render(CrucibleAssemblyBlockEntity be, float v, PoseStack poseStack, MultiBufferSource buffer, int i, int i1) {
        if (be.getTopFluidID() != null) {
            Fluid fluid = BuiltInRegistries.FLUID.get(be.getTopFluidID());
            float percentFilled = be.getPercentFilled();
        }
    }

}
