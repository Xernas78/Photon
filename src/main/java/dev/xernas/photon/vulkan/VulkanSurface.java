package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.api.window.Window;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSurface;
import org.lwjgl.vulkan.VK10;

import java.nio.LongBuffer;

public class VulkanSurface implements PhotonLogic {

    private final Window window;
    private final VulkanInstance instance;

    private long surface;

    public VulkanSurface(Window window, VulkanInstance instance) {
        this.window = window;
        this.instance = instance;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer pSurface = stack.longs(VK10.VK_NULL_HANDLE);
            if (GLFWVulkan.glfwCreateWindowSurface(instance.getInstance(), window.getHandle(), null, pSurface) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create window surface!");
            surface = pSurface.get(0);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        if (surface != VK10.VK_NULL_HANDLE) {
            KHRSurface.vkDestroySurfaceKHR(instance.getInstance(), surface, null);
            surface = VK10.VK_NULL_HANDLE;
        }
    }

    public long getSurface() {
        return surface;
    }
}
