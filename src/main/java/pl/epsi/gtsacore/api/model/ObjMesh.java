package pl.epsi.gtsacore.api.model;

public record ObjMesh(
        ObjVertexFormat[] vertices,
        int[] indices
) {}