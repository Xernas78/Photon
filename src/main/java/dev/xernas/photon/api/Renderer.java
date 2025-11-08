package dev.xernas.photon.api;

import dev.xernas.photon.exceptions.PhotonException;

public interface Renderer extends PhotonLogic {

    void swapBuffers() throws PhotonException;

}
