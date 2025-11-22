package dev.xernas.photon.api.shader;

public interface IUniform<T> {

    String getName();
    int getLocation();

    T get();
    void set(T value);

}
