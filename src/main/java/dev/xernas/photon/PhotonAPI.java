package dev.xernas.photon;

import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.api.window.Window;

public class PhotonAPI {

    private static Library library;
    private static boolean initialized = false;
    private static String appName;
    private static String appVersion;
    private static String engineName;
    private static String engineVersion;
    private static boolean debug;

    public static void init(Library lib, String appName, String appVersion, String engineName, String engineVersion, boolean debug) {
        PhotonAPI.library = lib;
        PhotonAPI.appName = appName;
        PhotonAPI.appVersion = appVersion;
        PhotonAPI.engineName = engineName;
        PhotonAPI.engineVersion = engineVersion;
        PhotonAPI.debug = debug;
        PhotonAPI.initialized = true;
    }

    public static Library getLibrary() {
        return library;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getAppVersion() {
        return appVersion;
    }

    public static String getEngineName() {
        return engineName;
    }

    public static String getEngineVersion() {
        return engineVersion;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static ITexture getTexture(Texture texture) {
        if (!initialized) throw new IllegalStateException("PhotonAPI is not initialized. Call PhotonAPI.init() first.");
        return library.createTexture(texture);
    }

    public static IRenderer<IShader, IMesh> getRenderer(Window window, boolean vsync) throws PhotonException {
        if (!initialized) throw new IllegalStateException("PhotonAPI is not initialized. Call PhotonAPI.init() first.");
        return (IRenderer<IShader, IMesh>) library.createRenderer(window, vsync, debug);
    }

    public static boolean isInitialized() {
        return initialized;
    }
}
