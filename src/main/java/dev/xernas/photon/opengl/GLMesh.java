package dev.xernas.photon.opengl;

import dev.xernas.photon.api.IMesh;
import dev.xernas.photon.api.Mesh;
import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GLMesh implements IMesh {

    private final Mesh mesh;

    private VAO vao;

    public GLMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    @Override
    public int getVertexCount() {
        return mesh.getVertices().length / 3;
    }

    @Override
    public void start() throws PhotonException {
        VBO verticesVBO = new VBO();
        vao = new VAO(verticesVBO);
        vao.start();

        verticesVBO.storeFloats(mesh.getVertices());
        vao.storeVBOInAttribute(0, 0);
    }

    public void bind() {
        GL30.glBindVertexArray(vao.getId());
        GL30.glEnableVertexAttribArray(0);
    }

    public void unbind() {
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void dispose() throws PhotonException {
        vao.dispose();
    }

    public static class VAO implements PhotonLogic {

        private int id;
        private final List<VBO> vbos = new ArrayList<>();

        public VAO(VBO... vbos) {
            this.vbos.addAll(Arrays.asList(vbos));
        }

        @Override
        public void start() throws PhotonException {
            id = GL30.glGenVertexArrays();
            GL30.glBindVertexArray(id);
            for (VBO vbo : vbos) vbo.start();
            GL30.glBindVertexArray(0);
        }

        public void storeVBOInAttribute(int vboIndex, int attribute) throws PhotonException {
            VBO vbo = vbos.get(vboIndex);
            if (vbo == null) throw new PhotonException("Invalid VBO Index");
            GL30.glBindVertexArray(id);
            GL30.glEnableVertexAttribArray(attribute);
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo.getId());
            GL30.glVertexAttribPointer(attribute, 3, GL20.GL_FLOAT, false, 0, 0);
            GL30.glDisableVertexAttribArray(attribute);
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
            GL30.glBindVertexArray(0);
        }

        @Override
        public void dispose() throws PhotonException {
            for (VBO vbo : vbos) vbo.dispose();
            GL30.glDeleteVertexArrays(id);
        }

        public int getId() {
            return id;
        }
    }

    public static class VBO implements PhotonLogic {

        private int id;

        @Override
        public void start() throws PhotonException {
            id = GL20.glGenBuffers();
        }

        public void storeFloats(float[] floats) {
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, id);
            GL20.glBufferData(GL20.GL_ARRAY_BUFFER, floats, GL20.GL_STATIC_DRAW);
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        }

        @Override
        public void dispose() throws PhotonException {
            GL20.glDeleteBuffers(id);
        }

        public int getId() {
            return id;
        }
    }

}
