package pl.epsi.gtsacore.api.renderer.machine;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
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
import pl.epsi.gtsacore.common.render.ObjRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<Integer, Integer> textures = new HashMap<>();

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

        RenderSystem.recordRenderCall(() -> {
            AbstractTexture texture =
                    Minecraft.getInstance()
                            .getTextureManager()
                            .getTexture(textureIdent);

            textures.put(0, texture.getId());
        });
    }

    @Override
    public DynamicRenderType<ICustomObjRendererMulti, CustomObjDynamicMultiRenderer> getType() {
        return TYPE;
    }

    @Override
    public void render(ICustomObjRendererMulti machine, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!machine.isFormed()) return;
        if (!machine.isActive() && !alwaysRender) return;

        GL45.glEnable(GL45.GL_DEPTH_TEST);

        poseStack.pushPose();
        poseStack.mulPoseMatrix(machine.getModelMatrix());

        ObjRenderer.render(buf, poseStack, packedLight, textures, true);

        poseStack.popPose();
        //GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_LINE);
        //GL45.glPolygonMode(GL45.GL_FRONT_AND_BACK, GL45.GL_FILL);

        GL45.glDisable(GL45.GL_DEPTH_TEST);
    }

    public static DynamicRender<?, ?> makeObjRenderer(ResourceLocation obj, ResourceLocation tex, boolean alwaysRender) {
        return new CustomObjDynamicMultiRenderer(obj, tex, alwaysRender);
    }

}
