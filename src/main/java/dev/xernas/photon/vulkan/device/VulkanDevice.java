package dev.xernas.photon.vulkan.device;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.VulkanErrorHelper;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;

public class VulkanDevice implements PhotonLogic {

    private final VulkanPhysicalDevice physicalDevice;

    private VkDevice device;
    private VkQueue graphicsQueue;
    private VkQueue presentQueue;

    public VulkanDevice(VulkanPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;
    }

    @Override
    public void start() throws PhotonException {
        VulkanPhysicalDevice.QueueFamilyIndices queueFamilyIndices = physicalDevice.getQueueFamilies();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer priority = stack.floats(1.0f);

            VkDeviceQueueCreateInfo.Buffer queueCreateInfos;
            if (queueFamilyIndices.getGraphicsFamily().get().equals(queueFamilyIndices.getPresentFamily().get())) {
                queueCreateInfos = VkDeviceQueueCreateInfo.calloc(1, stack);
                queueCreateInfos.get(0)
                        .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(queueFamilyIndices.getGraphicsFamily().get())
                        .pQueuePriorities(priority);
            } else {
                queueCreateInfos = VkDeviceQueueCreateInfo.calloc(2, stack);
                queueCreateInfos.get(0)
                        .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(queueFamilyIndices.getGraphicsFamily().get())
                        .pQueuePriorities(priority);
                queueCreateInfos.get(1)
                        .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
                        .queueFamilyIndex(queueFamilyIndices.getPresentFamily().get())
                        .pQueuePriorities(priority);
            }
            VkPhysicalDeviceFeatures features = VkPhysicalDeviceFeatures.calloc(stack);
            //TODO: use for later necessary features

            PointerBuffer extensions = stack.pointers(stack.UTF8(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME));

            VkDeviceCreateInfo createInfo = VkDeviceCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO)
                    .pQueueCreateInfos(queueCreateInfos)
                    .pEnabledFeatures(features)
                    .ppEnabledExtensionNames(extensions);

            PointerBuffer pDevice = stack.mallocPointer(1);
            int err = VK10.vkCreateDevice(physicalDevice.getPhysicalDevice(), createInfo, null, pDevice);
            if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to create logical device: " + VulkanErrorHelper.vkResultToString(err));

            device = new VkDevice(pDevice.get(0), physicalDevice.getPhysicalDevice(), createInfo);

            // Retrieve queues
            PointerBuffer pQueue = stack.mallocPointer(1);

            VK10.vkGetDeviceQueue(device, queueFamilyIndices.getGraphicsFamily().get(), 0, pQueue);
            graphicsQueue = new VkQueue(pQueue.get(0), device);

            VK10.vkGetDeviceQueue(device, queueFamilyIndices.getPresentFamily().get(), 0, pQueue);
            presentQueue = new VkQueue(pQueue.get(0), device);
        }
    }

    public void waitIdle() throws VulkanException {
        int err = VK10.vkDeviceWaitIdle(device);
        if (err != VK10.VK_SUCCESS) throw new VulkanException("Failed to wait for device idle: " + VulkanErrorHelper.vkResultToString(err));
    }

    @Override
    public void dispose() throws PhotonException {
        waitIdle();
        VK10.vkDestroyDevice(device, null);
    }

    public VulkanPhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }

    public VkDevice getDevice() {
        return device;
    }

    public VkQueue getGraphicsQueue() {
        return graphicsQueue;
    }

    public VkQueue getPresentQueue() {
        return presentQueue;
    }

}
