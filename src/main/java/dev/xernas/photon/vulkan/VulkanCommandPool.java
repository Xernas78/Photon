package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import dev.xernas.photon.vulkan.device.VulkanPhysicalDevice;
import dev.xernas.photon.vulkan.pipeline.VulkanPipeline;
import dev.xernas.photon.vulkan.swapchain.VulkanFramebuffers;
import dev.xernas.photon.vulkan.swapchain.VulkanRenderPass;
import dev.xernas.photon.vulkan.swapchain.VulkanSwapChain;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanCommandPool implements PhotonLogic {

    private final VulkanFramebuffers framebuffers;
    private final VulkanSwapChain swapChain;
    private final VulkanDevice device;

    private long commandPool;
    private List<VulkanCommandBuffer> commandBuffers;

    public VulkanCommandPool(VulkanFramebuffers framebuffers, VulkanSwapChain swapChain, VulkanDevice device) {
        this.framebuffers = framebuffers;
        this.swapChain = swapChain;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        VulkanPhysicalDevice.QueueFamilyIndices indices = device.getPhysicalDevice().getQueueFamilies();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo createInfo = VkCommandPoolCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
                    .queueFamilyIndex(indices.getGraphicsFamily().get());

            LongBuffer pCommandPool = stack.mallocLong(1);
            if (VK10.vkCreateCommandPool(device.getDevice(), createInfo, null, pCommandPool) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create command pool");
            commandPool = pCommandPool.get(0);

            // Allocate command buffers
            int commandBufferCount = framebuffers.getFramebuffers().size();
            commandBuffers = new ArrayList<>(commandBufferCount);
            VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
                    .commandPool(commandPool)
                    .level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
                    .commandBufferCount(commandBufferCount);

            PointerBuffer pCommandBuffers = stack.mallocPointer(commandBufferCount);
            if (VK10.vkAllocateCommandBuffers(device.getDevice(), allocInfo, pCommandBuffers) != VK10.VK_SUCCESS) throw new VulkanException("Failed to allocate command buffers");

            for (int i = 0; i < commandBufferCount; i++) {
                VulkanCommandBuffer commandBuffer = new VulkanCommandBuffer(pCommandBuffers.get(i), device);
                commandBuffer.start();
                commandBuffers.add(commandBuffer);
            }
        }
    }

    public void recordCommandBuffer(int imageIndex, VulkanCommandBuffer commandBuffer, VulkanPipeline pipeline, VulkanRenderPass renderPass) throws VulkanException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            recordCommandBuffer(imageIndex, commandBuffer, pipeline, renderPass, stack);
        }
    }

    public void recordCommandBuffer(int imageIndex, VulkanCommandBuffer commandBuffer, VulkanPipeline pipeline, VulkanRenderPass renderPass, MemoryStack stack) throws VulkanException {
        VK10.vkResetCommandBuffer(commandBuffer.getCommandBuffer(), 0);
        VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
                .flags(0)
                .pInheritanceInfo(null);

        if (VK10.vkBeginCommandBuffer(commandBuffer.getCommandBuffer(), beginInfo) != VK10.VK_SUCCESS) throw new VulkanException("Failed to begin recording command buffer");

        VkRenderPassBeginInfo renderPassInfo = VkRenderPassBeginInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_RENDER_PASS_BEGIN_INFO)
                .renderPass(renderPass.getRenderPass())
                .framebuffer(framebuffers.getFramebuffers().get(imageIndex).getFramebuffer());
        renderPassInfo.renderArea().offset(VkOffset2D.calloc(stack).set(0, 0));
        renderPassInfo.renderArea().extent(swapChain.getExtent(stack));

        VkClearValue.Buffer clearValues = VkClearValue.calloc(1, stack);
        clearValues.color().float32(0, 0.0f);
        clearValues.color().float32(1, 0.0f);
        clearValues.color().float32(2, 0.0f);
        clearValues.color().float32(3, 1.0f);
        renderPassInfo.pClearValues(clearValues);

        // Begin the render pass
        VK10.vkCmdBeginRenderPass(commandBuffer.getCommandBuffer(), renderPassInfo, VK10.VK_SUBPASS_CONTENTS_INLINE);
        VK10.vkCmdBindPipeline(commandBuffer.getCommandBuffer(), VK10.VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline.getGraphicsPipeline());
        VkViewport.Buffer viewport = VkViewport.calloc(1, stack)
                .x(0.0f)
                .y(0.0f)
                .width(swapChain.getExtentWidth())
                .height(swapChain.getExtentHeight())
                .minDepth(0.0f)
                .maxDepth(1.0f);
        VK10.vkCmdSetViewport(commandBuffer.getCommandBuffer(), 0, viewport);
        VkRect2D.Buffer scissor = VkRect2D.calloc(1, stack)
                .offset(VkOffset2D.calloc(stack).set(0, 0))
                .extent(swapChain.getExtent(stack));
        VK10.vkCmdSetScissor(commandBuffer.getCommandBuffer(), 0, scissor);

        VK10.vkCmdDraw(commandBuffer.getCommandBuffer(), 3, 1, 0, 0);

        VK10.vkCmdEndRenderPass(commandBuffer.getCommandBuffer());
        // End Render pass
        if (VK10.vkEndCommandBuffer(commandBuffer.getCommandBuffer()) != VK10.VK_SUCCESS) throw new VulkanException("Failed to record command buffer");
    }

    public void resetCommandBuffer(VulkanCommandBuffer commandBuffer) {
        VK10.vkResetCommandBuffer(commandBuffer.getCommandBuffer(), 0);
    }

    // Getting current command buffer (drawFameMethod)
    public VulkanCommandBuffer getCommandBuffer(int index) {
        return commandBuffers.get(index);
    }

    @Override
    public void dispose() throws PhotonException {
        VK10.vkDestroyCommandPool(device.getDevice(), commandPool, null);
    }
}
