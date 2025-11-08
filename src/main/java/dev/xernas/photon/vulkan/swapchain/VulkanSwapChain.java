package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;

import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;

public class VulkanSwapChain implements PhotonLogic {

    private final Window window;
    private final VulkanDevice device;
    private final VulkanPhysicalDevice physicalDevice;
    private final VulkanSurface surface;
    private final boolean vsync;

    private long swapChain;

    public VulkanSwapChain(boolean vsync, Window window, VulkanDevice device, VulkanPhysicalDevice physicalDevice, VulkanSurface surface) {
        this.window = window;
        this.device = device;
        this.physicalDevice = physicalDevice;
        this.surface = surface;
        this.vsync = vsync;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Query swap chain support details
            VulkanSwapChainSupportDetails swapChainSupportDetails = getSwapChainSupport(physicalDevice.getPhysicalDevice(), surface, stack);
            VkSurfaceCapabilitiesKHR capabilities = swapChainSupportDetails.getCapabilities();
            VkSurfaceFormatKHR.Buffer formats = swapChainSupportDetails.formats;
            IntBuffer presentModes = swapChainSupportDetails.presentModes;

            //Picking a format
            VkSurfaceFormatKHR chosenSurfaceFormat = pickSurfaceFormat(formats);
            //Picking a present mode
            int chosenPresentMode = pickPresentMode(presentModes);

            // Set extent
            VkExtent2D extent = createExtent(capabilities, stack);
            // Set image count
            int imageCount = capabilities.minImageCount() + 1;
            if (capabilities.maxImageCount() > 0 && imageCount > capabilities.maxImageCount()) imageCount = capabilities.maxImageCount();

            // Now we have everything to create the swap chain
            VkSwapchainCreateInfoKHR createInfo = VkSwapchainCreateInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_SWAPCHAIN_CREATE_INFO_KHR)
                    .surface(surface.getSurface())
                    .minImageCount(imageCount)
                    .imageFormat(chosenSurfaceFormat.format())
                    .imageColorSpace(chosenSurfaceFormat.colorSpace())
                    .imageExtent(extent)
                    .imageArrayLayers(1)
                    .imageUsage(VK10.VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT);
            VulkanPhysicalDevice.QueueFamilyIndices indices = physicalDevice.getQueueFamilies();
            if (indices.getGraphicsFamily().get() != indices.getPresentFamily().get()) {
                createInfo.imageSharingMode(VK10.VK_SHARING_MODE_CONCURRENT);
                createInfo.pQueueFamilyIndices(stack.ints(indices.getGraphicsFamily().get(), indices.getPresentFamily().get()));
            } else {
                createInfo.imageSharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
                createInfo.pQueueFamilyIndices(null);
            }
            int supported = capabilities.supportedTransforms();
            int preTransform;

            if ((supported & KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR) != 0) {
                preTransform = KHRSurface.VK_SURFACE_TRANSFORM_IDENTITY_BIT_KHR;
            } else {
                // fallback to currentTransform() if IDENTITY is not supported
                preTransform = capabilities.currentTransform();
            }
            createInfo.preTransform(preTransform);
            createInfo.compositeAlpha(KHRSurface.VK_COMPOSITE_ALPHA_OPAQUE_BIT_KHR);
            createInfo.presentMode(chosenPresentMode);
            createInfo.clipped(true);
            createInfo.oldSwapchain(VK10.VK_NULL_HANDLE);

            LongBuffer pSwapChain = stack.longs(VK10.VK_NULL_HANDLE);
            int err = KHRSwapchain.vkCreateSwapchainKHR(device.getDevice(), createInfo, null, pSwapChain);
            if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to create swap chain: " +  err);
            swapChain = pSwapChain.get(0);

