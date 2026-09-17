package pl.epsi.gtsacore.common.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;

import java.util.HashMap;
import java.util.Map;

public class ObjRenderer {

    public static int LIGHTMAP_TEXTURE_GL_ID;

    private static final SACShaderProgram SHADER = new SACShaderProgram(GTSubatomicCore.id("shader/default.vsh"), GTSubatomicCore.id("shader/default.fsh"));

    private static final SACRenderState state = new SACRenderState();

    public static void render(StaticVertexBuffer<?> buf, PoseStack poseStack, int packedLight, Map<Integer, Integer> slotToTextureID, boolean restoreState) {
        render(buf, getDefaultObjShader(), poseStack, packedLight, slotToTextureID, restoreState);
    }

    public static void render(StaticVertexBuffer<?> buf, SACShaderProgram sp, PoseStack poseStack, int packedLight, Map<Integer, Integer> slotToTextureID, boolean restoreState) {
        slotToTextureID = new HashMap<>(slotToTextureID);
        slotToTextureID.put(1, LIGHTMAP_TEXTURE_GL_ID);
        if (restoreState) {
            state.saveVAO();
        }

        buf.bind();
        sp.use();
        state.bindTextures(slotToTextureID);

        sp.uniformMat4f("projMatrix", RenderSystem.getProjectionMatrix());
        sp.uniformMat4f("modelViewMatrix", poseStack.last().pose());
        sp.uniformFloat2("packedLight", LightTexture.block(packedLight) / 15.0f, LightTexture.sky(packedLight) / 15.0f);
        slotToTextureID.forEach((slot, id) -> sp.uniformTexture("tex" + slot, slot));

        buf.draw();

        if (restoreState) {
            state.restoreVAO();
        }
    }

    public static SACShaderProgram getDefaultObjShader() {
        return SHADER;
    }

}
