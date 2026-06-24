package pl.epsi.gtsacore.api.model;

public record ObjModel(
        ObjVertexFormat[] vertices,
        int[] indices
) {}