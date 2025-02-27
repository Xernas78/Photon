package dev.xernas.photon.render.shader;

public interface IUniform<T> {

    void set(T value);

    T get();

    String getName();

    int getLocation();

}
