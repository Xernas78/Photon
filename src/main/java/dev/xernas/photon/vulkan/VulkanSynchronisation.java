package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;

import java.nio.LongBuffer;

public class VulkanSynchronisation implements PhotonLogic {

    private final VulkanDevice device;

    private long imageAvailableSemaphore;
    private long renderFinishedSemaphore;
    private long inFlightFence;

    public VulkanSynchronisation(VulkanDevice device) {
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSemaphoreCreateInfo semaphoreInfo = VkSemaphoreCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SEMAPHORE_CREATE_INFO);
            VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
                    .flags(VK10.VK_FENCE_CREATE_SIGNALED_BIT);

            LongBuffer pimageAvailableSemaphore = stack.mallocLong(1);
            LongBuffer prederFinishedSemaphore = stack.mallocLong(1);
            LongBuffer pInFlightFence = stack.mallocLong(1);
            if (VK10.vkCreateSemaphore(device.getDevice(), semaphoreInfo, null, pimageAvailableSemaphore) != VK10.VK_SUCCESS ||
                    VK10.vkCreateSemaphore(device.getDevice(), semaphoreInfo, null, prederFinishedSemaphore) != VK10.VK_SUCCESS ||
                    VK10.vkCreateFence(device.getDevice(), fenceInfo, null, pInFlightFence) != VK10.VK_SUCCESS) {
                throw new VulkanException("Failed to create synchronization objects for a frame");
            }

            imageAvailableSemaphore = pimageAvailableSemaphore.get(0);
            renderFinishedSemaphore = prederFinishedSemaphore.get(0);
            inFlightFence = pInFlightFence.get(0);
        }
    }

    public long getImageAvailableSemaphore() {
        return imageAvailableSemaphore;
    }

    public long getRenderFinishedSemaphore() {
        return renderFinishedSemaphore;
    }

    public long getInFlightFence() {
        return inFlightFence;
    }

    public void waitForFences() {
        VK10.vkWaitForFences(device.getDevice(), inFlightFence, true, Long.MAX_VALUE);
    }

    public void waitForGraphicsQueue() {
        VK10.vkQueueWaitIdle(device.getGraphicsQueue());
    }

    public void waitForPresentQueue() {
        VK10.vkQueueWaitIdle(device.getPresentQueue());
    }

    public void resetFences() {
        VK10.vkResetFences(device.getDevice(), inFlightFence);
    }

    @Override
    public void dispose() throws PhotonException {
        device.waitIdle();
        waitForFences();
        VK10.vkDestroyFence(device.getDevice(), inFlightFence, null);
        waitForGraphicsQueue();
        VK10.vkDestroySemaphore(device.getDevice(), renderFinishedSemaphore, null);
        VK10.vkDestroySemaphore(device.getDevice(), imageAvailableSemaphore, null);
    }

}
