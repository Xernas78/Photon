package dev.xernas.photon.opengl;

import dev.xernas.photon.api.IShader;
import dev.xernas.photon.api.Shader;
import dev.xernas.photon.api.ShaderModule;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.GlobalUtilitaries;
import dev.xernas.photon.utils.ShaderResource;
import dev.xernas.photon.utils.ShaderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class GLShader implements IShader {

    private final ShaderResource vertexShaderResource;
    private final ShaderResource fragmentShaderResource;

    private GLShaderModule vertexShader;
    private GLShaderModule fragmentShader;

    private int programId;

    public GLShader(Shader shader) {
        this.vertexShaderResource = shader.getVertexResource();
        this.fragmentShaderResource = shader.getFragmentResource();
    }

    @Override
    public ShaderModule getVertexShaderModule() {
        return vertexShader;
    }

    @Override
    public ShaderModule getFragmentShaderModule() {
        return fragmentShader;
    }

    @Override
    public void start() throws PhotonException {
        vertexShader = new GLShaderModule(vertexShaderResource, ShaderType.VERTEX);
        fragmentShader = new GLShaderModule(fragmentShaderResource, ShaderType.FRAGMENT);
        vertexShader.start();
        fragmentShader.start();

        programId = GlobalUtilitaries.requireNotEquals(GL30.glCreateProgram(), 0, "Error creating shader program");
        if (programId == 0) throw new PhotonException("Could not create program");
        GL30.glAttachShader(programId, vertexShader.getShaderId());
        GL30.glAttachShader(programId, fragmentShader.getShaderId());
        GL30.glLinkProgram(programId);
        if (GL30.glGetProgrami(programId, GL30.GL_LINK_STATUS) == GL11.GL_FALSE) throw new PhotonException("Could not link shader program");

        if (vertexShader.getShaderId() != 0) {
            GL30.glDetachShader(programId, vertexShader.getShaderId());
            vertexShader.dispose();
        }
        if (fragmentShader.getShaderId() != 0) {
            GL30.glDetachShader(programId, fragmentShader.getShaderId());
            fragmentShader.dispose();
        }

        GL30.glValidateProgram(programId);
        if (GL30.glGetProgrami(programId, GL30.GL_VALIDATE_STATUS) == GL11.GL_FALSE) throw new PhotonException("Could not validate shader program");
    }

    public void bind() {
        GL30.glUseProgram(programId);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    @Override
    public void dispose() throws PhotonException {
        GL30.glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }
}
