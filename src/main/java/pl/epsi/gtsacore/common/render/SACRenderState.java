package pl.epsi.gtsacore.common.render;

import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SACRenderState {

    private int VAO;

    public SACRenderState() {}

    public void saveVAO() {
        this.VAO = GL45.glGetInteger(GL45.GL_VERTEX_ARRAY_BINDING);
    }

    public void restoreVAO() {
        GL45.glBindVertexArray(this.VAO);
        this.VAO = 0;
    }

    public void bindTextures(Map<Integer, Integer> textures) {
        textures.forEach(GL45::glBindTextureUnit);
    }

}