            // Retrieve swap chain images
            IntBuffer pImageCount = stack.ints(0);
            KHRSwapchain.vkGetSwapchainImagesKHR(device.getDevice(), swapChain, pImageCount, null);
            LongBuffer swapChainImages = stack.mallocLong(pImageCount.get(0));
            KHRSwapchain.vkGetSwapchainImagesKHR(device.getDevice(), swapChain, pImageCount, swapChainImages);
            
        }
    }

    @Override
    public void dispose() throws PhotonException {
        KHRSwapchain.vkDestroySwapchainKHR(device.getDevice(), swapChain, null);
    }

    private VkSurfaceFormatKHR pickSurfaceFormat(VkSurfaceFormatKHR.Buffer availableFormats) {
        for (VkSurfaceFormatKHR availableFormat : availableFormats) {
            if (availableFormat.format() == VK10.VK_FORMAT_B8G8R8A8_SRGB) return availableFormat;
        }
        return availableFormats.get(0);
    }

    private int pickPresentMode(IntBuffer availablePresentModes) {
        for (int i = 0; i < availablePresentModes.capacity(); i++) {
            if (availablePresentModes.get(i) == (vsync ? VK_PRESENT_MODE_FIFO_KHR : KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR)) return availablePresentModes.get(i);
        }
        return VK_PRESENT_MODE_FIFO_KHR;
    }

    private VkExtent2D createExtent(VkSurfaceCapabilitiesKHR capabilities, MemoryStack stack) {
        VkExtent2D extent = VkExtent2D.malloc(stack);
        if (capabilities.currentExtent().width() != 0xFFFFFFFF) {
            extent.set(capabilities.currentExtent());
        } else {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window.getHandle(), pWidth, pHeight);
            int width = Math.max(capabilities.minImageExtent().width(),
                    Math.min(capabilities.maxImageExtent().width(), pWidth.get(0)));
            int height = Math.max(capabilities.minImageExtent().height(),
                    Math.min(capabilities.maxImageExtent().height(), pHeight.get(0)));
            extent.width(width).height(height);
        }
        return extent;
    }

    public static VulkanSwapChainSupportDetails getSwapChainSupport(VkPhysicalDevice device, VulkanSurface surface, MemoryStack stack) throws VulkanException {
        VkSurfaceCapabilitiesKHR capabilities = VkSurfaceCapabilitiesKHR.calloc(stack);
        int err = KHRSurface.vkGetPhysicalDeviceSurfaceCapabilitiesKHR(device, surface.getSurface(), capabilities);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to get physical device surface capabilities: " + err);

        IntBuffer formatCount = stack.ints(0);
        err = KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface.getSurface(), formatCount, null);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to get physical device surface formats: " + err);
        VkSurfaceFormatKHR.Buffer formats = VkSurfaceFormatKHR.calloc(formatCount.get(0), stack);
        err = KHRSurface.vkGetPhysicalDeviceSurfaceFormatsKHR(device, surface.getSurface(), formatCount, formats);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to get physical device surface formats: " + err);

        IntBuffer presentModeCount = stack.ints(0);
        err = KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface.getSurface(), presentModeCount, null);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to get physical device surface present modes: " + err);
        IntBuffer presentModes = stack.mallocInt(presentModeCount.get(0));
        err = KHRSurface.vkGetPhysicalDeviceSurfacePresentModesKHR(device, surface.getSurface(), presentModeCount, presentModes);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to get physical device surface present modes: " + err);

        return new VulkanSwapChainSupportDetails(capabilities, formats, presentModes);
    }

    public static class VulkanSwapChainSupportDetails {
        private final VkSurfaceCapabilitiesKHR capabilities;
        private final VkSurfaceFormatKHR.Buffer formats;
        private final IntBuffer presentModes;

        public VulkanSwapChainSupportDetails(VkSurfaceCapabilitiesKHR capabilities, VkSurfaceFormatKHR.Buffer formats, IntBuffer presentModes) {
            this.capabilities = capabilities;
            this.formats = formats;
            this.presentModes = presentModes;
        }

        public VkSurfaceCapabilitiesKHR getCapabilities() {
            return capabilities;
        }

        public boolean isFormatEmpty() {
            return formats == null || formats.capacity() == 0;
        }

        public boolean isPresentModeEmpty() {
            return presentModes == null || presentModes.capacity() == 0;
        }
    }

}
