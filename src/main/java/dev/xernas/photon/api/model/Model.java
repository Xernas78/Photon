package dev.xernas.photon.api.model;

public class Model {

    private final float[] vertices;
    private final int[] indices;
    private final float[] texCoords;
    private final float[] normals;

    private boolean flipV = true;

    public Model(float[] vertices, int[] indices, float[] texCoords, float[] normals) {
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
        if (texCoords == null) return null;
        if (!flipV) return texCoords;
        // Inverse V (y) : v -> 1 - v
        float[] flipped = new float[texCoords.length];
        for (int i = 0; i < texCoords.length; i += 2) {
            flipped[i] = texCoords[i]; // u
            if (i + 1 < texCoords.length) flipped[i + 1] = 1.0f - texCoords[i + 1]; // v
        }
        return flipped;
    }

    public float[] getNormals() {
        return normals;
    }

    public boolean is3D() {
        return vertices[2] != 0f;
    }

    public Model flipV() {
        flipV = !flipV;
        return this;
    }
}
