package dev.xernas.photon.api.material;

import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public class TextureMaterial implements Material {

    private Texture texture;

    public TextureMaterial(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Color getColor() {
        return null;
    }

}
