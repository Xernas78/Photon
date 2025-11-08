package dev.xernas.photon.vulkan.swapchain;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.*;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import dev.xernas.photon.vulkan.device.VulkanPhysicalDevice;
import dev.xernas.photon.api.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_FIFO_KHR;

public class VulkanSwapChain implements PhotonLogic {

    private final Window window;
    private final VulkanDevice device;
    private final VulkanPhysicalDevice physicalDevice;
    private final VulkanSurface surface;
    private final boolean vsync;

    private final List<VulkanImageView> imageViews = new ArrayList<>();

    private long swapChain;
    private int extentWidth;
    private int extentHeight;
    private VkSurfaceFormatKHR surfaceFormat;

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
            surfaceFormat = pickSurfaceFormat(formats);
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
                    .imageFormat(surfaceFormat.format())
                    .imageColorSpace(surfaceFormat.colorSpace())
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
            if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to create swap chain: " +  VulkanErrorHelper.vkResultToString(err));
            swapChain = pSwapChain.get(0);

            // Retrieve swap chain images
            IntBuffer pImageCount = stack.ints(0);
            KHRSwapchain.vkGetSwapchainImagesKHR(device.getDevice(), swapChain, pImageCount, null);
            LongBuffer swapChainImages = stack.mallocLong(pImageCount.get(0));
            KHRSwapchain.vkGetSwapchainImagesKHR(device.getDevice(), swapChain, pImageCount, swapChainImages);

            // SwapChain image views
            for (int i = 0; i < swapChainImages.capacity(); i++) {
                long image = swapChainImages.get(i);
                VulkanImageView imageView = new VulkanImageView(image, surfaceFormat.format(), device);
                imageView.start();
                imageViews.add(imageView);
            }
        }
    }

    @Override
    public void dispose() throws PhotonException {
        for (VulkanImageView imageView : imageViews) imageView.dispose();
        imageViews.clear();
        KHRSwapchain.vkDestroySwapchainKHR(device.getDevice(), swapChain, null);
        swapChain = VK10.VK_NULL_HANDLE;
    }

    public long getSwapChain() {
        return swapChain;
    }

    public int getExtentWidth() {
        return extentWidth;
    }

    public int getExtentHeight() {
        return extentHeight;
    }

    public VkSurfaceFormatKHR getSurfaceFormat() {
        return surfaceFormat;
    }

    public List<VulkanImageView> getImageViews() {
        return imageViews;
    }

    public int acquireNextImage(VulkanSynchronisation synchronisation) throws VulkanException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pImageIndex = stack.mallocInt(1);
            int err = KHRSwapchain.vkAcquireNextImageKHR(device.getDevice(), swapChain, Long.MAX_VALUE, synchronisation.getImageAvailableSemaphore(), VK10.VK_NULL_HANDLE, pImageIndex);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR) return -1;
            if (err != VK10.VK_SUCCESS && err != KHRSwapchain.VK_SUBOPTIMAL_KHR)
                throw new VulkanException("Failed to acquire swap chain image: " + err);
            return pImageIndex.get(0);
        }
    }

    public void submitCommandBuffer(int imageIndex, VulkanSynchronisation synchronisation, VulkanCommandBuffer commandBuffer) throws VulkanException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO);

            LongBuffer waitSemaphores = stack.longs(synchronisation.getImageAvailableSemaphore());
            IntBuffer waitStages = stack.ints(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT);
            submitInfo.waitSemaphoreCount(1)
                    .pWaitSemaphores(waitSemaphores)
                    .pWaitDstStageMask(waitStages);

            submitInfo.pCommandBuffers(stack.pointers(commandBuffer.getCommandBuffer()));

            LongBuffer signalSemaphores = stack.longs(synchronisation.getRenderFinishedSemaphore());
            submitInfo.pSignalSemaphores(signalSemaphores);

            synchronisation.waitForFences();
            synchronisation.resetFences();

            int err = VK10.vkQueueSubmit(device.getGraphicsQueue(), submitInfo, synchronisation.getInFlightFence());
            if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to submit draw command buffer: " + VulkanErrorHelper.vkResultToString(err));
        }
    }

    public int presentImage(int imageIndex, VulkanSynchronisation synchronisation, VulkanRenderer renderer) throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPresentInfoKHR presentInfo = VkPresentInfoKHR.calloc(stack)
                    .sType(KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);

            LongBuffer waitSemaphores = stack.longs(synchronisation.getRenderFinishedSemaphore());
            presentInfo.pWaitSemaphores(waitSemaphores);

            LongBuffer swapChains = stack.longs(swapChain);
            presentInfo.swapchainCount(1)
                    .pSwapchains(swapChains)
                    .pImageIndices(stack.ints(imageIndex))
                    .pResults(null);

            int err = KHRSwapchain.vkQueuePresentKHR(device.getPresentQueue(), presentInfo);
            if (err == KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR || err == KHRSwapchain.VK_SUBOPTIMAL_KHR) {
                return -1;
            }
            if (err != VK10.VK_SUCCESS)
                throw new VulkanException("Failed to present swap chain image: " + VulkanErrorHelper.vkResultToString(err));
            return 0;
        }
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
            extentWidth = Math.max(capabilities.minImageExtent().width(),
                    Math.min(capabilities.maxImageExtent().width(), pWidth.get(0)));
            extentHeight = Math.max(capabilities.minImageExtent().height(),
                    Math.min(capabilities.maxImageExtent().height(), pHeight.get(0)));
            extent.width(extentWidth).height(extentHeight);
        }
        return extent;
    }

    public VkExtent2D getExtent(MemoryStack stack) {
        VkExtent2D extent = VkExtent2D.malloc(stack);
        if (extentWidth == 0 || extentHeight == 0) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            GLFW.glfwGetFramebufferSize(window.getHandle(), pWidth, pHeight);
            extentWidth = pWidth.get(0);
            extentHeight = pHeight.get(0);
        }
        extent.width(extentWidth).height(extentHeight);
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
