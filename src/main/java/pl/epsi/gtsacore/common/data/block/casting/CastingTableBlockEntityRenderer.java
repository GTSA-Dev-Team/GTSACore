package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.render.ObjRenderer;

public class CastingTableBlockEntityRenderer implements BlockEntityRenderer<CastingTableBlockEntity> {

    @Override
    public void render(CastingTableBlockEntity castingTableBlockEntity, float v, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int overlay) {
        if (castingTableBlockEntity.getMoldItem().isEmpty()) return;
        Item item = castingTableBlockEntity.getMoldItem().getItem();
        if (item instanceof AbstractCastItem cast) {
            GL45.glEnable(GL45.GL_DEPTH_TEST);

            var be = castingTableBlockEntity.getLevel().getBlockEntity(castingTableBlockEntity.getBlockPos().above());
            Direction d = null;
            if (be instanceof FaucetBlockEntity faucet) {
                d = faucet.getBlockState().getValue(FaucetBlock.FACING);
            }

            poseStack.pushPose();
            Matrix4f rot = new Matrix4f();
            if (d != null) {
                rot.rotateY((float) Math.toRadians(((float) d.ordinal() - 1) * 90));
            }
            Matrix4f local = cast.getRenderInfo().localMat();

// extract the nudge
            float ox = local.m30();
            float oy = local.m31();
            float oz = local.m32();

            poseStack.mulPoseMatrix(local);
            poseStack.translate(-ox, -oy, -oz);
            poseStack.mulPoseMatrix(rot);
            poseStack.translate(ox, oy, oz);

// then render the model at the origin


            ObjRenderer.render(cast.getRenderInfo().VBO(), cast.getRenderInfo().shader(), poseStack,
                    packedLight, cast.getTextures(), true);
            poseStack.popPose();

            if (castingTableBlockEntity.getFluidID() != null) {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(castingTableBlockEntity.getFluidID());
                var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(fluid.defaultFluidState());
                var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));

                var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
                var sprite = RenderUtil.FluidTextureType.FLOWING.map(fluidClientInfo);
                AABB aabb = cast.getRenderInfo().cavityBounds();
                float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();

                float minU = (float) (u0 + (u1 - u0) * (aabb.minX + 0.5f));
                float maxU = (float) (u0 + (u1 - u0) * (aabb.maxX + 0.5f));

                float minV = (float) (v0 + (v1 - v0) * (aabb.minZ + 0.5f));
                float maxV = (float) (v0 + (v1 - v0) * (aabb.maxZ + 0.5f));
                int color = fluidClientInfo.getTintColor();


                poseStack.pushPose();
                poseStack.mulPoseMatrix(new Matrix4f().translate(0.5f, 0.9375f, 0.5f).mul(rot));
                var mat = poseStack.last().pose();
                consumer.vertex(mat, (float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ).color(color)
                        .uv(minU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
                consumer.vertex(mat, (float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ).color(color)
                        .uv(minU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
                consumer.vertex(mat, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ).color(color)
                        .uv(maxU, maxV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
                consumer.vertex(mat, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ).color(color)
                        .uv(maxU, minV).overlayCoords(overlay).uv2(packedLight).normal(0, 1, 0).endVertex();
                poseStack.popPose();
            }

            GL45.glDisable(GL45.GL_DEPTH_TEST);
        }
    }

}
