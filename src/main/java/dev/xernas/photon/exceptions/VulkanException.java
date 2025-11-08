package dev.xernas.photon.exceptions;

public class VulkanException extends PhotonException {

    public VulkanException(Throwable cause) {
        super(cause);
    }

    public VulkanException(String message) {
        super(message);
    }

    public VulkanException(String message, Throwable cause) {
        super(message, cause);
    }
}
