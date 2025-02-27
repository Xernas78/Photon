package dev.xernas.photon.render;

import dev.xernas.photon.Initializable;

public interface IUseable extends Initializable {

    void use();

    void disuse();

    void dispose();

}
