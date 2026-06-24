package pl.epsi.gtsacore.common.render;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.api.renderer.data.dynamic.DynamicVertexBuilder;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;

public class DynamicRenderer {

    private int VBO, VAO, IBO;

    private final SACVertexFormat format;
    private final SACRenderState state = new SACRenderState();
    private final SACShaderProgram shader;

    private int indexCount = 0;

    private int texID = 0;

    @Getter
    private boolean reset = false;

    public DynamicRenderer(SACVertexFormat format, SACShaderProgram shader) {
        this.format = format;
        this.shader = shader;
        RenderSystem.recordRenderCall(this::init);
    }

    public void useTexture(ResourceLocation tex) {
        if (tex == null) {
            texID = 0;
            return;
        }
        AbstractTexture at =
                Minecraft.getInstance().getTextureManager().getTexture(tex);
        this.texID = at.getId();
    }

    public void init() {
        this.VBO = GL45.glGenBuffers();
        this.IBO = GL45.glGenBuffers();
        this.VAO = GL45.glGenVertexArrays();

        this.bind();
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, this.VBO);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, this.IBO);
        format.setupState();

        this.unbind();
        format.cleanupState();
    }

    public void bind() {
        GL45.glBindVertexArray(this.VAO);
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, this.VBO);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, this.IBO);
    }

    public void unbind() {
        GL45.glBindVertexArray(0);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, 0);
    }

    public void draw(boolean restoreState) {
        if (restoreState) {
            state.saveVAO();
            state.saveTextures(0);
        }

        this.bind();
        shader.use();

        shader.uniformMat4f("projMatrix", RenderSystem.getProjectionMatrix());
        GL45.glBindTextureUnit(0, texID);
        shader.uniformTexture("tex0", 0);

        GL45.glDrawElements(GL45.GL_TRIANGLES, indexCount, GL45.GL_UNSIGNED_INT, 0);

        if (restoreState) {
            state.restoreVAO();
            state.restoreTextures();
        }
    }

    public void upload(DynamicVertexBuilder builder, boolean restoreState) {
        if (restoreState) {
            state.saveVAO();
        }
        this.reset = false;
        this.bind();
        builder.getVertexData().flip();
        builder.getIndexData().flip();

        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, builder.getVertexData(), GL45.GL_DYNAMIC_DRAW);
        GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, builder.getIndexData(), GL45.GL_DYNAMIC_DRAW);
        this.unbind();
        this.indexCount = builder.getIndexCount();
        if (restoreState) {
            state.restoreVAO();
        }
    }

    public void reset() { this.reset = true; }

}
