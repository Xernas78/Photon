package dev.xernas.photon.api.texture;

import dev.xernas.photon.api.PhotonLogic;

public interface ITexture extends PhotonLogic {

    Texture getTexture();
    int getCurrentUnitID();

}
