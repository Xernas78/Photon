package dev.xernas.photon.utils;

import dev.xernas.photon.api.model.Model;

public class Models {

    public static Model createQuad(float size) {
        float halfSize = size / 2.0f;
        Model.Vertex[] vertices = {
                new Model.Vertex(-halfSize, -halfSize, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f),
                new Model.Vertex(halfSize, -halfSize, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f),
                new Model.Vertex(halfSize, halfSize, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f),
                new Model.Vertex(-halfSize, halfSize, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f)
        };
        int[] indices = {
                0, 1, 2,
                2, 3, 0
        };
        return new Model(vertices, indices);
    }
    public static Model createQuad() {
        return createQuad(1.0f);
    }

    public static Model createCube(float size) {
        float halfSize = size / 2.0f;
        Model.Vertex[] vertices = {
                new Model.Vertex(-halfSize, -halfSize, -halfSize, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f), // Front face vert 1
                new Model.Vertex(halfSize, -halfSize, -halfSize, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f), // Front face vert 2
                new Model.Vertex(halfSize, halfSize, -halfSize, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f), // Front face vert 3
                new Model.Vertex(-halfSize, halfSize, -halfSize, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f), // Front face vert 4

                new Model.Vertex(-halfSize, -halfSize, halfSize, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f), // Back face vert 1
                new Model.Vertex(halfSize, -halfSize, halfSize, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f), // Back face vert 2
                new Model.Vertex(halfSize, halfSize, halfSize, 0.0f, 1.0f, 0.0f, 0.0f, -1.0f), // Back face vert 3
                new Model.Vertex(-halfSize, halfSize, halfSize, 1.0f, 1.0f, 0.0f, 0.0f, -1.0f), // Back face vert 4

                new Model.Vertex(halfSize, -halfSize, -halfSize, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f), // Right face vert 1
                new Model.Vertex(halfSize, -halfSize, halfSize, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f), // Right face vert 2
                new Model.Vertex(halfSize, halfSize, halfSize, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f), // Right face vert 3
                new Model.Vertex(halfSize, halfSize, -halfSize, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f), // Right face vert 4

                new Model.Vertex(-halfSize, -halfSize, -halfSize, 1.0f, 0.0f, -1.0f, 0.0f, 0.0f), // Left face vert 1
                new Model.Vertex(-halfSize, -halfSize, halfSize, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f), // Left face vert 2
                new Model.Vertex(-halfSize, halfSize, halfSize, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f), // Left face vert 3
                new Model.Vertex(-halfSize, halfSize, -halfSize, 1.0f, 1.0f, -1.0f, 0.0f, 0.0f), // Left face vert 4

                new Model.Vertex(-halfSize, halfSize, -halfSize, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f), // Top face vert 1
                new Model.Vertex(-halfSize, halfSize, halfSize, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f), // Top face vert 2
                new Model.Vertex(halfSize, halfSize, -halfSize, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f), // Top face vert 3
                new Model.Vertex(halfSize, halfSize, halfSize, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f), // Top face vert 4

                new Model.Vertex(-halfSize, -halfSize, -halfSize, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f), // Bottom face vert 1
                new Model.Vertex(-halfSize, -halfSize, halfSize, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f), // Bottom face vert 2
                new Model.Vertex(halfSize, -halfSize, -halfSize, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f), // Bottom face vert 3
                new Model.Vertex(halfSize, -halfSize, halfSize, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f) // Bottom face vert 4
        };
        int[] indices = {
                0, 1, 2, 2, 3, 0, // Front face
                4, 5, 6, 6, 7, 4, // Back face
                8, 9, 10, 10, 11, 8, // Right face
                12, 13, 14, 14, 15, 12, // Left face
                16, 18, 19, 19, 17, 16, // Top face
                20, 22, 23, 23, 21, 20  // Bottom face
        };
        return new Model(vertices, indices);
    }
    public static Model createCube() {
        return createCube(1.0f);
    }

}
