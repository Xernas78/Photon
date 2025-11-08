package dev.xernas.photon.vulkan.swapchain;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkComponentMapping;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import java.nio.LongBuffer;

public class VulkanImageView implements PhotonLogic {

    private final long image;
    private final int format;
    private final VulkanDevice device;

    private long imageView;

    public VulkanImageView(long image, int format, VulkanDevice device) {
        this.image = image;
        this.format = format;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageViewCreateInfo createInfo = VkImageViewCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO)
                    .image(image)
                    .viewType(VK10.VK_IMAGE_VIEW_TYPE_2D)
                    .format(format);
            VkComponentMapping components = VkComponentMapping.calloc(stack)
                    .r(VK10.VK_COMPONENT_SWIZZLE_IDENTITY)
                    .g(VK10.VK_COMPONENT_SWIZZLE_IDENTITY)
                    .b(VK10.VK_COMPONENT_SWIZZLE_IDENTITY)
                    .a(VK10.VK_COMPONENT_SWIZZLE_IDENTITY);
            createInfo.components(components);
            createInfo.subresourceRange()
                    .aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT)
                    .baseMipLevel(0)
                    .levelCount(1)
                    .baseArrayLayer(0)
                    .layerCount(1);

            LongBuffer pImageView = stack.longs(VK10.VK_NULL_HANDLE);
            if (VK10.vkCreateImageView(device.getDevice(), createInfo, null, pImageView) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create image views!");
            imageView = pImageView.get(0);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        VK10.vkDestroyImageView(device.getDevice(), imageView, null);
    }

    public long getImageView() {
        return imageView;
    }
}
