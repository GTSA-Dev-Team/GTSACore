package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import pl.epsi.gtsacore.util.SACRenderUtil;
import pl.epsi.gtsacore.util.SACUtils;

public class CrucibleAssemblyBlockEntityRenderer implements BlockEntityRenderer<CrucibleAssemblyBlockEntity> {

    @Override
    public void render(CrucibleAssemblyBlockEntity be, float v, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (be.getTopFluidID() != null && !be.getTopFluidID().equals(SACUtils.EMPTY_IDENTIFIER)) {
            Fluid fluid = BuiltInRegistries.FLUID.get(be.getTopFluidID());
            float percentFilled = be.getPercentFilled();

            RenderType fluidRenderType = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
            VertexConsumer consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
            var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
            var sprite = RenderUtil.FluidTextureType.FLOWING.map(fluidClientInfo);
            float[] uvs = SACRenderUtil.getUV(sprite, 10, 10);
            float u0 = uvs[0], v0 = uvs[1], u1 = uvs[2], v1 = uvs[3];
            int color = fluidClientInfo.getTintColor();

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.01f, 0.5f);
            float height = (float) (0.3125 * percentFilled);
            RenderSystem.disableCull();
            var mat = poseStack.last().pose();
            light = LightTexture.FULL_BRIGHT;
            consumer.vertex(mat, -0.3125f, height, -0.3125f).color(color).uv(u0, v0).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, -0.3125f, height, 0.3125f).color(color).uv(u0, v1).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.3125f, height, 0.3125f).color(color).uv(u1, v1).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.3125f, height, -0.3125f).color(color).uv(u1, v0).overlayCoords(overlay).uv2(light)
                    .normal(0, 1, 0).endVertex();
            poseStack.popPose();

            Direction facing = be.getBlockState().getValue(CrucibleAssemblyBlock.FACING);
            if (be.getLevel().getBlockEntity(be.getBlockPos().relative(facing)) instanceof FaucetBlockEntity fe && fe.getCastingState() == CastingState.FILLING) {
                poseStack.pushPose();
                poseStack.translate(0.75f, -0.0234375f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(facing.toYRot()));
                SACRenderUtil.renderPrism(consumer, poseStack.last().pose(), sprite, color, overlay, light, 0.25f, 0.4375f, 0.125f);
                poseStack.popPose();
            }
            RenderSystem.enableCull();
        }
    }

}
