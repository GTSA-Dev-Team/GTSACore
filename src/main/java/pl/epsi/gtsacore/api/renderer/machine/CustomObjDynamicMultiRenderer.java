package pl.epsi.gtsacore.api.renderer.machine;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.machine.feature.multiblock.ICustomObjRendererMulti;
import pl.epsi.gtsacore.api.model.ObjMesh;
import pl.epsi.gtsacore.api.model.ObjParser;
import pl.epsi.gtsacore.api.model.ObjVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;

import java.io.IOException;

public class CustomObjDynamicMultiRenderer extends DynamicRender<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> {

    // spotless:off
    public static final Codec<CustomObjDynamicMultiRenderer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("obj_ident").forGetter(CustomObjDynamicMultiRenderer::getObjIdent),
            ResourceLocation.CODEC.fieldOf("tex_ident").forGetter(CustomObjDynamicMultiRenderer::getTextureIdent),
            Codec.BOOL.fieldOf("always_render").forGetter(CustomObjDynamicMultiRenderer::isAlwaysRender)
    ).apply(instance, CustomObjDynamicMultiRenderer::new));

    public static final DynamicRenderType<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> TYPE = new DynamicRenderType<>(CODEC);
    // spotless:on

    @Getter
    private final ResourceLocation objIdent, textureIdent;
    @Getter
    private final boolean alwaysRender;
    private final StaticVertexBuffer<ObjVertexFormat> buf;

    private final SACShaderProgram shader;
    private int glTexID;

    public CustomObjDynamicMultiRenderer(ResourceLocation objIdent, ResourceLocation textureIdent, boolean alwaysRender) {
        this.objIdent = objIdent;
        this.textureIdent = textureIdent;
        this.alwaysRender = alwaysRender;
        try {
            ObjMesh mesh = ObjParser.load(objIdent);
            this.buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (textureIdent == null || Minecraft.getInstance() == null) {
            glTexID = 0;
        } else {
            RenderSystem.recordRenderCall(() -> {
                AbstractTexture texture =
                        Minecraft.getInstance()
                                .getTextureManager()
                                .getTexture(textureIdent);

                glTexID = texture.getId();
            });
        }

        this.shader = new SACShaderProgram(GTSubatomicCore.id("shader/default.vsh"), GTSubatomicCore.id("shader/default.fsh"));
    }

    @Override
    public DynamicRenderType<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> getType() {
        return TYPE;
    }

    @Override
    public void render(ICustomObjRendererMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed()) return;
        if (!machine.isActive() && !alwaysRender) return;

        int oldVao = GL45.glGetInteger(GL45.GL_VERTEX_ARRAY_BINDING);

        buf.bind();
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, buf.getIBO());
        GL45.glEnable(GL45.GL_DEPTH_TEST);

        shader.use();

        if (glTexID != 0) {
            GL45.glActiveTexture(GL45.GL_TEXTURE0);
            GL45.glBindTexture(GL45.GL_TEXTURE_2D, glTexID);
            shader.uniformTexture("tex0", 0);
        }

        poseStack.pushPose();
        poseStack.mulPoseMatrix(machine.getModelMatrix());

        shader.uniformMat4f("projMatrix", RenderSystem.getProjectionMatrix());
        shader.uniformMat4f("modelViewMatrix", poseStack.last().pose());

        poseStack.popPose();
        //GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_LINE);
        buf.draw();
        //GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_FILL);
        buf.unbind();

        GL45.glDisable(GL45.GL_DEPTH_TEST);

        GL45.glBindVertexArray(oldVao);
    }

    public static DynamicRender<?, ?> makeObjRenderer(ResourceLocation obj, ResourceLocation tex, boolean alwaysRender) {
        return new CustomObjDynamicMultiRenderer(obj, tex, alwaysRender);
    }

}
