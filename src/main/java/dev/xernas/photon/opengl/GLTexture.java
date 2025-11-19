package dev.xernas.photon.opengl;

import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;

public class GLTexture implements ITexture {

    private final Texture image;
    private int textureId;

    private boolean bound;

    public GLTexture(Texture image) {
        this.image = image;
    }

    @Override
    public void start() throws PhotonException {
        textureId = GL45.glCreateTextures(GL20.GL_TEXTURE_2D);

        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);

        GL45.glTextureStorage2D(textureId, 1, GL20.GL_RGBA8, image.getWidth(), image.getHeight());
        GL45.glTextureSubImage2D(textureId, 0, 0, 0, image.getWidth(), image.getHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, image.getData());
        GL45.glGenerateMipmap(textureId);
    }

    public void bind(int unit) {
        if (bound) return;
        GL45.glBindTextureUnit(unit, textureId);
        bound = true;
    }

    public void unbind(int unit) {
        if (!bound) return;
        GL45.glBindTextureUnit(unit, 0);
        bound = false;
    }

    @Override
    public void dispose() throws PhotonException {
        GL45.glDeleteTextures(textureId);
    }

}
