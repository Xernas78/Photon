package dev.xernas.photon.opengl;

import dev.xernas.photon.api.material.Material;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;

import java.util.ArrayList;
import java.util.List;

public class GLMesh implements IMesh {

    private static int lastBoundMeshId = 0;

    private final Model model;

    private VAO vao;
    private GLTexture texture;

    public GLMesh(Model model) {
        this.model = model;
    }

    @Override
    public int getVertexCount() {
        return model.getIndices().length;
    }

    @Override
    public Material getMaterial() {
        return model.getMaterial();
    }

    @Override
    public void start() throws PhotonException {
        // Checks
        if (model.getVertices() == null || model.getIndices() == null || model.getMaterial() == null) throw new PhotonException("Model vertices or indices are null");

        // Create buffers
        GLBufferObject verticesBuffer = new GLBufferObject(GLBufferObject.GLBufferType.VERTEX, GLBufferObject.GLDataType.VERTICES);
        GLBufferObject indicesBuffer = new GLBufferObject(GLBufferObject.GLBufferType.ELEMENT);
        GLBufferObject texCoordsBuffer = null;
        if (model.getTexCoords() != null && model.getTexCoords().length > 0) texCoordsBuffer = new GLBufferObject(GLBufferObject.GLBufferType.VERTEX, GLBufferObject.GLDataType.UVS);

        if (hasTexture()) texture = (GLTexture) model.getMaterial().getApiTexture();
        if (hasTexture()) texture.start();

        vao = new VAO(verticesBuffer, indicesBuffer, texCoordsBuffer);
        vao.start();

        // Store data in buffers
        verticesBuffer.storeBuffer(model.getVertices());
        indicesBuffer.storeBuffer(model.getIndices());
        if (texCoordsBuffer != null) {
            float[] src = model.getTexCoords();
            // Inverse V (y) : v -> 1 - v
            float[] flipped = new float[src.length];
            for (int i = 0; i < src.length; i += 2) {
                flipped[i] = src[i]; // u
                if (i + 1 < src.length) flipped[i + 1] = 1.0f - src[i + 1]; // v
            }
            texCoordsBuffer.storeBuffer(flipped);
        }

        // Create attributes
        vao.createBufferAttribute(verticesBuffer, 0);
        if (texCoordsBuffer != null) vao.createBufferAttribute(texCoordsBuffer, 1);
    }

    public boolean hasTexture() {
        return model.getTexCoords() != null && model.getTexCoords().length != 0 && model.getMaterial().hasTexture();
    }

    public void bind() {
        if (lastBoundMeshId != vao.getId()) GL45.glBindVertexArray(vao.getId());
        lastBoundMeshId = vao.getId();
        if (hasTexture()) texture.bind(0);
        GL45.glEnableVertexAttribArray(0);
        if (model.getTexCoords() != null && model.getTexCoords().length > 0) GL45.glEnableVertexAttribArray(1);
    }

    public void unbind() {
        if (model.getTexCoords() != null && model.getTexCoords().length > 0) GL45.glDisableVertexAttribArray(1);
        GL45.glDisableVertexAttribArray(0);
        if (hasTexture()) texture.unbind(0);
        GL45.glBindVertexArray(0);
        lastBoundMeshId = 0;
    }

    @Override
    public void dispose() throws PhotonException {
        if (hasTexture()) texture.dispose();
        vao.dispose();
    }

    public static class VAO implements PhotonLogic {

        private int id;
        private final List<GLBufferObject> buffers = new ArrayList<>();

        public VAO(GLBufferObject... buffers) {
            for (GLBufferObject buffer : buffers) {
                if (buffer == null) continue;
                this.buffers.add(buffer);
            }
        }

        @Override
        public void start() throws PhotonException {
            id = GL45.glCreateVertexArrays();
            for (GLBufferObject gLBufferObject : buffers) {
                gLBufferObject.start();
                gLBufferObject.attach(this);
            }
        }

        public void createBufferAttribute(GLBufferObject buffer, int attribute) throws PhotonException {
            GL45.glEnableVertexArrayAttrib(id, attribute);
            GL45.glVertexArrayAttribFormat(id, attribute, buffer.dataType.getSize(), buffer.dataType.getGlConstant(), false, 0);
            GL45.glVertexArrayAttribBinding(id, attribute, buffer.dataType.getBindingIndex());
        }

        @Override
        public void dispose() throws PhotonException {
            for (GLBufferObject gLBufferObject : buffers) gLBufferObject.dispose();
            GL45.glDeleteVertexArrays(id);
        }

        public int getId() {
            return id;
        }
    }

    public static class GLBufferObject implements PhotonLogic {

        private final GLBufferType type;
        private GLDataType dataType;
        private int id;

        public GLBufferObject(GLBufferType type) {
            this.type = type;
        }

        public GLBufferObject(GLBufferType type, GLDataType dataType) {
            this.type = type;
            this.dataType = dataType;
        }

        @Override
        public void start() throws PhotonException {
            id = GL45.glCreateBuffers();
        }

        public void attach(VAO vao) throws PhotonException {
            switch (type) {
                case VERTEX -> {
                    if (dataType == null) throw new PhotonException("Data type must be specified for vertex buffer");
                    GL45.glVertexArrayVertexBuffer(vao.getId(), dataType.getBindingIndex(), id, 0, dataType.getStride());
                }
                case ELEMENT -> GL45.glVertexArrayElementBuffer(vao.getId(), id);
            }
        }

        public void storeBuffer(float[] floats) {
            GL45.glNamedBufferData(id, floats, GL20.GL_STATIC_DRAW);
        }

        public void storeBuffer(int[] ints) {
            GL45.glNamedBufferData(id, ints, GL20.GL_STATIC_DRAW);
        }

        @Override
        public void dispose() throws PhotonException {
            GL45.glDeleteBuffers(id);
        }

        public int getId() {
            return id;
        }

        public enum GLDataType {

            VERTICES(0, 3, GL20.GL_FLOAT, Float.BYTES * 3),
            UVS(1, 2, GL20.GL_FLOAT, Float.BYTES * 2),
            NORMALS(2, 3, GL20.GL_FLOAT, Float.BYTES * 3);

            private final int bindingIndex;
            private final int size;
            private final int glConstant;
            private final int stride;

            GLDataType(int bindingIndex, int size, int glConstant, int stride) {
                this.bindingIndex = bindingIndex;
                this.size = size;
                this.glConstant = glConstant;
                this.stride = stride;
            }

            public int getBindingIndex() {
                return bindingIndex;
            }

            public int getSize() {
                return size;
            }

            public int getGlConstant() {
                return glConstant;
            }

            public int getStride() {
                return stride;
            }
        }

        public enum GLBufferType {

            VERTEX,
            ELEMENT

        }

    }

}
