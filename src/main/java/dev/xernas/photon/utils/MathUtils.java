package dev.xernas.photon.utils;

import org.joml.Vector3f;

public class MathUtils {

    public static float lerp(float a, float b, float f) {
        return a + f * (b - a);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

}
