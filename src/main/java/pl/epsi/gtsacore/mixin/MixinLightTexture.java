package pl.epsi.gtsacore.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.epsi.gtsacore.common.render.ObjRenderer;

@Mixin(LightTexture.class)
public class MixinLightTexture {

    @Shadow
    @Final
    private DynamicTexture lightTexture;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void gtsac$setUpLightTexture(GameRenderer renderer, Minecraft minecraft, CallbackInfo ci) {
        ObjRenderer.LIGHTMAP_TEXTURE_GL_ID = lightTexture.getId();
    }

}
