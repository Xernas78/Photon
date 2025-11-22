package dev.xernas.photon.api;

import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.exceptions.PhotonException;

import java.awt.*;

public interface IRenderer<S extends IShader, M extends IMesh> extends PhotonLogic {

    void render(S shader, M mesh) throws PhotonException;

    void swapBuffers() throws PhotonException;

    M loadMesh(Model model) throws PhotonException;

    S loadShader(Shader shader) throws PhotonException;

}
