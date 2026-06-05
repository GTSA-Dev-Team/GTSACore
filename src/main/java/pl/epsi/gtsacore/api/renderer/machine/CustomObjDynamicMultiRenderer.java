package pl.epsi.gtsacore.api.renderer.machine;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
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
            ResourceLocation.CODEC.fieldOf("obj_ident").forGetter(CustomObjDynamicMultiRenderer::getObjIdent)
    ).apply(instance, CustomObjDynamicMultiRenderer::new));

    public static final DynamicRenderType<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> TYPE = new DynamicRenderType<>(CODEC);
    // spotless:on

    @Getter
    private final ResourceLocation objIdent;
    private final StaticVertexBuffer<ObjVertexFormat> buf;

    private final SACShaderProgram shader;

    public CustomObjDynamicMultiRenderer(ResourceLocation objIdent) {
        this.objIdent = objIdent;
//        this.buf = new StaticVertexBuffer<>(new DefaultVertex[]{
//                new DefaultVertex(-0.5f, -0.5f, 0f, 0, 0, 0, 1), // bottom-left
//                new DefaultVertex( 0.5f, -0.5f, 0f, 1, 0, 0, 1), // bottom-right
//                new DefaultVertex( 0.5f,  0.5f, 0f, 1, 1, 0, 1), // top-right
//                new DefaultVertex(-0.5f,  0.5f, 0f, 0, 1, 0, 1)  // top-left
//        }, new int[]{ 0, 1, 2, 2, 3, 0 });
        try {
            ObjMesh mesh = ObjParser.load(objIdent);
            this.buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.shader = new SACShaderProgram(GTSubatomicCore.id("shader/default.vsh"), GTSubatomicCore.id("shader/default.fsh"));
    }

    @Override
    public DynamicRenderType<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> getType() {
        return TYPE;
    }

    @Override
    public void render(ICustomObjRendererMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed() || !machine.isActive()) return;

        int oldVao = GL45.glGetInteger(GL45.GL_VERTEX_ARRAY_BINDING);

        buf.bind();
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, buf.getIBO());
        GL45.glEnable(GL45.GL_DEPTH_TEST);
        shader.use();

        poseStack.pushPose();
        poseStack.mulPoseMatrix(machine.getModelMatrix());

        shader.uniformMat4f("projMatrix", RenderSystem.getProjectionMatrix());
        shader.uniformMat4f("modelViewMatrix", poseStack.last().pose());

        poseStack.popPose();

        buf.draw();
        buf.unbind();

        GL45.glDisable(GL45.GL_DEPTH_TEST);

        GL45.glBindVertexArray(oldVao);
    }

    public static DynamicRender<?, ?> makeObjRenderer(ResourceLocation rl) {
        return new CustomObjDynamicMultiRenderer(rl);
    }

}
