package dev.xernas.photon.api.window.input;

import dev.xernas.photon.api.window.Window;
import org.joml.Vector2f;

public class Mouse {

    private float x;
    private float y;

    private float xScrollDelta;
    private float yScrollDelta;

    public Mouse(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setScrollDelta(float x, float y) {
        this.xScrollDelta = x;
        this.yScrollDelta = y;
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

    public boolean hasScrolled() {
        return xScrollDelta != 0 || yScrollDelta != 0;
    }

    public float getXScroll() {
        return xScrollDelta;
    }

    public float getYScroll() {
        return yScrollDelta;
    }

    public float getScroll() {
        return getYScroll();
    }

    @Override
    public String toString() {
        return "MousePosition{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
