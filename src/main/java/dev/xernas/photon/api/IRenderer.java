package dev.xernas.photon.api;

import dev.xernas.photon.exceptions.PhotonException;

public interface IRenderer<S extends IShader, M extends IMesh> extends PhotonLogic {

    void render(S shader, M mesh) throws PhotonException;

    void swapBuffers() throws PhotonException;

    M loadMesh(Mesh mesh) throws PhotonException;

    S loadShader(Shader shader) throws PhotonException;

}
