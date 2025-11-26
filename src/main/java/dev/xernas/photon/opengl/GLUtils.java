package dev.xernas.photon.opengl;

import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.GLException;
import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallback;

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

    public static void clear(GLFramebuffer framebuffer, Color color) {
        GL45.glClearNamedFramebufferfv(framebuffer.getFramebufferId(), GL45.GL_COLOR, 0, new float[] {
                color.getRed() / 255f,
                color.getGreen() / 255f,
                color.getBlue() / 255f,
                color.getAlpha() / 255f
        });
        GL45.glClearNamedFramebufferfv(framebuffer.getFramebufferId(), GL45.GL_DEPTH, 0, new float[]{1f});
    }

    public static void viewport(Window window) {
        viewport(window.getWidth(), window.getHeight());
    }

    public static void viewport(int width, int height) {
        GL45.glViewport(0, 0, width, height);
    }

    public static String getRendererInfo() {
        return GL45.glGetString(GL45.GL_RENDERER);
    }

    public static void setupDebugMessageCallback() {
        GL45.glEnable(GL45.GL_DEBUG_OUTPUT);
        GL45.glEnable(GL45.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        // Ignorer les notifications
        GL45.glDebugMessageControl(GL45.GL_DONT_CARE, GL45.GL_DONT_CARE, GL45.GL_DEBUG_SEVERITY_NOTIFICATION, (java.nio.IntBuffer) null, false);

        GLDebugMessageCallback callback = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
            StringBuilder sb = new StringBuilder();
            sb.append("[Photon] New OpenGL Debug Message:\n");
            printDetail(sb, "Source", getSourceString(source));
            printDetail(sb, "Type", getTypeString(type));
            printDetail(sb, "ID", String.valueOf(id));
            printDetail(sb, "Severity", getSeverityString(severity));
            String messageStr = GLDebugMessageCallback.getMessage(length, message);
            printDetail(sb, "Message", messageStr);

            if (severity == GL45.GL_DEBUG_SEVERITY_HIGH || severity == GL45.GL_DEBUG_SEVERITY_MEDIUM) {
                System.err.println(sb);
                new GLException(messageStr).printStackTrace();
            } else System.out.println(sb);
        });
        GL45.glDebugMessageCallback(callback, 0);
    }

    private static void printDetail(StringBuilder sb, String type, String message) {
        sb.append("\t").append(type).append(": ").append(message).append("\n");
    }

    private static String getSourceString(int source) {
        return switch (source) {
            case GL45.GL_DEBUG_SOURCE_API -> "API";
            case GL45.GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "Window System";
            case GL45.GL_DEBUG_SOURCE_SHADER_COMPILER -> "Shader Compiler";
            case GL45.GL_DEBUG_SOURCE_THIRD_PARTY -> "Third Party";
            case GL45.GL_DEBUG_SOURCE_APPLICATION -> "Application";
            case GL45.GL_DEBUG_SOURCE_OTHER -> "Other";
            default -> "Unknown";
        };
    }

    private static String getTypeString(int type) {
        return switch (type) {
            case GL45.GL_DEBUG_TYPE_ERROR -> "Error";
            case GL45.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "Deprecated Behavior";
            case GL45.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "Undefined Behavior";
            case GL45.GL_DEBUG_TYPE_PORTABILITY -> "Portability";
            case GL45.GL_DEBUG_TYPE_PERFORMANCE -> "Performance";
            case GL45.GL_DEBUG_TYPE_MARKER -> "Marker";
            case GL45.GL_DEBUG_TYPE_PUSH_GROUP -> "Push Group";
            case GL45.GL_DEBUG_TYPE_POP_GROUP -> "Pop Group";
            case GL45.GL_DEBUG_TYPE_OTHER -> "Other";
            default -> "Unknown";
        };
    }

    private static String getSeverityString(int severity) {
        return switch (severity) {
            case GL45.GL_DEBUG_SEVERITY_HIGH -> "High";
            case GL45.GL_DEBUG_SEVERITY_MEDIUM -> "Medium";
            case GL45.GL_DEBUG_SEVERITY_LOW -> "Low";
            case GL45.GL_DEBUG_SEVERITY_NOTIFICATION -> "Notification";
            default -> "Unknown";
        };
    }

}
