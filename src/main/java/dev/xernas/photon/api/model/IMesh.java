package dev.xernas.photon.api.model;

import dev.xernas.photon.api.PhotonLogic;
import dev.xernas.photon.api.material.Material;

public interface IMesh extends PhotonLogic {

    int getVertexCount();

    Material getMaterial();

}
