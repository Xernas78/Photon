package dev.xernas.photon.render;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;

public interface IMesh extends IUseable {

    boolean hasNormals();

    boolean hasTexture();

}
