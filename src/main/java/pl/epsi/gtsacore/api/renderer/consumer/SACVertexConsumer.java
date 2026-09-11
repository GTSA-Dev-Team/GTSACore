package pl.epsi.gtsacore.api.renderer.consumer;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Setter;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryUtil;
import pl.epsi.gtsacore.api.renderer.Vertex;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;
import pl.epsi.gtsacore.common.render.SACRenderState;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

public class SACVertexConsumer<T extends Vertex> {

    private final T template;

    private final SACRenderState state;

    private ByteBuffer verticesBuffer;
    private IntBuffer indicesBuffer;

    private int VBO, VAO, IBO;

    private int maxVertexCount;
    private int maxIndexCount;

    private int currentVertexCount;
    private int currentIndexCount;

    private int frozenIndex;

    private final SACShaderProgram shader;

    @Setter
    private Consumer<SACShaderProgram> uniformSetup = (s) -> {};

    public SACVertexConsumer(T template, SACShaderProgram shader) {
        this(template, 1536, 1536, shader);
    }

    public SACVertexConsumer(T template, int vertexCount, int indexCount, SACShaderProgram shader) {
        this.template = template;
        this.state = new SACRenderState();
        this.shader = shader;
        this.maxVertexCount = vertexCount;
        this.maxIndexCount = indexCount;

        RenderSystem.recordRenderCall(() -> this.init(vertexCount, indexCount));
    }

    protected void init(int vc, int ic) {
        this.VBO = GL45.glGenBuffers();
        this.VAO = GL45.glGenVertexArrays();
        this.IBO = GL45.glGenBuffers();

        reallocVertexBuffer(vc);
        reallocIndexBuffer(ic);

        this.bind();
        template.vertexFormat().setupState();
        GL45.glBindVertexArray(0);
        template.vertexFormat().cleanupState();
    }

    public void bind() {
        this.state.saveVAO();

        GL45.glBindVertexArray(this.VAO);
        GL45.glBindBuffer(GL45.GL_ARRAY_BUFFER, this.VBO);
        GL45.glBindBuffer(GL45.GL_ELEMENT_ARRAY_BUFFER, this.IBO);
    }

    public void unbind() {
        this.state.restoreVAO();
    }


    public void begin() {
        this.frozenIndex = currentVertexCount;
    }

    public void putVertices(T... verts) {
        ensureVertexSpace(verts.length);

        for (T vert : verts) {
            vert.putSelf(verticesBuffer);
        }

        this.currentVertexCount += verts.length;
    }

    public void putIndices(int... indices) {
        ensureIndexSpace(indices.length);
        for (int i = 0; i < indices.length; i++) {
            indicesBuffer.put(indices[i] + frozenIndex);
        }
        this.currentIndexCount += indices.length;
    }

    public void upload() {
        verticesBuffer.flip();
        indicesBuffer.flip();

        GL45.glNamedBufferSubData(VBO, 0, verticesBuffer);
        GL45.glNamedBufferSubData(IBO, 0, indicesBuffer);

        verticesBuffer.clear();
        indicesBuffer.clear();
    }

    public void drawTriangles() {
        this.draw(GL45.GL_TRIANGLES);
    }

    public void drawLines() {
        this.draw(GL45.GL_LINES);
    }

    public void draw(int mode) {
        this.bind();
        this.upload();

        shader.use();
        uniformSetup.accept(shader);

        GL45.glDrawElements(mode, this.currentIndexCount, GL45.GL_UNSIGNED_INT, 0);

        verticesBuffer.clear();
        indicesBuffer.clear();

        this.currentVertexCount = 0;
        this.currentIndexCount = 0;

        this.frozenIndex = 0;

        this.unbind();
    }

    protected void ensureVertexSpace(int additionalSpace) {
        int newCount = this.currentVertexCount + additionalSpace;
        if (newCount >= this.maxVertexCount) {
            while (newCount > this.maxVertexCount) {
                this.maxVertexCount = (int) (((double) this.maxVertexCount) * 2);
            }
            reallocVertexBuffer(this.maxVertexCount);
        }
    }

    protected void ensureIndexSpace(int additional) {
        int newCount = this.currentIndexCount + additional;
        if (newCount >= this.maxIndexCount) {
            while (newCount > this.maxIndexCount) {
                this.maxIndexCount = (int) (((double) this.maxIndexCount) * 2);
            }
            reallocIndexBuffer(this.maxIndexCount);
        }
    }

    protected ByteBuffer genVertexBuffer(int vertexCount) {
        return MemoryUtil.memAlloc(vertexCount * template.size());
    }

    protected IntBuffer genIndexBuffer(int indexCount) {
        return MemoryUtil.memAllocInt(indexCount);
    }

    public void reallocVertexBuffer(int vertexCount) {
        MemoryUtil.memFree(verticesBuffer);
        verticesBuffer = genVertexBuffer(vertexCount);

        GL45.glNamedBufferData(VBO, (long) vertexCount * template.size(), GL45.GL_STREAM_DRAW);
    }

    public void reallocIndexBuffer(int indexCount) {
        MemoryUtil.memFree(indicesBuffer);
        indicesBuffer = genIndexBuffer(indexCount);

        GL45.glNamedBufferData(IBO, indexCount * 4L, GL45.GL_STREAM_DRAW);
    }

    public void free() {
        MemoryUtil.memFree(verticesBuffer);
        MemoryUtil.memFree(indicesBuffer);

        GL45.glDeleteBuffers(VBO);
        GL45.glDeleteBuffers(IBO);
        GL45.glDeleteVertexArrays(VAO);
    }

}
