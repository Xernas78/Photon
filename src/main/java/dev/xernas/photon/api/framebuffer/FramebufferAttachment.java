package dev.xernas.photon.api.framebuffer;

public enum FramebufferAttachment {

    COLOR_TEXTURE,
    DEPTH_TEXTURE,
    STENCIL_TEXTURE,
    DEPTH_STENCIL_TEXTURE,

    DEPTH_RENDERBUFFER,
    STENCIL_RENDERBUFFER,
    DEPTH_STENCIL_RENDERBUFFER;

    public boolean isTexture() {
        return this == COLOR_TEXTURE || this == DEPTH_TEXTURE || this == STENCIL_TEXTURE || this == DEPTH_STENCIL_TEXTURE;
    }

}
