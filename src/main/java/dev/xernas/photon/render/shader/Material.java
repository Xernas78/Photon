package dev.xernas.photon.render.shader;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.PhotonImage;

import java.awt.*;

public interface Material {

    PhotonImage getTexture() throws PhotonException;

    Color getBaseColor();

    boolean isIlluminated();

}
