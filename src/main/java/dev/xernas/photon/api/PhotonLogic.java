package dev.xernas.photon.api;

import dev.xernas.photon.exceptions.PhotonException;

public interface PhotonLogic {

    void start() throws PhotonException;

    void dispose() throws PhotonException;

}
