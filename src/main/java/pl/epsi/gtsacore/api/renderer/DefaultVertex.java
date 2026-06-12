package pl.epsi.gtsacore.api.renderer;

import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormatElement;

import java.nio.ByteBuffer;

public class DefaultVertex implements Vertex {

    private final SACVertexFormat format;
    private final float x, y, z, r, g, b, a;

    public DefaultVertex(float x, float y, float z, float r, float g, float b, float a) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.format = SACVertexFormat.builder()
                .withElement(SACVertexFormatElement.Type.FLOAT, 3)
                .withElement(SACVertexFormatElement.Type.FLOAT, 4)
                .build();
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
    }

    @Override
    public SACVertexFormat vertexFormat() {
        return this.format;
    }

}
