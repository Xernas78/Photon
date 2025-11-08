package dev.xernas.photon.vulkan.swapchain;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkFramebufferCreateInfo;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

public class VulkanFramebuffers implements PhotonLogic {

    private final VulkanSwapChain swapChain;
    private final VulkanRenderPass renderPass;
    private final VulkanDevice device;

    private final List<VulkanFramebuffer> framebuffers = new ArrayList<>();

    public VulkanFramebuffers(VulkanSwapChain swapChain, VulkanRenderPass renderPass, VulkanDevice device) {
        this.swapChain = swapChain;
        this.renderPass = renderPass;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        for (VulkanImageView imageView : swapChain.getImageViews()) {
            VulkanFramebuffer framebuffer = new VulkanFramebuffer(imageView, swapChain, renderPass, device);
            framebuffer.start();
            framebuffers.add(framebuffer);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        for (VulkanFramebuffer framebuffer : framebuffers) framebuffer.dispose();
        framebuffers.clear();
    }

    public List<VulkanFramebuffer> getFramebuffers() {
        return framebuffers;
    }

    public static class VulkanFramebuffer implements PhotonLogic {

        private final VulkanImageView imageView;
        private final VulkanSwapChain swapChain;
        private final VulkanRenderPass renderPass;
        private final VulkanDevice device;

        private long framebuffer;

        public VulkanFramebuffer(VulkanImageView imageView, VulkanSwapChain swapChain, VulkanRenderPass renderPass, VulkanDevice device) {
            this.imageView = imageView;
            this.swapChain = swapChain;
            this.renderPass = renderPass;
            this.device = device;
        }

        @Override
        public void start() throws PhotonException {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                System.out.println("Creating framebuffer for image view: " + imageView.getImageView() + " w/ render pass: " + renderPass.getRenderPass());
                VkFramebufferCreateInfo framebufferInfo = VkFramebufferCreateInfo.calloc(stack)
                        .sType(VK10.VK_STRUCTURE_TYPE_FRAMEBUFFER_CREATE_INFO)
                        .renderPass(renderPass.getRenderPass())
                        .pAttachments(stack.longs(imageView.getImageView()))
                        .width(swapChain.getExtentWidth())
                        .height(swapChain.getExtentHeight())
                        .layers(1);

                LongBuffer pFramebuffer = stack.mallocLong(1);
                if (VK10.vkCreateFramebuffer(device.getDevice(), framebufferInfo, null, pFramebuffer) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create framebuffer");
                framebuffer = pFramebuffer.get(0);
            }
        }

        @Override
        public void dispose() throws PhotonException {
            VK10.vkDestroyFramebuffer(device.getDevice(), framebuffer, null);
        }

        public long getFramebuffer() {
            return framebuffer;
        }
    }
}
