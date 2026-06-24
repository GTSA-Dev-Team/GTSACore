package pl.epsi.gtsacore.api.renderer.data.st;

import pl.epsi.gtsacore.api.renderer.Vertex;

public record Mesh<T extends Vertex>(StaticVertexBuffer<T> data, Material material) { }