package dev.xernas.photon.opengl;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.utils.GlobalUtilitaries;
import lombok.Getter;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLTexture implements ITexture, IBindeable {

    @Getter
    private int textureID;
    private int width;
    private int height;
    @Getter
    private final ByteBuffer data;
    private final boolean alpha;
    private final boolean clampToEdge;

    public GLTexture(int width, int height, boolean alpha, boolean clampToEdge) {
        this(width, height, null, alpha, clampToEdge);
    }

    public GLTexture(int width, int height, ByteBuffer data) {
        this(width, height, data, true, false);
    }

    public GLTexture(int width, int height, ByteBuffer data, boolean alpha, boolean clampToEdge) {
        this.width = width;
        this.height = height;
        this.data = data;
        this.alpha = alpha;
        this.clampToEdge = clampToEdge;
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
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
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

    @Override
    public void init() throws PhotonException {
        textureID = GlobalUtilitaries.requireNotEquals(glGenTextures(), 0, "Error creating texture");
        glBindTexture(GL_TEXTURE_2D, textureID);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        if (data != null) glTexImage2D(GL_TEXTURE_2D, 0, alpha ? GL_RGBA : GL_RGB, width, height, 0, alpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, data);
        else glTexImage2D(GL_TEXTURE_2D, 0, alpha ? GL_RGBA : GL_RGB, width, height, 0, alpha ? GL_RGBA : GL_RGB, GL_UNSIGNED_BYTE, 0);
        if (data != null) glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, clampToEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, clampToEdge ? GL_CLAMP_TO_EDGE : GL_REPEAT);
        if (data != null) STBImage.stbi_image_free(data);
    }

    @Override
    public void bind() {
        GLRenderer.useBoundTextureUnit();
        GLRenderer.bindTexture(textureID);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void cleanup() {
        glDeleteTextures(textureID);
    }

}
