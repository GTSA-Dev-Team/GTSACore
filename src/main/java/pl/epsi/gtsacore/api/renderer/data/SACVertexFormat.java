package pl.epsi.gtsacore.api.renderer.data;

import lombok.Getter;

import java.util.ArrayList;

public class SACVertexFormat {

    private final ArrayList<SACVertexFormatElement> elements;

    @Getter
    private final int stride;

    public SACVertexFormat(ArrayList<SACVertexFormatElement> elements) {
        this.elements = elements;
        this.stride = this.elements.stream().mapToInt(SACVertexFormatElement::getStride).sum();
    }

    public void setupState() { elements.stream().reduce(0L, (value, format) -> format.setupState(value, stride), (a, b) -> b); }
    public void cleanupState() { elements.forEach(SACVertexFormatElement::cleanupState); }

    public static SACVertexFormatBuilder builder() {
        return new SACVertexFormatBuilder();
    }

    public static class SACVertexFormatBuilder {

        private int index = 0;

        private final ArrayList<SACVertexFormatElement> elements = new ArrayList<>();

        private SACVertexFormatBuilder() {}

        public SACVertexFormatBuilder withElement(SACVertexFormatElement.Type type, int count) {
            this.elements.add(new SACVertexFormatElement(type, index++, count));
            return this;
        }

        public SACVertexFormat build() {
            return new SACVertexFormat(this.elements);
        }

    }

}
