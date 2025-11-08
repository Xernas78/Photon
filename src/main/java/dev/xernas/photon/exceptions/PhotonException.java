package dev.xernas.photon.exceptions;

public class PhotonException extends Exception {

    public PhotonException(Throwable cause) {
        super(cause);
    }
    public PhotonException(String message) {
        super(message);
    }
    public PhotonException(String message, Throwable cause) {
        super(message, cause);
    }

}
