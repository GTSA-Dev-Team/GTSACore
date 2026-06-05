package pl.epsi.gtsacore.api.renderer;

import pl.epsi.gtsacore.api.renderer.data.SACVertexFormat;

import java.nio.ByteBuffer;

public interface Vertex {

    void putSelf(ByteBuffer buffer);

    default int size() { return vertexFormat().getStride(); }

    SACVertexFormat vertexFormat();

}
