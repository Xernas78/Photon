package dev.xernas.photon.opengl;

import dev.xernas.photon.input.Action;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;
import dev.xernas.photon.window.WindowHints;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.window.IWindow;
import lombok.Getter;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

@Getter
public class GLWindow implements IWindow {

    private long windowHandle;

    private final String defaultTitle;
    private String title;
    private int width, height;
    private Color color = Color.BLACK;
    private final WindowHints hints;
    private final Input input;

    public GLWindow(String title, int width, int height, WindowHints hints) {
        this.defaultTitle = title;
        this.title = title;
        this.width = width;
        this.height = height;
        this.hints = hints;
        this.input = new Input(this, hints.isAzerty());
    }


    @Override
    public void init() throws PhotonException {
        GLFW.glfwDefaultWindowHints();
        hints.apply();
        windowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == 0) {
            throw new PhotonException("Failed to create window");
        }

        GLFW.glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        GL11.glViewport(0, 0, width, height);
        // Resize
        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> resize(width, height));
        // Keyboard
        GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> input.setKeyAction(Key.fromCode(key, input.isAzerty()), Action.fromCode(action)));
        // Mouse
        GLFW.glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> input.setKeyAction(Key.fromCode(button, input.isAzerty()), Action.fromCode(action)));
        // Mouse position
        GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> input.setMousePosition((float) xpos, (float) ypos));

        hints.applyOGL();
    }

    @Override
    public void update() {
        GLFW.glfwPollEvents();
        GLFW.glfwSwapBuffers(windowHandle);
    }

    @Override
    public void updateInput() throws PhotonException {
        input.updateInput();
    }

    @Override
    public String getDefaultTitle() {
        return defaultTitle;
    }

    @Override
    public Color getBackgroundColor() {
        return color;
    }

    @Override
    public void close() {
        GLFW.glfwSetWindowShouldClose(windowHandle, true);
    }

    @Override
    public void stop() {
        close();
        Callbacks.glfwFreeCallbacks(windowHandle);
        GLFW.glfwDestroyWindow(windowHandle);
    }

    @Override
    public void show() {
        GLFW.glfwShowWindow(windowHandle);
    }

    @Override
    public void maximize() {
        GLFW.glfwMaximizeWindow(windowHandle);
    }

    @Override
    public void hide() {
        GLFW.glfwHideWindow(windowHandle);
    }

    @Override
    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        GLFW.glfwSetWindowTitle(windowHandle, title);
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        GLFW.glfwSetWindowSize(windowHandle, width, height);
        GL11.glViewport(0, 0, width, height);
    }

    @Override
    public boolean isKeyPressed(Key key) {
        return glfwGetKey(windowHandle, key.getQwerty()) == GLFW_PRESS;
    }

    @Override
    public boolean isMouseButtonPressed(Key button) {
        return glfwGetMouseButton(windowHandle, button.getQwerty()) == GLFW_PRESS;
    }

    @Override
    public void setBackgroundColor(Color color) {
        this.color = color;
    }

}
