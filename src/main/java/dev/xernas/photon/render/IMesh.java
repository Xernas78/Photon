package dev.xernas.photon.render;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;
import dev.xernas.photon.render.shader.Material;

public interface IMesh extends IUseable {

    boolean hasNormals();

    boolean hasTexture();

    Material getMaterial();

    void updateTexture(ITexture texture);

}
