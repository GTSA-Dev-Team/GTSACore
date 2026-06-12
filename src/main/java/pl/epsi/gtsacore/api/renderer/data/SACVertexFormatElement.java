package pl.epsi.gtsacore.api.renderer.data;

import lombok.Getter;
import org.lwjgl.opengl.GL45;

public class SACVertexFormatElement {

    @Getter
    private final Type type;
    @Getter
    private final int index;
    @Getter
    private final int stride;
    @Getter
    private final int count;

    public SACVertexFormatElement(Type type, int index, int count) {
        this.type = type;
        this.index = index;
        this.count = count;
        this.stride = this.count * type.getSize();
    }

    public long setupState(long last, int stride) {
        GL45.glEnableVertexAttribArray(index);
        GL45.glVertexAttribPointer(index, count, type.getGlType(), false, stride, last);
        return last + this.stride;
    }

    public void cleanupState() {
        GL45.glDisableVertexAttribArray(index);
    }

    public enum Type {

        BYTE(1,GL45.GL_BYTE),
        UBYTE(1,GL45.GL_UNSIGNED_BYTE),
        SHORT(2, GL45.GL_SHORT),
        USHORT(2, GL45.GL_SHORT),
        INT(4, GL45.GL_INT),
        UINT(4, GL45.GL_UNSIGNED_INT),
        FLOAT(4, GL45.GL_FLOAT);

        private final int size;
        private final int glType;

        Type(int size, int glType) {
            this.size = size;
            this.glType = glType;
        }

        public int getSize() {
            return size;
        }

        public int getGlType() {
            return glType;
        }

    }

}
