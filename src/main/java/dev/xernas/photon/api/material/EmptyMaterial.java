package dev.xernas.photon.api.material;

import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public class EmptyMaterial implements Material {

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public Texture getTexture() {
        return null;
    }

}
