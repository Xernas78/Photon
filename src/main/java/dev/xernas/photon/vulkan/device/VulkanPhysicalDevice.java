package dev.xernas.photon.vulkan.device;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.VulkanInstance;
import dev.xernas.photon.vulkan.VulkanSurface;
import dev.xernas.photon.vulkan.swapchain.VulkanSwapChain;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;

public class VulkanPhysicalDevice implements PhotonLogic {

    private final List<String> requiredExtensions = List.of("VK_KHR_swapchain");

    private final VulkanInstance instance;
    private final VulkanSurface surface;

    private VkPhysicalDevice physicalDevice;
    private QueueFamilyIndices queueFamilyIndices;
    private VulkanSwapChain.VulkanSwapChainSupportDetails swapChainSupportDetails;

    public VulkanPhysicalDevice(VulkanInstance instance, VulkanSurface surface) {
        this.instance = instance;
        this.surface = surface;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = stack.ints(0);
            VK10.vkEnumeratePhysicalDevices(instance.getInstance(), deviceCount, null);
            if (deviceCount.get(0) == 0) throw new VulkanException("No GPUs with Vulkan support found!");

            PointerBuffer devices = stack.mallocPointer(deviceCount.get(0));
            VK10.vkEnumeratePhysicalDevices(instance.getInstance(), deviceCount, devices);

            for (int i = 0; i < devices.capacity(); i++) {
                VkPhysicalDevice device = new VkPhysicalDevice(devices.get(i), instance.getInstance());
                VulkanSwapChain.VulkanSwapChainSupportDetails supportDetails = VulkanSwapChain.getSwapChainSupport(device, surface, stack);
                if (isDeviceSuitable(device, supportDetails)) {
                    physicalDevice = device;
                    swapChainSupportDetails = supportDetails;
                    break;
                }
            }

            if (physicalDevice == null) throw new VulkanException("Failed to find a suitable GPU!");
        }
    }

    private boolean isDeviceSuitable(VkPhysicalDevice device, VulkanSwapChain.VulkanSwapChainSupportDetails supportDetails) {
        queueFamilyIndices = findQueueFamilies(device);
        boolean extensionsSupported = checkDeviceExtensionSupport(device);
        //TODO Use features to attribute score to each device and pick the best one
        boolean swapchainAdequate = false;
        if (extensionsSupported) swapchainAdequate = !supportDetails.isFormatEmpty() && !supportDetails.isPresentModeEmpty();
        return queueFamilyIndices.isComplete() && extensionsSupported && swapchainAdequate;
    }

    private QueueFamilyIndices findQueueFamilies(VkPhysicalDevice device) {
        QueueFamilyIndices indices = new QueueFamilyIndices();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer queueFamilyCount = stack.ints(0);
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(queueFamilyCount.get(0), stack);
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilies);

            int i = 0;
            for (VkQueueFamilyProperties props : queueFamilies) {
                if ((props.queueFlags() & VK10.VK_QUEUE_GRAPHICS_BIT) != 0) indices.graphicsFamily = Optional.of(i);

                IntBuffer presentSupport = stack.ints(VK10.VK_FALSE);
                KHRSurface.vkGetPhysicalDeviceSurfaceSupportKHR(device, i, surface.getSurface(), presentSupport);
                if (presentSupport.get(0) == VK10.VK_TRUE) indices.presentFamily = Optional.of(i);

                if (indices.isComplete()) break;
                i++;
            }
        }

        return indices;
    }

    private boolean checkDeviceExtensionSupport(VkPhysicalDevice device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer extensionCount = stack.ints(0);
            VK10.vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, null);

            VkExtensionProperties.Buffer availableExtensions =
                    VkExtensionProperties.malloc(extensionCount.get(0), stack);
            VK10.vkEnumerateDeviceExtensionProperties(device, (String) null, extensionCount, availableExtensions);

            for (String required : requiredExtensions) {
                boolean found = false;
                for (VkExtensionProperties ext : availableExtensions) {
                    if (required.equals(ext.extensionNameString())) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return true;
    }

    @Override
    public void dispose() {

    }

    public VkPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public QueueFamilyIndices getQueueFamilies() {
        return queueFamilyIndices;
    }

    public VulkanSwapChain.VulkanSwapChainSupportDetails getSwapChainSupportDetails() {
        return swapChainSupportDetails;
    }

    public static class QueueFamilyIndices {
        private Optional<Integer> graphicsFamily = Optional.empty();
        private Optional<Integer> presentFamily = Optional.empty();

        public boolean isComplete() {
            return graphicsFamily.isPresent() && presentFamily.isPresent();
        }

        public Optional<Integer> getGraphicsFamily() {
            return graphicsFamily;
        }

        public Optional<Integer> getPresentFamily() {
            return presentFamily;
        }
    }

}
