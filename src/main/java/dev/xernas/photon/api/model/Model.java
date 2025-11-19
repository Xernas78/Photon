package dev.xernas.photon.api.model;

import dev.xernas.photon.api.material.Material;

public class Model {

    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;
    private final float[] normals;
    private final Material material;

    public Model(float[] vertices, int[] indices, float[] texCoords, float[] normals, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.material = material;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getTexCoords() {
        return texCoords;
    }

    public float[] getNormals() {
        return normals;
    }

    public Material getMaterial() {
        return material;
    }
}
