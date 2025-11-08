package dev.xernas.photon.api;

public interface Shader extends PhotonLogic {

    ShaderModule getVertexShaderModule();
    ShaderModule getFragmentShaderModule();

}
