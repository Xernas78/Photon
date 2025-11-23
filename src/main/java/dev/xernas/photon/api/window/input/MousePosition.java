package dev.xernas.photon.api.window.input;

import dev.xernas.photon.api.window.Window;
import org.joml.Vector2f;

public class MousePosition {

    private float x;
    private float y;

    public MousePosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f toWorldSpace(Window window) {
        float aspect = window.getWidth() / (float) window.getHeight();
        if (aspect > 1) {
            return new Vector2f(
                    ((x / window.getWidth()) * 2 - 1) * aspect,
                    1 - (y / window.getHeight()) * 2
            );
        } else {
            return new Vector2f(
                    (x / window.getWidth()) * 2 - 1,
                    (1 - (y / window.getHeight()) * 2) * aspect
            );
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "MousePosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
