package dev.xernas.photon.opengl.utils;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.GlobalUtilitaries;
import dev.xernas.photon.utils.Image;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

public class GLUtils {

    public static int loadTexture(Path textPath) throws PhotonException {
        Image image = GlobalUtilitaries.loadImage(textPath);
        ByteBuffer buffer = image.getData();
        int width = image.getWidth();
        int height = image.getHeight();

        int id = GlobalUtilitaries.requireNotEquals(glGenTextures(), 0, "Error creating texture");
        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        STBImage.stbi_image_free(buffer);
        return id;
    }

}
