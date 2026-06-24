package pl.epsi.gtsacore.api.renderer.data.dynamic;

import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.SACVertexFormatElement;

public class CastingTableDynMeshBuffer extends DynamicMeshBuffer {

    public static final SACVertexFormat FORMAT = SACVertexFormat.builder()
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 4)
            .withElement(SACVertexFormatElement.Type.FLOAT, 3)
            .withElement(SACVertexFormatElement.Type.FLOAT, 2)
            .withElement(SACVertexFormatElement.Type.FLOAT, 1)
            .build();

    private float progress;

    public CastingTableDynMeshBuffer(int size) {
        super(size);
    }

    public CastingTableDynMeshBuffer prog(float prog) {
        this.progress = prog;
        return this;
    }

    @Override
    public void endVertex() {
        this.ensureSpace(13 * Float.BYTES);

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

        this.buf.putFloat(this.progress);
    }

    @Override
    public SACVertexFormat format() {
        return FORMAT;
    }

}
