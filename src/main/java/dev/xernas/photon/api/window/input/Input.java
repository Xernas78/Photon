package dev.xernas.photon.api.window.input;

import dev.xernas.photon.api.window.Window;

import java.util.HashMap;
import java.util.Map;

public class Input {

    private final Window window;
    private final boolean azerty;
    private final Map<Key, Action> keyMap = new HashMap<>();
    private final MousePosition mousePosition;
    private final MousePosition absoluteMousePosition;

    public Input(Window window, boolean azerty) {
        this.window = window;
        this.azerty = azerty;
        this.mousePosition = new MousePosition(0, 0);
        this.absoluteMousePosition = new MousePosition(0, 0);
    }

    public void updateInput() {
        if (keyMap.isEmpty()) return;
        keyMap.clear();
    }

    public Action getKeyAction(Key key) {
        return keyMap.getOrDefault(key, Action.IDLE);
    }

    public boolean isReleasing(Key key) {
        return getKeyAction(key) == Action.RELEASE;
    }

    public boolean isHolding(Key key) {
        return getKeyAction(key) == Action.HOLD;
    }

    public boolean isPressing(Key button) {
        return getKeyAction(button) == Action.PRESS;
    }

    public void setKeyAction(Key key, Action action) {
        keyMap.put(key, action);
    }

    public void setMousePosition(double x, double y) {
        mousePosition.set((float) x, (float) y);
    }

    public void setAbsoluteMousePosition(double x, double y) {
        absoluteMousePosition.set((float) x, (float) y);
    }

    public boolean isAzerty() {
        return azerty;
    }

    public Window getWindow() {
        return window;
    }

    public MousePosition getMousePosition() {
        return mousePosition;
    }

    public MousePosition getAbsoluteMousePosition() {
        return absoluteMousePosition;
    }
}