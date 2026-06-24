package pl.epsi.gtsacore.api.renderer.data.dynamic;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface DynamicVertexBuilder {

    default DynamicVertexBuilder vertex(Matrix4f mat, float x, float y, float z) {
        Vector4f v = new Vector4f(x, y, z, 1);
        mat.transform(v);
        return vertex(v.x, v.y, v.z);
    }
    DynamicVertexBuilder vertex(float x, float y, float z);

    default DynamicVertexBuilder color(int argb) {
        float a = ((argb >>> 24) & 0xFF) / 255.0f;
        float r = ((argb >>> 16) & 0xFF) / 255.0f;
        float g = ((argb >>> 8)  & 0xFF) / 255.0f;
        float b = (argb & 0xFF) / 255.0f;

        return color(r, g, b, a);
    }

    DynamicVertexBuilder color(float r, float g, float b, float a);

    DynamicVertexBuilder uv(float u, float v);

    DynamicVertexBuilder light(int packedLight);

    DynamicVertexBuilder normal(float nx, float ny, float nz);

    DynamicVertexBuilder index(int... i);

    void endVertex();

    void reset();

    SACVertexFormat format();

    ByteBuffer getVertexData();
    IntBuffer getIndexData();
    int getIndexCount();

    void start();

}
