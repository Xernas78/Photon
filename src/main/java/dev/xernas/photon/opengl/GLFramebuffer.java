package dev.xernas.photon.opengl;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IFramebuffer;
import lombok.Getter;

import static org.lwjgl.opengl.GL30.*;

public class GLFramebuffer implements IFramebuffer, IBindeable {

    private int framebufferID;
    @Getter
    private GLTexture attachedTexture;
    private int renderbufferID;
    private int width;
    private int height;

    public GLFramebuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void cleanup() {
        glDeleteFramebuffers(framebufferID);
        if (renderbufferID != 0) glDeleteRenderbuffers(renderbufferID);
        if (attachedTexture != null) attachedTexture.cleanup();
    }

    @Override
    public void init() throws PhotonException {
        framebufferID = glGenFramebuffers();
        storeAttachments();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void resize(int width, int height) throws PhotonException {
        // Erase the old resources
        if (attachedTexture != null) attachedTexture.cleanup();
        if (renderbufferID != 0) glDeleteRenderbuffers(renderbufferID);

        // Update the width and height
        this.width = width;
        this.height = height;

        storeAttachments();
    }

    private void storeAttachments() throws PhotonException {
        bind();
        // Recreate the framebuffer and its attachments
        attachedTexture = new GLTexture(width, height, false, true);
        attachedTexture.init();
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, attachedTexture.getTextureID(), 0);

        renderbufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, renderbufferID);

        // Check if the framebuffer is complete
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) throw new PhotonException("Framebuffer is not complete");

        unbind();
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
}
