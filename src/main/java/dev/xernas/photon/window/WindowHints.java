package dev.xernas.photon.window;

import dev.xernas.photon.utils.PhotonImage;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;

@Getter
@Setter
public class WindowHints {

    private PhotonImage icon;
    private boolean resizable;
    private boolean visible;
    private boolean decorated;
    private boolean vsync;
    private boolean maximized;
    private boolean isAzerty;

    public WindowHints() {
        this(null, true, true, true, false, false, true);
    }

    public WindowHints(PhotonImage icon) {
        this(icon, true, true, true, false, false, true);
    }

    public WindowHints(PhotonImage icon, boolean resizable, boolean visible, boolean decorated, boolean vsync, boolean maximized, boolean isAzerty) {
        this.icon = icon;
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
