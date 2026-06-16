package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.joml.Matrix4f;

public class FaucetBlockEntityRenderer implements BlockEntityRenderer<FaucetBlockEntity> {

    @Override
    public void render(FaucetBlockEntity faucetBlockEntity, float v, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int overlay) {
        if (faucetBlockEntity.getFluidID() != null) {
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(faucetBlockEntity.getFluidID());
            var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
            var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));

            var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
            var sprite = RenderUtil.FluidTextureType.FLOWING.map(fluidClientInfo);
            float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
            float du = u1 - u0;
            float dv = v1 - v0;

            float minU = u0 + du * 0.375f;
            float maxU = u0 + du * 0.625f;

            float minV = v0 + dv * 0.0f;
            float maxV = v0 + dv * 0.4375f;
            int color = fluidClientInfo.getTintColor();

            poseStack.pushPose();
            poseStack.mulPoseMatrix(new Matrix4f()
                    .translate(0.5f, 0.4150f, 0.5f)
                    .rotateY(((float) Math.toRadians(faucetBlockEntity.getBlockState().getValue(FaucetBlock.FACING).ordinal() - 2) * 90)));
            var mat = poseStack.last().pose();
            consumer.vertex(mat, -0.125f, 0, -0.5f).color(color)
                    .uv(minU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, -0.125f, 0, -0.0625f).color(color)
                    .uv(minU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.125f, 0, -0.0625f).color(color)
                    .uv(maxU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.125f, 0, -0.5f).color(color)
                    .uv(maxU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();

            minU = u0 + du * (-0.125f + 0.5f);
            maxU = u0 + du * (0.125f + 0.5f);

            minV = v0 + dv * (-0.1025f + 0.5f);
            maxV = v0 + dv * 0;

            consumer.vertex(mat, -0.125f, 0, -0.0625f).color(color)
                    .uv(minU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, -0.125f, -0.1025f, -0.0625f).color(color)
                    .uv(minU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.125f, -0.1025f, -0.0625f).color(color)
                    .uv(maxU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            consumer.vertex(mat, 0.125f, 0, -0.0625f).color(color)
                    .uv(maxU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.mulPoseMatrix(new Matrix4f()
                    .translate(0.5f, 0.0f, 0.5f)
                    .rotateY(((float) Math.toRadians(faucetBlockEntity.getBlockState().getValue(FaucetBlock.FACING).ordinal() - 2) * 90)));
            mat = poseStack.last().pose();
            renderPrism(consumer, mat, sprite, color, overlay, packedLight, 0.125f, 0.415f, 0.125f);
            poseStack.popPose();
        }
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f mat,
            int color,
            int overlay,
            int packedLight,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz
    ) {
        consumer.vertex(mat, x, y, z)
                .color(color)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
    }

    public static void renderPrism(VertexConsumer consumer, Matrix4f mat, TextureAtlasSprite sprite, int color, int overlay,
                                   int packedLight, float width, float height, float depth) {
        float x0 = -width * 0.5f;
        float x1 =  width * 0.5f;

        float z0 = -depth * 0.5f;
        float z1 =  depth * 0.5f;

        float y0 = 0.0f;
        float y1 = height;

        // UV helpers (block-space -> texture-space)
        float topU0 = (x0 + 0.5f) * 16.0f;
        float topU1 = (x1 + 0.5f) * 16.0f;
        float topV0 = (z0 + 0.5f) * 16.0f;
        float topV1 = (z1 + 0.5f) * 16.0f;

        float sideU0X = (x0 + 0.5f) * 16.0f;
        float sideU1X = (x1 + 0.5f) * 16.0f;

        float sideU0Z = (z0 + 0.5f) * 16.0f;
        float sideU1Z = (z1 + 0.5f) * 16.0f;

        float sideV0 = 0.0f;
        float sideV1 = y1 * 16.0f;

        // =========================
        // TOP (+Y) CCW from above
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(topU0), sprite.getV(topV0),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(topU0), sprite.getV(topV1),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(topU1), sprite.getV(topV1),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(topU1), sprite.getV(topV0),
                0, 1, 0);

        // =========================
        // NORTH (-Z)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z0,
                sprite.getU(sideU1X), sprite.getV(sideV0),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z0,
                sprite.getU(sideU0X), sprite.getV(sideV0),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(sideU0X), sprite.getV(sideV1),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(sideU1X), sprite.getV(sideV1),
                0, 0, -1);

        // =========================
        // SOUTH (+Z)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z1,
                sprite.getU(sideU0X), sprite.getV(sideV0),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z1,
                sprite.getU(sideU1X), sprite.getV(sideV0),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(sideU1X), sprite.getV(sideV1),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(sideU0X), sprite.getV(sideV1),
                0, 0, 1);

        // =========================
        // WEST (-X)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV0),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV0),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV1),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV1),
                -1, 0, 0);

        // =========================
        // EAST (+X)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV0),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV0),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV1),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV1),
                1, 0, 0);
    }

}
