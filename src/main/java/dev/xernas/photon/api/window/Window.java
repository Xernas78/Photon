package dev.xernas.photon.window;

import dev.xernas.photon.Library;
import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.api.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Window implements PhotonLogic {

    private final boolean resizable;

    private final String defaultTitle;
    private String title;
    private int width;
    private int height;
    private boolean vsync;

    private long handle;
    private GLFWErrorCallback errorCallback;

    public Window(String title, int width, int height) {
        this(true, false, title, width, height);
    }

    public Window(boolean resizable, boolean vsync, String title, int width, int height) {
        this.resizable = resizable;
        this.vsync = vsync;
        this.defaultTitle = title;
        this.title = title;
        this.width = width;
        this.height = height;
    }

    @Override
    public void start() throws PhotonException {
        // Initialize GLFW
        if (!PhotonAPI.isInitialized()) throw new PhotonException("PhotonAPI not initialized");
        if (PhotonAPI.isDebug()) {
            errorCallback = GLFWErrorCallback.createPrint(System.err);
            errorCallback.set();
        }
        if (!GLFW.glfwInit()) throw new PhotonException("Failed to initialize GLFW");

        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, PhotonAPI.getLibrary().isVulkan() ? GLFW.GLFW_NO_API : GLFW.GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, resizable ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
        if (PhotonAPI.getLibrary().isOpenGL()) {
            String[] version = PhotonAPI.getLibrary().getVersion().split("\\.");
            int major = Integer.parseInt(version[0]);
            int minor = Integer.parseInt(version[1]);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
            if (PhotonAPI.isDebug()) GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        }

        // Create the window
        handle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (handle == MemoryUtil.NULL) throw new PhotonException("Failed to create GLFW window");

        // Callbacks
        GLFW.glfwSetFramebufferSizeCallback(handle, (window, w, h) -> {
            this.width = w;
            this.height = h;
        });
        //TODO: Key callback

        // Center the window
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(handle, pWidth, pHeight);
            GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            if (vidMode != null) {
                GLFW.glfwSetWindowPos(
                        handle,
                        (vidMode.width() - pWidth.get(0)) / 2,
                        (vidMode.height() - pHeight.get(0)) / 2
                );
            }
        }
    }

    public void update(Renderer renderer) {
        renderer.swapBuffers();
        GLFW.glfwPollEvents();
    }

    public void show() {
        GLFW.glfwShowWindow(handle);
        GLFW.glfwRestoreWindow(handle);
    }

    public void minimize() {
        GLFW.glfwIconifyWindow(handle);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        GLFW.glfwSetWindowSize(handle, width, height);
    }

    public long getHandle() {
        return handle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isVsync() {
        return vsync;
    }

    @Override
    public void dispose() throws PhotonException {
        if (handle != MemoryUtil.NULL){
            Callbacks.glfwFreeCallbacks(handle);
            GLFW.glfwDestroyWindow(handle);
        }
        GLFW.glfwTerminate();
        if (errorCallback != null)  {
            errorCallback.free();
            errorCallback = null;
        }
    }
}
