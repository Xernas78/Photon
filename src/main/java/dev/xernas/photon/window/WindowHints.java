package dev.xernas.photon.window;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

@Getter
@Setter
public class WindowHints {

    private boolean resizable;
    private boolean visible;
    private boolean decorated;
    private boolean vsync;
    private boolean maximized;
    private boolean isAzerty;

    public WindowHints() {
        this.resizable = true;
        this.visible = true;
        this.decorated = true;
        this.vsync = false;
        this.maximized = false;
        this.isAzerty = true;
    }

    public WindowHints(boolean resizable, boolean visible, boolean decorated, boolean vsync, boolean maximized, boolean isAzerty) {
        this.resizable = resizable;
        this.visible = visible;
        this.decorated = decorated;
        this.vsync = vsync;
        this.maximized = maximized;
        this.isAzerty = isAzerty;
    }

    public void apply() {
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, visible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, decorated ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);

        //TODO Faire gaffe à ça
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        // END

        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, maximized ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    public void applyOGL() {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 6);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
    }

    public void applyVulkan() {
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
    }

}
