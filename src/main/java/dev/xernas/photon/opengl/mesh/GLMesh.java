package dev.xernas.photon.opengl.mesh;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;
import dev.xernas.photon.opengl.GLRenderer;
import dev.xernas.photon.opengl.utils.BufferUtils;
import dev.xernas.photon.opengl.utils.GLUtils;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.Material;
import lombok.Getter;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class GLMesh implements IMesh, IBindeable {

    private final float[] vertices;
    private final int[] indices;
    private final float[] normals;
    private final float[] textureCoords;
    private final Material material;

    private VAO vao;

    private IntBuffer indicesBuffer;
    private FloatBuffer verticesBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer textureCoordsBuffer;
    private int textureID;
    private boolean hasTexture;

    public GLMesh(float[] vertices, int[] indices, float[] normals, float[] textureCoords, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.normals = normals;
        this.textureCoords = textureCoords;
        this.material = material;
    }

    @Override
    public void init() throws PhotonException {
        vao = new VAO();
        vao.init();
        bind();
        indicesBuffer = vao.storeIndicesBuffer(indices);
        verticesBuffer = vao.storeDataInAttributeList(0, 3, vertices);
        normalsBuffer = normals == null ? null : vao.storeDataInAttributeList(1, 3, normals);
        textureCoordsBuffer = textureCoords == null ? null : vao.storeDataInAttributeList(2, 2, textureCoords);
        textureID = material.getTexture() == null ? 0 : GLUtils.loadTexture(material.getTexture());
        hasTexture = textureID != 0 && textureCoords != null && material.getTexture() != null;
        unbind();
    }

    @Override
    public void bind() {
        vao.bind();
        if (hasTexture()) glBindTexture(GL_TEXTURE_2D, textureID);
        GLRenderer.enableVertexAttribArray(0);
        if (hasNormals()) GLRenderer.enableVertexAttribArray(1);
        if (hasTexture()) GLRenderer.enableVertexAttribArray(2);
    }

    @Override
    public void unbind() {
        vao.unbind();
        if (hasTexture()) glBindTexture(GL_TEXTURE_2D, 0);
        GLRenderer.disableVertexAttribArray(0);
        if (hasNormals()) GLRenderer.disableVertexAttribArray(1);
        if (hasTexture()) GLRenderer.disableVertexAttribArray(2);
    }

    @Override
    public void cleanup() {
        vao.cleanup();
        if (hasTexture()) glDeleteTextures(textureID);
        MemoryUtil.memFree(verticesBuffer);
        MemoryUtil.memFree(indicesBuffer);
        if (hasNormals()) MemoryUtil.memFree(normalsBuffer);
        if (hasTexture()) MemoryUtil.memFree(textureCoordsBuffer);
    }

    @Override
    public void use() {
        bind();
    }

    @Override
    public void disuse() {
        unbind();
    }

    @Override
    public void dispose() {
        cleanup();
    }

    public int getIndicesCount() {
        return indices.length;
    }

    @Override
    public boolean hasNormals() {
        return normals != null;
    }

    @Override
    public boolean hasTexture() {
        return hasTexture;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Getter
    public static class VAO implements IBindeable {

        private int id;
        private final List<VBO> vbos = new ArrayList<>();

        @Override
        public void bind() {
            glBindVertexArray(id);
            for (VBO vbo : vbos) vbo.bind();
        }

        @Override
        public void unbind() {
            glBindVertexArray(0);
            for (VBO vbo : vbos) vbo.unbind();
        }

        @Override
        public void init() throws PhotonException {
            id = glGenVertexArrays();
        }

        @Override
        public void cleanup() {
            glDeleteVertexArrays(id);
            for (VBO vbo : vbos) vbo.cleanup();
        }

        public IntBuffer storeIndicesBuffer(int[] indices) throws PhotonException {
            int type = GL_ELEMENT_ARRAY_BUFFER;
            GLMesh.VAO.VBO vbo = new GLMesh.VAO.VBO(type);
            vbo.init();
            vbo.bind();
            vbos.add(vbo);
            IntBuffer buffer = BufferUtils.storeIntsInBuffer(indices);
            glBufferData(type, buffer, GL_STATIC_DRAW);
            return buffer;
        }

        public FloatBuffer storeDataInAttributeList(int attributeNumber, int size, float[] data) throws PhotonException {
            int type = GL_ARRAY_BUFFER;
            GLMesh.VAO.VBO vbo = new GLMesh.VAO.VBO(type);
            vbo.init();
            vbo.bind();
            vbos.add(vbo);
            FloatBuffer buffer = BufferUtils.storeFloatsInBuffer(data);
            glBufferData(type, buffer, GL_STATIC_DRAW);
            glVertexAttribPointer(attributeNumber, size, GL_FLOAT, false, 0, 0);
            vbo.unbind();
            return buffer;
        }

        @Getter
        public static class VBO implements IBindeable {

            private int id;
            private final int type;

            public VBO(int type) {
                this.type = type;
            }

            @Override
            public void bind() {
                glBindBuffer(type, id);
            }

            @Override
            public void unbind() {
                glBindBuffer(type, 0);
            }

            @Override
            public void init() throws PhotonException {
                id = glGenBuffers();
            }

            @Override
            public void cleanup() {
                glDeleteBuffers(id);
            }
        }

    }
}
