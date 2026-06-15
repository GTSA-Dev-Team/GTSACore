package pl.epsi.gtsacore.api.renderer.data;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import pl.epsi.gtsacore.api.renderer.Vertex;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class StaticVertexBuffer<T extends Vertex> {

    @Getter
    private int VAO;
    @Getter
    private int IBO;
    @Getter
    private int VBO;

    private int indexCount;

    public StaticVertexBuffer(T[] vertices, int[] indices) {
        RenderSystem.recordRenderCall(() -> init(vertices, indices));
    }

    protected void init(T[] vertices, int[] indices) {
        this.VBO = GL45.glGenBuffers();
        this.IBO = GL45.glGenBuffers();
        this.VAO = GL45.glGenVertexArrays();
        this.indexCount = indices.length;

        this.bind();
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, this.VBO);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, this.IBO);

        ByteBuffer data = MemoryUtil.memAlloc(vertices.length * vertices[0].size());
        Arrays.stream(vertices).forEach(v -> v.putSelf(data));
        data.flip();

        IntBuffer indexBuffer = MemoryUtil.memAllocInt(indices.length);
        indexBuffer.put(indices);
        indexBuffer.flip();

        GL45.glBufferData(GL45.GL_ARRAY_BUFFER, data, GL45.GL_STATIC_DRAW);
        GL45.glBufferData(GL45.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL45.GL_STATIC_DRAW);
        Arrays.stream(vertices).forEach(v -> v.vertexFormat().setupState());

        this.unbind();
        Arrays.stream(vertices).forEach(v -> v.vertexFormat().cleanupState());
    }

    public void bind() {
        GL45.glBindVertexArray(this.VAO);
    }

    public void unbind() {
        GL45.glBindVertexArray(0);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, 0);

    }

    public void draw() {
        GL20.glDrawElements(GL45.GL_TRIANGLES, indexCount, GL45.GL_UNSIGNED_INT, 0);
    }

}
