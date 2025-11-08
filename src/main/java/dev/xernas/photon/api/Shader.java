package dev.xernas.photon.api;

import dev.xernas.photon.utils.ShaderResource;

public class Shader {

    private final ShaderResource vertexResource;
    private final ShaderResource fragmentResource;

    public Shader(ShaderResource vertexResource, ShaderResource fragmentResource) {
        this.vertexResource = vertexResource;
        this.fragmentResource = fragmentResource;
    }

    public ShaderResource getVertexResource() {
        return vertexResource;
    }

    public ShaderResource getFragmentResource() {
        return fragmentResource;
    }

}
