package dev.xernas.photon;

import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class Photon {

    public static void initPhoton() throws PhotonException {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new PhotonException("Failed to initialize GLFW");
        }
    }

    public static void terminatePhoton() {
        GLFW.glfwTerminate();
    }

}
