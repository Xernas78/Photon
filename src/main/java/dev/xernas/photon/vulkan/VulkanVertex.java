package dev.xernas.photon.vulkan;

import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

public class VulkanVertex {

    public static final int SIZEOF = (3 + 3 + 2) * Float.BYTES;
    public static final int OFFSETOF_POS = 0;
    public static final int OFFSETOF_COLOR = 3 * Float.BYTES;
    public static final int OFFSETOF_TEXTCOORDS = (3 + 3) * Float.BYTES;

    private Vector3fc pos;
    private Vector3fc color;
    private Vector2fc texCoords;

    public VulkanVertex(Vector3fc pos, Vector3fc color, Vector2fc texCoords) {
        this.pos = pos;
        this.color = color;
        this.texCoords = texCoords;
    }

    private static VkVertexInputBindingDescription.Buffer getBindingDescription(MemoryStack stack) {

        VkVertexInputBindingDescription.Buffer bindingDescription =
                VkVertexInputBindingDescription.calloc(1, stack);

        bindingDescription.binding(0);
        bindingDescription.stride(SIZEOF);
        bindingDescription.inputRate(VK_VERTEX_INPUT_RATE_VERTEX);

        return bindingDescription;
    }

    private static VkVertexInputAttributeDescription.Buffer getAttributeDescriptions(MemoryStack stack) {

        VkVertexInputAttributeDescription.Buffer attributeDescriptions =
                VkVertexInputAttributeDescription.calloc(3, stack);

        // Position
        VkVertexInputAttributeDescription posDescription = attributeDescriptions.get(0);
        posDescription.binding(0);
        posDescription.location(0);
        posDescription.format(VK10.VK_FORMAT_R32G32B32_SFLOAT);
        posDescription.offset(OFFSETOF_POS);

        // Color
        VkVertexInputAttributeDescription colorDescription = attributeDescriptions.get(1);
        colorDescription.binding(0);
        colorDescription.location(1);
        colorDescription.format(VK10.VK_FORMAT_R32G32B32_SFLOAT);
        colorDescription.offset(OFFSETOF_COLOR);

        // Texture coordinates
        VkVertexInputAttributeDescription texCoordsDescription = attributeDescriptions.get(2);
        texCoordsDescription.binding(0);
        texCoordsDescription.location(2);
        texCoordsDescription.format(VK10.VK_FORMAT_R32G32_SFLOAT);
        texCoordsDescription.offset(OFFSETOF_TEXTCOORDS);

        return attributeDescriptions.rewind();
    }

    public Vector3fc getPos() {
        return pos;
    }

    public Vector3fc getColor() {
        return color;
    }

    public Vector2fc getTexCoords() {
        return texCoords;
    }
}
