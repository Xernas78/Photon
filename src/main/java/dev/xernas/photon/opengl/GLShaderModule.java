package dev.xernas.photon.opengl;

import dev.xernas.photon.api.shader.ShaderModule;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.GlobalUtilitaries;
import dev.xernas.photon.utils.ShaderResource;
import dev.xernas.photon.utils.ShaderType;
import org.lwjgl.opengl.GL20;

public class GLShaderModule implements ShaderModule {

    private final ShaderResource shaderResource;
    private final ShaderType shaderType;

    private int shaderId;

    public GLShaderModule(ShaderResource resource, ShaderType type) {
        this.shaderResource = resource;
        this.shaderType = type;
    }

    @Override
    public void start() throws PhotonException {
        shaderId = GlobalUtilitaries.requireNotEquals(GL20.glCreateShader(shaderType.toOpenGLConstant()), 0, "Error creating " + shaderType.name() + " shader");
        GL20.glShaderSource(shaderId, shaderResource.shaderCode());
        GL20.glCompileShader(shaderId);
        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) throw new PhotonException("Error compiling " + shaderType.name() + " shader: " + GL20.glGetShaderInfoLog(shaderId));
    }

    @Override
    public void dispose() throws PhotonException {
        GL20.glDeleteShader(shaderId);
    }

    public int getShaderId() {
        return shaderId;
    }
}
