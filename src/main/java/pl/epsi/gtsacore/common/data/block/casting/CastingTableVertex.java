package pl.epsi.gtsacore.common.data.block.casting;

import pl.epsi.gtsacore.api.renderer.Vertex;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormatElement;

import java.nio.ByteBuffer;

public class CastingTableVertex implements Vertex {

    public static final CastingTableVertex TEMPLATE = new CastingTableVertex(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);

    public static final SACVertexFormat FORMAT = SACVertexFormat.builder()
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 4)
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 2)
            .withElement(SACVertexFormatElement.Type.FLOAT, 1)
            .build();

    private final float x, y, z;
    private final float r, g, b, a;
    private final float nx, ny, nz;
    private final float u, v;
    private final float prog;

    public CastingTableVertex(float x, float y, float z, float r, float g, float b, float a, float nx, float ny, float nz, float u, float v, float prog) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.u = u;
        this.v = v;
        this.prog = prog;
    }
    public CastingTableVertex(float x, float y, float z, int rgba, float nx, float ny, float nz, float u, float v, float prog) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.a = ((rgba >>> 24)  & 0xFF) / 255.0f;
        this.r = ((rgba >>> 16) & 0xFF) / 255.0f;
        this.g = ((rgba >>> 8) & 0xFF) / 255.0f;
        this.b = ((rgba      )  & 0xFF) / 255.0f;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.u = u;
        this.v = v;
        this.prog = prog;
    }

    @Override
    public void putSelf(ByteBuffer buffer) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(z);

        buffer.putFloat(r);
        buffer.putFloat(g);
        buffer.putFloat(b);
        buffer.putFloat(a);

        buffer.putFloat(nx);
        buffer.putFloat(ny);
        buffer.putFloat(nz);

        buffer.putFloat(u);
        buffer.putFloat(v);

        buffer.putFloat(prog);
    }

    @Override
    public SACVertexFormat vertexFormat() {
        return FORMAT;
    }
}
