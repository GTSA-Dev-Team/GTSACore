package pl.epsi.gtsacore.common.data.block.casting;

import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.renderer.consumer.SACVertexConsumer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;
import pl.epsi.gtsacore.common.data.item.casting.ICastingTableable;
import pl.epsi.gtsacore.common.render.ObjRenderer;

public class CastingTableBlockEntityRenderer implements BlockEntityRenderer<CastingTableBlockEntity> {

    private static final SACVertexConsumer<CastingTableVertex> builder = new SACVertexConsumer<>(CastingTableVertex.TEMPLATE);
    private static final SACShaderProgram shader = new SACShaderProgram(GTSubatomicCore.id("shader/casting_table.vsh"), GTSubatomicCore.id("shader/casting_table.fsh"));

    @Override
    public void render(CastingTableBlockEntity castingTableBlockEntity, float v, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight, int overlay) {
        if (castingTableBlockEntity.getMoldItem().isEmpty()) return;
        Item item = castingTableBlockEntity.getMoldItem().getItem();
        if (item instanceof ICastingTableable tableable) {
            GL45.glEnable(GL45.GL_DEPTH_TEST);

            var be = castingTableBlockEntity.getLevel().getBlockEntity(castingTableBlockEntity.getBlockPos().above());
            Direction d = null;
            if (be instanceof FaucetBlockEntity faucet) {
                d = faucet.getBlockState().getValue(FaucetBlock.FACING);
            }

            poseStack.pushPose();
            poseStack.translate(0.5f, 0.0f, 0.5f);

            float angle = 0;
            if (d != null) {
                angle = d.toYRot() - Direction.EAST.toYRot();
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
            }

            poseStack.translate(-0.5f, 0.0f, -0.5f);

            poseStack.mulPoseMatrix(tableable.getRenderInfo().localMat());
            if (castingTableBlockEntity.getHammeringProgress() != 0) {
                poseStack.scale(1, (float) (16 - 3 * castingTableBlockEntity.getHammeringProgress()) / 16, 1);
            }

            ObjRenderer.render(tableable.getRenderInfo().VBO(), tableable.getRenderInfo().shader(), poseStack,
                    packedLight, tableable.getTextures(castingTableBlockEntity.getMoldItem()), true);
            poseStack.popPose();

            if (castingTableBlockEntity.getFluidID() != null) {
                Fluid fluid = ForgeRegistries.FLUIDS.getValue(castingTableBlockEntity.getFluidID());
                var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
                var sprite = RenderUtil.FluidTextureType.FLOWING.map(fluidClientInfo);

                AbstractTexture at = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());

                AABB aabb = tableable.getRenderInfo().cavityBounds();
                long runningTicks = castingTableBlockEntity.getProgress();
                CastingState castingState = castingTableBlockEntity.getCastingState();
                float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();

                float minU = (float) (u0 + (u1 - u0) * (aabb.minX + 0.5f));
                float maxU = (float) (u0 + (u1 - u0) * (aabb.maxX + 0.5f));

                float minV = (float) (v0 + (v1 - v0) * (aabb.minZ + 0.5f));
                float maxV = (float) (v0 + (v1 - v0) * (aabb.maxZ + 0.5f));
                int color = fluidClientInfo.getTintColor();

                poseStack.pushPose();
                poseStack.mulPoseMatrix(new Matrix4f().translate(0.5f, 0.9375f, 0.5f));
                if (d != null) {
                    poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
                }

                if (castingState == CastingState.FILLING) {
                    double fillDistance = aabb.maxY - aabb.minY;
                    writePlane(aabb, color, minU, minV, maxU, maxV,
                            (float) (fillDistance * (1 - getPercentFilled(castingTableBlockEntity, runningTicks))), 0);
                } else if (castingState == CastingState.SOLIDIFYING) {
                    float solidPercent = getPercentSolidified(castingTableBlockEntity, runningTicks);

                    writePlane(aabb, color, minU, minV, maxU, maxV, 0f, solidPercent);
                } else if (castingState == CastingState.FINISHED) {
                    writePlane(aabb, color, minU, minV, minU, minV, 0f, 1);
                }

                shader.use();
                shader.uniformMat4f("projMatrix", RenderSystem.getProjectionMatrix());
                shader.uniformMat4f("localMatrix", poseStack.last().pose());
                shader.uniformTexture("tex0", 0);
                GL45.glBindTextureUnit(0, at.getId());
                builder.drawTriangles();

                poseStack.popPose();
            }

            GL45.glDisable(GL45.GL_DEPTH_TEST);
        }
    }

    public float getPercentFilled(CastingTableBlockEntity be, long progress) {
        float percent = (float) progress / be.getFillingTime();
        if (percent > 0.9) percent = 1;
        return percent;
    }

    public float getPercentSolidified(CastingTableBlockEntity be, long progress) {
        return (float) progress / be.getSolidifyingTime();
    }

    public void writePlane(AABB aabb, int color, float minU, float minV,
                           float maxU, float maxV, float yOffset, float solidifyProgress) {
        builder.begin();
        builder.putVertices(
                new CastingTableVertex((float) aabb.minX, (float) (aabb.maxY - yOffset), (float) aabb.minZ, color, 0, 1, 0, minU, minV, solidifyProgress),
                new CastingTableVertex((float) aabb.minX, (float) aabb.maxY - yOffset, (float) aabb.maxZ, color, 0, 1, 0, minU, maxV, solidifyProgress),
                new CastingTableVertex((float) aabb.maxX, (float) aabb.maxY - yOffset, (float) aabb.maxZ, color, 0, 1, 0, maxU, maxV, solidifyProgress),
                new CastingTableVertex((float) aabb.maxX, (float) aabb.maxY - yOffset, (float) aabb.minZ, color, 0, 1, 0, maxU, minV, solidifyProgress)
        );

        builder.putIndices(0, 1, 2);
        builder.putIndices(3, 0, 2);
    }

}
