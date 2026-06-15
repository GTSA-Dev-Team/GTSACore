package pl.epsi.gtsacore.common.render;

import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class SACRenderState {

    private final Map<Integer, Integer> textures = new HashMap<>();

    private int VAO;

    public SACRenderState() {}

    public void saveVAO() {
        this.VAO = GL45.glGetInteger(GL45.GL_VERTEX_ARRAY_BINDING);
    }

    public void restoreVAO() {
        GL45.glBindVertexArray(this.VAO);
        this.VAO = 0;
    }

    public void saveTextures(int... slots) {
        saveTextures(slots);
    }

    public void saveTextures(Integer[] slots) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer buf = stack.callocInt(1);
            for (int slot : slots) {
                GL45.glGetIntegeri_v(GL45.GL_TEXTURE_BINDING_2D, slot, buf);
                textures.put(slot, buf.get(0));
                buf.clear();
            }
        }
    }

    public void restoreTextures() {
        textures.forEach(GL45::glBindTextureUnit);
    }

    public void bindTextures(Map<Integer, Integer> textures) {
        textures.forEach(GL45::glBindTextureUnit);
    }

}
