package dev.xernas.photon.api.window.cursor;

import org.lwjgl.glfw.GLFW;

public enum CursorShape {

    ARROW(GLFW.GLFW_ARROW_CURSOR),
    IBEAM(GLFW.GLFW_IBEAM_CURSOR),
    CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
    HAND(GLFW.GLFW_HAND_CURSOR),
    HRESIZE(GLFW.GLFW_HRESIZE_CURSOR),
    VRESIZE(GLFW.GLFW_VRESIZE_CURSOR);

    private final int glfwShape;

    CursorShape(int glfwShape) {
        this.glfwShape = glfwShape;
    }

    public int getGlfwShape() {
        return glfwShape;
    }

}
