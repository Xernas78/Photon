package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;

public class VulkanCommand implements PhotonLogic {

    private final VulkanCommandPool commandPool;
    private final VulkanDevice device;

    public VulkanCommand(VulkanCommandPool commandPool, VulkanDevice device) {
        this.commandPool = commandPool;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool.getCommandPool())
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(1);

            if (VK10.vkAllocateCommandBuffers(device, allocInfo, ) != VK10.VK_SUCCESS) {
                throw new PhotonException("Failed to allocate command buffers");
            }
        }
    }

    @Override
    public void dispose() throws PhotonException {

    }

}
