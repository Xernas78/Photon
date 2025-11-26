package dev.xernas.photon.opengl;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.opengl.GL45;

public class GLRenderbuffer implements PhotonLogic {

    private int renderbufferId;

    private final int width;
    private final int height;

    public GLRenderbuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void start() throws PhotonException {
        renderbufferId = GL45.glCreateRenderbuffers();
        GL45.glNamedRenderbufferStorage(renderbufferId, GL45.GL_DEPTH24_STENCIL8, width, height);
    }

    @Override
    public void dispose() throws PhotonException {
        GL45.glDeleteRenderbuffers(renderbufferId);
    }

    public int getRenderbufferId() {
        return renderbufferId;
    }
}
