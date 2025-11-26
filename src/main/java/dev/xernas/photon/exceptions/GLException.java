package dev.xernas.photon.exceptions;

public class GLException extends PhotonException {

    public GLException(Throwable cause) {
        super(cause);
    }

    public GLException(String message) {
        super(message);
    }

    public GLException(String message, Throwable cause) {
        super(message, cause);
    }
}
