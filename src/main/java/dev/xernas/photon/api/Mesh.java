package dev.xernas.photon.api;

public class Mesh {

    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;
    private final float[] normals;

    public Mesh(float[] vertices, int[] indices, float[] texCoords, float[] normals) {
        this.vertices = vertices;
        this.indices = indices;
        this.texCoords = texCoords;
        this.normals = normals;
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
}
