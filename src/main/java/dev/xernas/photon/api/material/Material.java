package dev.xernas.photon.api.material;

import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.exceptions.PhotonException;

import java.awt.*;

public interface Material {

    Color getColor();
    Texture getTexture();

    default boolean hasTexture() {
        return getTexture() != null && getApiTexture() != null;
    }
    default ITexture getApiTexture() {
        return PhotonAPI.getTexture(getTexture());
    }

}
