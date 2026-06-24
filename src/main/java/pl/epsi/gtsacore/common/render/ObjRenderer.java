package pl.epsi.gtsacore.common.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.renderer.data.st.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;

import java.util.HashMap;
import java.util.Map;

public class ObjRenderer {

    public static ResourceLocation LIGHTMAP_TEXTURE_ID;
    public static int LIGHTMAP_TEXTURE_GL_ID;

    private static final SACShaderProgram SHADER = new SACShaderProgram(GTSubatomicCore.id("shader/default.vsh"), GTSubatomicCore.id("shader/default.fsh"));

    static {
        RenderSystem.recordRenderCall(() -> {
            Minecraft mc = Minecraft.getInstance();

            LIGHTMAP_TEXTURE_ID =
                    ObfuscationReflectionHelper.getPrivateValue(
                            LightTexture.class,
                            mc.gameRenderer.lightTexture(),
                            "lightTextureLocation" // verify mapped name
                    );

            AbstractTexture tex =
                    mc.getTextureManager().getTexture(LIGHTMAP_TEXTURE_ID);
            LIGHTMAP_TEXTURE_GL_ID = tex.getId();
        });
    }

    private static final SACRenderState state = new SACRenderState();

    public static void render(StaticVertexBuffer<?> buf, PoseStack poseStack, int packedLight, Map<Integer, Integer> slotToTextureID, boolean restoreState) {
        render(buf, getDefaultObjShader(), poseStack, packedLight, slotToTextureID, restoreState);
    }

    public static void render(StaticVertexBuffer<?> buf, SACShaderProgram sp, PoseStack poseStack, int packedLight, Map<Integer, Integer> slotToTextureID, boolean restoreState) {
        slotToTextureID = new HashMap<>(slotToTextureID);
        slotToTextureID.put(1, LIGHTMAP_TEXTURE_GL_ID);
        if (restoreState) {
            state.saveVAO();
            state.saveTextures(slotToTextureID.keySet().toArray(new Integer[]{}));
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
            state.restoreTextures();
        }
    }

    public static SACShaderProgram getDefaultObjShader() {
        return SHADER;
    }

}
