package pl.epsi.gtsacore.api.renderer.data.dynamic;

import net.minecraft.client.renderer.LightTexture;
import org.lwjgl.system.MemoryUtil;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormatElement;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class DynamicMeshBuffer implements DynamicVertexBuilder {

    public static final SACVertexFormat FORMAT = SACVertexFormat.builder()
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 4)
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 2)
            .withElement(SACVertexFormatElement.Type.UINT, 2)
            .build();

    protected int size, idxSize;

    protected ByteBuffer buf;
    protected IntBuffer indicesBuffer;

    protected float x, y, z;
    protected float r, g, b, a;
    protected float nx, ny, nz;
    protected float u, v;
    protected int skyLight, blockLight;

    protected int currentIndex = 0;
    protected int startIndex;

    public DynamicMeshBuffer(int size) {
        this.buf = MemoryUtil.memAlloc(size);
        this.indicesBuffer = MemoryUtil.memAllocInt(size);
        this.size = size;
        this.idxSize = size;
    }

    protected void ensureSpace(int bytes) {
        if (size - buf.position() < bytes) {
            while (size - buf.position() < bytes) {
                size = size * 2;
            }
            this.buf = MemoryUtil.memRealloc(this.buf, size);
        }
    }

    protected void ensureSpaceIndices(int numIndices) {
        if (idxSize - indicesBuffer.position() < numIndices) {
            while (idxSize - indicesBuffer.position() < numIndices) {
                idxSize = idxSize * 2;
            }
            this.indicesBuffer = MemoryUtil.memRealloc(this.indicesBuffer, idxSize);
        }
    }

    @Override
    public DynamicVertexBuilder vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public DynamicVertexBuilder color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        return this;
    }

    @Override
    public DynamicVertexBuilder uv(float u, float v) {
        this.u = u;
        this.v = v;
        return this;
    }

    @Override
    public DynamicVertexBuilder light(int packedLight) {
        this.blockLight = LightTexture.block(packedLight);
        this.skyLight = LightTexture.sky(packedLight);
        return this;
    }

    @Override
    public DynamicVertexBuilder normal(float nx, float ny, float nz) {
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        return this;
    }

    @Override
    public DynamicVertexBuilder index(int... i) {
        ensureSpaceIndices(i.length);
        for (int a : i) {
            this.indicesBuffer.put(startIndex + a);
            currentIndex++;
        }
        return this;
    }

    @Override
    public void endVertex() {
        this.ensureSpace(12 * Float.BYTES + 2 * Integer.BYTES);

        this.buf.putFloat(x);
        this.buf.putFloat(y);
        this.buf.putFloat(z);

        this.buf.putFloat(r);
        this.buf.putFloat(g);
        this.buf.putFloat(b);
        this.buf.putFloat(a);

        this.buf.putFloat(nx);
        this.buf.putFloat(ny);
        this.buf.putFloat(nz);

        this.buf.putFloat(u);
        this.buf.putFloat(v);

        this.buf.putInt(blockLight);
        this.buf.putInt(skyLight);
    }

    @Override
    public void reset() {
        this.buf.clear();
        this.indicesBuffer.clear();
        this.currentIndex = 0;
    }

    @Override
    public SACVertexFormat format() {
        return FORMAT;
    }

    @Override
    public ByteBuffer getVertexData() {
        return this.buf;
    }

    @Override
    public IntBuffer getIndexData() {
        return this.indicesBuffer;
    }

    @Override
    public int getIndexCount() {
        return this.currentIndex;
    }

    @Override
    public void start() {
        this.startIndex = this.currentIndex;
    }
}
