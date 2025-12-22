package dev.xernas.photon.api.window.input;

import dev.xernas.photon.api.window.Window;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class Input {

    private final Window window;
    private final boolean azerty;
    private final Map<Key, Action> keyMap = new HashMap<>();
    private final Mouse mouse;
    private final Mouse absoluteMouse;

    public Input(Window window, boolean azerty) {
        this.window = window;
        this.azerty = azerty;
        this.mouse = new Mouse(0, 0);
        this.absoluteMouse = new Mouse(0, 0);
    }

    public void updateInput() {
        if (keyMap.isEmpty()) return;
        keyMap.clear();
        resetScrollDelta();
    }

    public Action getKeyAction(Key key) {
        return keyMap.getOrDefault(key, Action.IDLE);
    }

    public boolean isPressing(Key key) {
        if (key.isMouseButton()) return GLFW.glfwGetMouseButton(window.getHandle(), key.getQwerty()) == GLFW.GLFW_PRESS;
        else return GLFW.glfwGetKey(window.getHandle(), key.getQwerty()) == GLFW.GLFW_PRESS;
    }

    public boolean hasReleased(Key key) {
        return getKeyAction(key) == Action.RELEASE;
    }

    public boolean hasHold(Key key) {
        return getKeyAction(key) == Action.HOLD;
    }

    public boolean hasPressed(Key button) {
        return getKeyAction(button) == Action.PRESS;
    }

    public void setKeyAction(Key key, Action action) {
        keyMap.put(key, action);
    }

    public void setMousePosition(double x, double y) {
        mouse.set((float) x, (float) y);
    }

    public void setScrollDelta(float x, float y) {
        mouse.setScrollDelta(x, y);
    }

    public void resetScrollDelta() {
        mouse.setScrollDelta(0, 0);
    }

    public void setAbsoluteMousePosition(double x, double y) {
        absoluteMouse.set((float) x, (float) y);
    }

    public boolean isAzerty() {
        return azerty;
    }

    public Window getWindow() {
        return window;
    }

    public Mouse getMouse() {
        return mouse;
    }

    public Mouse getAbsoluteMouse() {
        return absoluteMouse;
    }
}