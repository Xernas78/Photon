package dev.xernas.photon.render;

public interface ITexture extends IUseable {

    int getWidth();

    int getHeight();

    void resize(int width, int height);

}
