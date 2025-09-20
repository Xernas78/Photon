package dev.xernas.photon.opengl.font;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;
import dev.xernas.photon.opengl.IOGLLogic;
import dev.xernas.photon.render.font.IFont;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GLFont implements IFont, IBindeable {

    private static final int BITMAP_W = 512;   // Width of atlas
    private static final int BITMAP_H = 512;   // Height of atlas
    private static final int FIRST_CHAR = 32;  // ASCII start
    private static final int NUM_CHARS = 96;   // ASCII 32..127

    private final byte[] fontData;              // Font data
    private final float pixelHeight;           // Font size in pixels

    private ByteBuffer bitmap;
    private STBTTBakedChar.Buffer cdata;

    public GLFont(byte[] fontData, float pixelHeight) {
        this.fontData = fontData;
        this.pixelHeight = pixelHeight;
    }

    @Override
    public void init() throws PhotonException {
        ByteBuffer ttf = MemoryUtil.memAlloc(fontData.length).put(fontData).flip();

        // Allocate glyph buffer
        cdata = STBTTBakedChar.malloc(NUM_CHARS);

        // Allocate bitmap for atlas
        bitmap = MemoryUtil.memAlloc(BITMAP_W * BITMAP_H);

        // Bake font bitmap
        STBTruetype.stbtt_BakeFontBitmap(ttf, pixelHeight, bitmap, BITMAP_W, BITMAP_H, FIRST_CHAR, cdata);

        MemoryUtil.memFree(bitmap);
        MemoryUtil.memFree(ttf);
    }

    public static int getBitmapHeight() {
        return BITMAP_H;
    }

    public static int getBitmapWidth() {
        return BITMAP_W;
    }

    public ByteBuffer getBitmap() {
        return bitmap;
    }

    public STBTTBakedChar.Buffer getCharData() {
        return cdata;
    }

    @Override
    public void cleanup() {
        cdata.free();
    }

    @Override
    public void bind() {

    }

    @Override
    public void unbind() {

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
