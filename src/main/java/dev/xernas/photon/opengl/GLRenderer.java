package dev.xernas.photon.opengl;

import lombok.Getter;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class GLRenderer {

    @Getter
    private static int boundTextureUnit = GL_TEXTURE0;

    @Getter
    private static boolean depthTest = true;
    @Getter
    private static boolean wireFrame = false;

    public static void drawElements(int indicesCount) {
        glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
    }

    public static void enableDepthTest() {
        glEnable(GL_DEPTH_TEST);
        depthTest = true;
    }

    public static void disableDepthTest() {
        glDisable(GL_DEPTH_TEST);
        depthTest = false;
    }

    public static void enableWireframe() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        wireFrame = true;
    }

    public static void disableWireframe() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        wireFrame = false;
    }

    public static void enableBackfaceCulling() {
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    public static void disableBackfaceCulling() {
        glDisable(GL_CULL_FACE);
    }

    public static void enableVertexAttribArray(int attribute) {
        glEnableVertexAttribArray(attribute);
    }

    public static void disableVertexAttribArray(int attribute) {
        glDisableVertexAttribArray(attribute);
    }

    public static void setBoundTextureUnit(int unitId) {
        GLRenderer.boundTextureUnit = GL_TEXTURE0 + unitId;
    }

    public static void useBoundTextureUnit() {
        glActiveTexture(boundTextureUnit);
    }

    public static void bindTexture(int textureId) {
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    public static void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void clearColor(Color color) {
        glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static void clear(Color color) {
        clear();
        clearColor(color);
    }

}
