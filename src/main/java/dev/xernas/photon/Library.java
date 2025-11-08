package dev.xernas.photon;

import dev.xernas.photon.api.Renderer;
import dev.xernas.photon.api.Shader;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.OpenGLRenderer;
import dev.xernas.photon.vulkan.VulkanRenderer;
import dev.xernas.photon.api.window.Window;

import java.util.Locale;

public enum Library {

    VULKAN_1_0("1.0"),
    VULKAN_1_1("1.1"),
    VULKAN_1_2("1.2"),
    VULKAN_1_3("1.3"),
    VULKAN_1_4("1.4"),
    OPENGL_3_3("3.3"),
    OPENGL_4_6("4.6");

    private final String version;

    Library(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public boolean isVulkan() {
        return name().toLowerCase(Locale.ROOT).startsWith("vulkan");
    }

    public boolean isOpenGL() {
        return name().toLowerCase(Locale.ROOT).startsWith("opengl");
    }

    public Renderer createRenderer(Shader shader, Window window, boolean vsync, boolean debug) throws PhotonException {
        if (isVulkan()) {
            return new VulkanRenderer(shader, window, vsync, debug);
        } else if (isOpenGL()) {
            return new OpenGLRenderer(window, vsync, debug);
        }
        throw new PhotonException("Unsupported library: " + name());
    }

}
