package dev.xernas.photon.vulkan.pipeline;

import dev.xernas.photon.api.Shader;
import dev.xernas.photon.api.ShaderModule;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.ShaderCompiler;
import dev.xernas.photon.utils.ShaderResource;
import dev.xernas.photon.utils.ShaderType;
import dev.xernas.photon.vulkan.device.VulkanDevice;

public class VulkanShader implements Shader {

    private final ShaderResource vertexResource;
    private final ShaderResource fragmentResource;
    private final VulkanDevice device;

    private VulkanShaderModule vertexShaderModule;
    private VulkanShaderModule fragmentShaderModule;

    public VulkanShader(ShaderResource vertexResource, ShaderResource fragmentResource, VulkanDevice device) {
        this.vertexResource = vertexResource;
        this.fragmentResource = fragmentResource;
        this.device = device;
    }

    @Override
    public ShaderModule getVertexShaderModule() {
        return vertexShaderModule;
    }

    @Override
    public ShaderModule getFragmentShaderModule() {
        return fragmentShaderModule;
    }

    @Override
    public void start() throws PhotonException {
        ShaderCompiler.SPIRV vertexSPIRV = ShaderCompiler.compileShaderCodeToSPIRV(vertexResource.filename(), vertexResource.shaderCode(), ShaderType.VERTEX);
        ShaderCompiler.SPIRV fragmentSPIRV = ShaderCompiler.compileShaderCodeToSPIRV(fragmentResource.filename(), fragmentResource.shaderCode(), ShaderType.FRAGMENT);

        // Create shader modules
        vertexShaderModule = new VulkanShaderModule(vertexSPIRV, device);
        fragmentShaderModule = new VulkanShaderModule(fragmentSPIRV, device);
        vertexShaderModule.start();
        fragmentShaderModule.start();
    }

    @Override
    public void dispose() throws PhotonException {
        vertexShaderModule.dispose();
        fragmentShaderModule.dispose();
    }
}
