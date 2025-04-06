package dev.xernas.photon.render.shader;

import dev.xernas.photon.utils.Image;

import java.awt.*;

public interface Material {

    Image getTexture();

    Color getBaseColor();

}
