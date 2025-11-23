package dev.xernas.photon.api.window.input;

import org.lwjgl.glfw.GLFW;

public enum Action {

    PRESS(GLFW.GLFW_PRESS),
    RELEASE(GLFW.GLFW_RELEASE),
    HOLD(GLFW.GLFW_REPEAT),
    IDLE(-1);

    private final int actionCode;

    Action(int actionCode) {
        this.actionCode = actionCode;
    }

    public static Action fromCode(int code) {
        for (Action action : values()) {
            if (action.actionCode == code) {
                return action;
            }
        }
        return null;
    }

}
