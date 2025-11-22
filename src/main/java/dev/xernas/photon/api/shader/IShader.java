package dev.xernas.photon.api.shader;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.exceptions.PhotonException;

public interface IShader extends PhotonLogic {

    ShaderModule getVertexShaderModule();
    ShaderModule getFragmentShaderModule();

    <T> boolean setUniform(String name, T value) throws PhotonException;
    boolean useSampler(String name, int unit) throws PhotonException;

}
