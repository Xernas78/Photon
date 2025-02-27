package dev.xernas.photon.render.shader;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;
import dev.xernas.photon.render.IUseable;

public interface IShader extends IUseable {

    String getName();

    <T> boolean setUniform(String name, T value) throws PhotonException;

    boolean hasUniform(String name) throws PhotonException;

}
