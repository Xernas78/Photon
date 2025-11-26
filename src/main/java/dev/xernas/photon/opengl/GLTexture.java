package dev.xernas.photon.opengl;

import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL45;

public class GLTexture implements ITexture {

    private static int lastBoundTextureId = 0;

    private final Texture image;
    private int textureId;

    public GLTexture(int width, int height) {
        this.image = new Texture(width, height, null);
    }

    public GLTexture(Texture image) {
        this.image = image;
    }

    @Override
    public void start() throws PhotonException {
        textureId = GL45.glCreateTextures(GL20.GL_TEXTURE_2D);

        int wrapMode = image.getData() != null ? GL20.GL_REPEAT : GL20.GL_CLAMP_TO_EDGE;
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_WRAP_S, wrapMode);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_WRAP_T, wrapMode);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
        GL45.glTextureParameteri(textureId, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);

        GL45.glTextureStorage2D(textureId, 1, GL20.GL_RGBA8, image.getWidth(), image.getHeight());
        if (image.getData() != null) {
            GL45.glTextureSubImage2D(textureId, 0, 0, 0, image.getWidth(), image.getHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, image.getData());
            GL45.glGenerateTextureMipmap(textureId);
        }
    }

    public void bind(int unit) {
        if (lastBoundTextureId == textureId) return;
        GL45.glBindTextureUnit(unit, textureId);
        lastBoundTextureId = textureId;
    }

    public void unbind(int unit) {
        GL45.glBindTextureUnit(unit, 0);
        lastBoundTextureId = 0;
    }

    @Override
    public void dispose() throws PhotonException {
        GL45.glDeleteTextures(textureId);
    }

    @Override
    public Texture getTexture() {
        return image;
    }

    public int getTextureId() {
        return textureId;
    }
}
