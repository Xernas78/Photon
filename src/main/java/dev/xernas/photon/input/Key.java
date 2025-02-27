package dev.xernas.photon.input;

import static org.lwjgl.glfw.GLFW.*;

public enum Key {

    KEY_UNKNOWN(GLFW_KEY_UNKNOWN, GLFW_KEY_UNKNOWN),
    KEY_A(GLFW_KEY_A, GLFW_KEY_Q),
    KEY_Z(GLFW_KEY_Z, GLFW_KEY_W),
    KEY_E(GLFW_KEY_E, GLFW_KEY_E),
    KEY_R(GLFW_KEY_R, GLFW_KEY_R),
    KEY_T(GLFW_KEY_T, GLFW_KEY_T),
    KEY_Y(GLFW_KEY_Y, GLFW_KEY_Y),
    KEY_U(GLFW_KEY_U, GLFW_KEY_U),
    KEY_I(GLFW_KEY_I, GLFW_KEY_I),
    KEY_O(GLFW_KEY_O, GLFW_KEY_O),
    KEY_P(GLFW_KEY_P, GLFW_KEY_P),
    KEY_Q(GLFW_KEY_Q, GLFW_KEY_A),
    KEY_S(GLFW_KEY_S, GLFW_KEY_S),
    KEY_D(GLFW_KEY_D, GLFW_KEY_D),
    KEY_F(GLFW_KEY_F, GLFW_KEY_F),
    KEY_G(GLFW_KEY_G, GLFW_KEY_G),
    KEY_H(GLFW_KEY_H, GLFW_KEY_H),
    KEY_J(GLFW_KEY_J, GLFW_KEY_J),
    KEY_K(GLFW_KEY_K, GLFW_KEY_K),
    KEY_L(GLFW_KEY_L, GLFW_KEY_L),
    KEY_M(GLFW_KEY_M, GLFW_KEY_SEMICOLON),
    KEY_W(GLFW_KEY_W, GLFW_KEY_Z),
    KEY_X(GLFW_KEY_X, GLFW_KEY_X),
    KEY_C(GLFW_KEY_C, GLFW_KEY_C),
    KEY_V(GLFW_KEY_V, GLFW_KEY_V),
    KEY_B(GLFW_KEY_B, GLFW_KEY_B),
    KEY_N(GLFW_KEY_N, GLFW_KEY_N),
    KEY_SEMICOLON(GLFW_KEY_SEMICOLON, GLFW_KEY_M),
    KEY_ARROW_UP(GLFW_KEY_UP, GLFW_KEY_UP),
    KEY_ARROW_DOWN(GLFW_KEY_DOWN, GLFW_KEY_DOWN),
    KEY_ARROW_LEFT(GLFW_KEY_LEFT, GLFW_KEY_LEFT),
    KEY_ARROW_RIGHT(GLFW_KEY_RIGHT, GLFW_KEY_RIGHT),
    KEY_ENTER(GLFW_KEY_ENTER, GLFW_KEY_ENTER),
    KEY_BACKSPACE(GLFW_KEY_BACKSPACE, GLFW_KEY_BACKSPACE),
    KEY_ESCAPE(GLFW_KEY_ESCAPE, GLFW_KEY_ESCAPE),
    KEY_NUMPAD_0(GLFW_KEY_KP_0, GLFW_KEY_KP_0),
    KEY_NUMPAD_1(GLFW_KEY_KP_1, GLFW_KEY_KP_1),
    KEY_NUMPAD_2(GLFW_KEY_KP_2, GLFW_KEY_KP_2),
    KEY_NUMPAD_3(GLFW_KEY_KP_3, GLFW_KEY_KP_3),
    KEY_NUMPAD_4(GLFW_KEY_KP_4, GLFW_KEY_KP_4),
    KEY_NUMPAD_5(GLFW_KEY_KP_5, GLFW_KEY_KP_5),
    KEY_NUMPAD_6(GLFW_KEY_KP_6, GLFW_KEY_KP_6),
    KEY_NUMPAD_7(GLFW_KEY_KP_7, GLFW_KEY_KP_7),
    KEY_NUMPAD_8(GLFW_KEY_KP_8, GLFW_KEY_KP_8),
    KEY_NUMPAD_9(GLFW_KEY_KP_9, GLFW_KEY_KP_9),
    KEY_F1(GLFW_KEY_F1, GLFW_KEY_F1),
    KEY_F2(GLFW_KEY_F2, GLFW_KEY_F2),
    KEY_F3(GLFW_KEY_F3, GLFW_KEY_F3),
    KEY_F4(GLFW_KEY_F4, GLFW_KEY_F4),
    KEY_F5(GLFW_KEY_F5, GLFW_KEY_F5),
    KEY_F6(GLFW_KEY_F6, GLFW_KEY_F6),
    KEY_F7(GLFW_KEY_F7, GLFW_KEY_F7),
    KEY_F8(GLFW_KEY_F8, GLFW_KEY_F8),
    KEY_F9(GLFW_KEY_F9, GLFW_KEY_F9),
    KEY_F10(GLFW_KEY_F10, GLFW_KEY_F10),
    KEY_F11(GLFW_KEY_F11, GLFW_KEY_F11),
    KEY_F12(GLFW_KEY_F12, GLFW_KEY_F12),
    KEY_TAB(GLFW_KEY_TAB, GLFW_KEY_TAB),
    KEY_LEFT_SHIFT(GLFW_KEY_LEFT_SHIFT, GLFW_KEY_LEFT_SHIFT),
    KEY_RIGHT_SHIFT(GLFW_KEY_RIGHT_SHIFT, GLFW_KEY_RIGHT_SHIFT),
    KEY_SPACE(GLFW_KEY_SPACE, GLFW_KEY_SPACE),
    KEY_LEFT_CONTROL(GLFW_KEY_LEFT_CONTROL, GLFW_KEY_LEFT_CONTROL),
    KEY_RIGHT_CONTROL(GLFW_KEY_RIGHT_CONTROL, GLFW_KEY_RIGHT_CONTROL),

    MOUSE_LEFT(GLFW_MOUSE_BUTTON_LEFT, GLFW_MOUSE_BUTTON_LEFT),
    MOUSE_RIGHT(GLFW_MOUSE_BUTTON_RIGHT, GLFW_MOUSE_BUTTON_RIGHT),
    MOUSE_MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE, GLFW_MOUSE_BUTTON_MIDDLE),
    MOUSE_BUTTON_4(GLFW_MOUSE_BUTTON_4, GLFW_MOUSE_BUTTON_4),
    MOUSE_BUTTON_5(GLFW_MOUSE_BUTTON_5, GLFW_MOUSE_BUTTON_5);

    private final int azerty;
    private final int qwerty;

    Key(int azerty, int qwerty) {
        this.azerty = azerty;
        this.qwerty = qwerty;
    }

    public static Key fromCode(int code, boolean azerty) {
        for (Key key : values()) {
            if ((azerty ? key.getQwerty() : key.getAzerty()) == code) {
                return key;
            }
        }
        return Key.KEY_UNKNOWN;
    }

    public int getAzerty() {
        return azerty;
    }

    public int getQwerty() {
        return qwerty;
    }
}
