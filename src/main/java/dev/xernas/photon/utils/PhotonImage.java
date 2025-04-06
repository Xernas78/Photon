package dev.xernas.photon.utils;

import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
public class PhotonImage {

    private final int width;
    private final int height;
    private final ByteBuffer data;

    public PhotonImage(int width, int height, ByteBuffer data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public static PhotonImage fromByteArray(int width, int height, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();
        return new PhotonImage(width, height, buffer);
    }
    
}