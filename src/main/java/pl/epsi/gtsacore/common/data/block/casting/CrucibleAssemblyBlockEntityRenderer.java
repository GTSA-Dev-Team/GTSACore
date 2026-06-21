package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class CrucibleAssemblyBlockEntityRenderer implements BlockEntityRenderer<CrucibleAssemblyBlockEntity> {

    @Override
    public void render(CrucibleAssemblyBlockEntity be, float v, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (be.getTopFluidID() != null) {
            Fluid fluid = BuiltInRegistries.FLUID.get(be.getTopFluidID());
            float percentFilled = be.getPercentFilled();

            RenderType fluidRenderType = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
            var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
            var sprite = RenderUtil.FluidTextureType.FLOWING.map(fluidClientInfo);
            float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
            int color = fluidClientInfo.getTintColor();

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.0f, 0.5f);
            float height = (float) (0.3125 * percentFilled);
            RenderSystem.disableCull();
            RenderSystem.disableDepthTest();
            var mat = poseStack.last().pose();
            consumer.vertex(mat, -0.3125f, height, -0.3125f).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, -0.3125f, height, 0.3125f).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.3125f, height, 0.3125f).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.3125f, height, -0.3125f).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            RenderSystem.enableCull();
            poseStack.popPose();
        }
    }

}
