package dev.xernas.photon.render.shader;

import dev.xernas.photon.utils.PhotonImage;

import java.awt.*;

public interface Material {

    PhotonImage getTexture();

    Color getBaseColor();

}
