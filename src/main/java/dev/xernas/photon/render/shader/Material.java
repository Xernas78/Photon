package dev.xernas.photon.render.shader;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;

import java.awt.*;

public interface Material {

    ITexture getTexture() throws PhotonException;

    Color getBaseColor();

    boolean isIlluminated();

}
