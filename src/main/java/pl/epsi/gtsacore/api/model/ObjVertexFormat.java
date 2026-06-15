package pl.epsi.gtsacore.api.model;

import pl.epsi.gtsacore.api.renderer.Vertex;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormatElement;

import java.nio.ByteBuffer;

public class ObjVertexFormat implements Vertex {

    private static final SACVertexFormat format = SACVertexFormat.builder()
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 2)
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .build();

    private final float x, y, z;
    private final float u, v;
    private final float nx, ny, nz;

    public ObjVertexFormat(float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.u = u;
        this.v = v;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
    }

    @Override
    public void putSelf(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);

        buffer.putFloat(u);
        buffer.putFloat(v);

        buffer.putFloat(nx);
        buffer.putFloat(ny);
        buffer.putFloat(nz);
    }

    @Override
    public SACVertexFormat vertexFormat() {
        return ObjVertexFormat.format;
    }
}
