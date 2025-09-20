package dev.xernas.photon.window;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IOGLLogic;
import org.lwjgl.glfw.GLFW;

public class Cursor implements IOGLLogic {

    private long handle;

    private final CursorShape shape;

    public Cursor(CursorShape shape) {
        this.shape = shape;
    }

    @Override
    public void init() throws PhotonException {
        handle = GLFW.glfwCreateStandardCursor(shape.getGlfwShape());
    }

    @Override
    public void cleanup() {
        GLFW.glfwDestroyCursor(handle);
    }

    public long getHandle() {
        return handle;
    }

}
