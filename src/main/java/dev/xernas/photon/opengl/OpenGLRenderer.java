package dev.xernas.photon.opengl;

import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.api.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

public class OpenGLRenderer implements Renderer {

    private final Window window;
    private final boolean vsync;
    private final boolean debug;

    public OpenGLRenderer(Window window, boolean vsync, boolean debug) {
        this.window = window;
        this.vsync = vsync;
        this.debug = debug;
    }

    @Override
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window.getHandle());
    }

    @Override
    public void start() throws PhotonException {
        if (!PhotonAPI.isInitialized()) throw new PhotonException("PhotonAPI not initialized");
        GLFW.glfwMakeContextCurrent(window.getHandle());
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
        if (debug) GLUtil.setupDebugMessageCallback(System.err);
    }

    @Override
    public void dispose() throws PhotonException {

    }
}
