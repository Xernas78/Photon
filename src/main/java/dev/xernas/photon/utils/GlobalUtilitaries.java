package dev.xernas.photon.utils;

import dev.xernas.photon.exceptions.PhotonException;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class GlobalUtilitaries {

    public static PhotonImage loadImage(Path iconPath) throws PhotonException {
        int width, height;
        ByteBuffer imageBuffer;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Use the MemoryStack for temporary storage
            IntBuffer widthBuffer = stack.mallocInt(1);
            IntBuffer heightBuffer = stack.mallocInt(1);
            IntBuffer channelsBuffer = stack.mallocInt(1);

            // Convert the Path to an InputStream
            try (InputStream inputStream = Files.newInputStream(iconPath)) {
                // Read the InputStream into a ByteBuffer
                byte[] bytes = inputStream.readAllBytes();
                imageBuffer = ByteBuffer.allocateDirect(bytes.length).put(bytes);
                imageBuffer.flip(); // Reset buffer position to zero for reading

                // Use STBImage to load the image from the ByteBuffer
                ByteBuffer buffer = STBImage.stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelsBuffer, 4);
                if (buffer == null) {
                    throw new PhotonException("Error loading image file: " + STBImage.stbi_failure_reason());
                }

                width = widthBuffer.get();
                height = heightBuffer.get();

                return new PhotonImage(width, height, buffer);
            }
        } catch (IOException e) {
            throw new PhotonException("Error loading image file: " + iconPath);
        }
    }

    public static int requireNotEquals(int obj, int notEqual, String message) throws PhotonException {
        if (obj == notEqual) throw new PhotonException(message);
        return obj;
    }

    public static Vector3f colorToVector3f(Color color) {
        return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public static Vector4f colorToVector4f(Color color) {
        return new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

}
