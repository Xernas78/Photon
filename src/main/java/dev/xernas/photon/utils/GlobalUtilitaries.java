package dev.xernas.photon.utils;

import dev.xernas.photon.exceptions.PhotonException;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class GlobalUtilitaries {

    public static int requireNotEquals(int obj, int notEqual, String message) throws PhotonException {
        if (obj == notEqual) throw new PhotonException(message);
        return obj;
    }

    public static Vector3f colorToVector3f(Color color) {
        return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public static Vector4f colorToVector4f(Color color) {
        return new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }

}
