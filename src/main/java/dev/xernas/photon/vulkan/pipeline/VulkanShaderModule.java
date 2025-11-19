package dev.xernas.photon.vulkan.pipeline;

import dev.xernas.photon.api.shader.ShaderModule;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.exceptions.VulkanException;
import dev.xernas.photon.utils.ShaderCompiler;
import dev.xernas.photon.vulkan.device.VulkanDevice;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.nio.LongBuffer;

public class VulkanShaderModule implements ShaderModule {

    private final ShaderCompiler.SPIRV compiledShader;
    private final VulkanDevice device;

    private long shaderModule;

    public VulkanShaderModule(ShaderCompiler.SPIRV compiledShader, VulkanDevice device) {
        this.compiledShader = compiledShader;
        this.device = device;
    }

    @Override
    public void start() throws PhotonException {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            // Create shader module
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack)
                    .sType(VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(compiledShader.byteCode());
            LongBuffer pShaderModule = stack.mallocLong(1);
            if (VK10.vkCreateShaderModule(device.getDevice(), createInfo, null, pShaderModule) != VK10.VK_SUCCESS) throw new VulkanException("Failed to create shader module");

            shaderModule = pShaderModule.get(0);
        }
    }

    @Override
    public void dispose() throws PhotonException {
        VK10.vkDestroyShaderModule(device.getDevice(), shaderModule, null);
    }

    public long getShaderModule() {
        return shaderModule;
    }
}
