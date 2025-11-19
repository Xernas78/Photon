package dev.xernas.photon.api.shader;

import dev.xernas.photon.api.PhotonLogic;

public interface IShader extends PhotonLogic {

    ShaderModule getVertexShaderModule();
    ShaderModule getFragmentShaderModule();

}
