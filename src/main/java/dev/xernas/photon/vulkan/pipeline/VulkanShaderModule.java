package dev.xernas.photon.vulkan;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.ShaderCompiler;
import dev.xernas.photon.utils.ShaderType;

public class VulkanShaderModule implements PhotonLogic {

    private final ShaderCompiler.SPIRV compiledShader;
    private final ShaderType type;

    public VulkanShaderModule(ShaderCompiler.SPIRV compiledShader, ShaderType type) {
        this.compiledShader = compiledShader;
        this.type = type;
    }

    @Override
    public void start() throws PhotonException {

    }

    @Override
    public void dispose() throws PhotonException {

    }
}
