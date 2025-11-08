package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import dev.xernas.photon.vulkan.swapchain.VulkanFramebuffers;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;

import java.util.ArrayList;
import java.util.List;

public class VulkanCommandBuffer implements PhotonLogic {

    private final long commandBufferHandle;
    private final VulkanDevice device;

    private VkCommandBuffer commandBuffer;

    public VulkanCommandBuffer(long commandBufferHandle, VulkanDevice device) {
        this.commandBufferHandle = commandBufferHandle;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            commandBuffer = new VkCommandBuffer(commandBufferHandle, device.getDevice());
        }
    }

    @Override
    public void dispose() throws PhotonException {

    }

    public VkCommandBuffer getCommandBuffer() {
        return commandBuffer;
    }

}
