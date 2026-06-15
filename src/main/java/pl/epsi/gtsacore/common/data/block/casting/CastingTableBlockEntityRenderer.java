package pl.epsi.gtsacore.common.data.block.casting;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.render.ObjRenderer;

public class CastingTableBlockEntityRenderer implements BlockEntityRenderer<CastingTableBlockEntity> {

    @Override
    public void render(CastingTableBlockEntity castingTableBlockEntity, float v, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight, int overlay) {
        Item item = castingTableBlockEntity.getItem().getItem();
        if (item instanceof AbstractCastItem cast) {
            GL45.glEnable(GL45.GL_DEPTH_TEST);

            poseStack.pushPose();
            poseStack.mulPoseMatrix(cast.getModelMatrix());

            ObjRenderer.render(cast.getModel(), cast.getShader(), poseStack, packedLight, cast.getTextures(), true);

            poseStack.popPose();

            GL45.glDisable(GL45.GL_DEPTH_TEST);
        }
    }

}
