package dev.xernas.photon.opengl;

import dev.xernas.photon.api.window.Window;
import org.lwjgl.opengl.GL45;

import java.awt.*;

public class GLUtils {

    public static void draw(int first, int count) {
        if (OpenGLConstants.DRAWING_METHOD.equals(OpenGLConstants.DrawingMethod.ARRAY)) GL45.glDrawArrays(OpenGLConstants.DRAW_MODE.toOpenGLConstant(), first, count);
        else GL45.glDrawElements(OpenGLConstants.DRAW_MODE.toOpenGLConstant(), count, GL45.GL_UNSIGNED_INT, first);
    }

    public static void clear() {
        GL45.glClear(GL45.GL_COLOR_BUFFER_BIT | GL45.GL_DEPTH_BUFFER_BIT);
    }

    public static void clear(Color color) {
        clear();
        if (color != null) GL45.glClearColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

    public static void viewport(Window window) {
        GL45.glViewport(0, 0, window.getWidth(), window.getHeight());
    }

    public static String getRendererInfo() {
        return GL45.glGetString(GL45.GL_RENDERER);
    }


}
