package dev.xernas.photon.api;

public interface IShader extends PhotonLogic {

    ShaderModule getVertexShaderModule();
    ShaderModule getFragmentShaderModule();

}
