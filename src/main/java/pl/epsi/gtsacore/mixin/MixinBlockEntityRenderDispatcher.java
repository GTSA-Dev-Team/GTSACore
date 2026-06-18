package pl.epsi.gtsacore.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.lwjgl.opengl.GL45;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlockEntityRenderer;

@Mixin(BlockEntityRenderDispatcher.class)
public class MixinBlockEntityRenderDispatcher {

    @Inject(method = "setupAndRender", at = @At("TAIL"))
    private static <T extends BlockEntity> void gtsac$castingTableRender(BlockEntityRenderer<T> renderer, T blockEntity, float partialTick,
                                                                         PoseStack poseStack, MultiBufferSource bufferSource, CallbackInfo ci) {
        if (!CastingTableBlockEntityRenderer.renderer.isReset()) {
            GL45.glEnable(GL45.GL_DEPTH_TEST);
            CastingTableBlockEntityRenderer.renderer.draw(true);
            CastingTableBlockEntityRenderer.renderer.reset();
            GL45.glDisable(GL45.GL_DEPTH_TEST);
        }
    }

}
