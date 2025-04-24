package dev.xernas.photon.render;

import dev.xernas.photon.exceptions.PhotonException;

public interface IFramebuffer extends IUseable {

    int getWidth();

    int getHeight();

    void resize(int width, int height) throws PhotonException;

}
