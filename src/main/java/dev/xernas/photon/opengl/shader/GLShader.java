package dev.xernas.photon.opengl.shader;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.IBindeable;
import dev.xernas.photon.opengl.exceptions.OpenGLException;
import dev.xernas.photon.render.shader.IShader;
import dev.xernas.photon.utils.GlobalUtilitaries;
import lombok.Getter;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL41.glShaderBinary;

public class GLShader implements IShader, IBindeable {

    private final Map<String, Integer> uniforms = new HashMap<>();

    private int programId;

    private final String name;
    @Getter
    private final String vertexCode;
    @Getter
    private final String fragmentCode;

    public GLShader(String name, String vertexCode, String fragmentCode) {
        this.name = name;
        this.vertexCode = vertexCode;
        this.fragmentCode = fragmentCode;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> boolean setUniform(String name, T value) throws OpenGLException {
        int location = uniforms.getOrDefault(name, -1);
        if (location == -1) return false;
        GLUniform<T> uniform = new GLUniform<>(name, location);
        if (value == null) {
            throw new OpenGLException("Trying to set uniform with null value: " + name);
        }
        uniform.set(value);
        return true;
    }

    @Override
    public boolean hasUniform(String name) {
        return uniforms.containsKey(name);
    }

    @Override
    public void bind() {
        glUseProgram(programId);
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void cleanup() {
        unbind();
        if (programId != 0) glDeleteProgram(programId);
    }

    @Override
    public void init() throws PhotonException {
        // Creating shaders
        int vertexShaderId = GlobalUtilitaries.requireNotEquals(glCreateShader(GL_VERTEX_SHADER), 0, "Error creating vertex shader");
        int fragmentShaderId = GlobalUtilitaries.requireNotEquals(glCreateShader(GL_FRAGMENT_SHADER), 0, "Error creating fragment shader");

        // Creating program
        programId = GlobalUtilitaries.requireNotEquals(glCreateProgram(), 0, "Error creating shader program");

        // Compiling vertex shader
        glShaderSource(vertexShaderId, vertexCode);
        glCompileShader(vertexShaderId);
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error compiling vertex shader: " + glGetShaderInfoLog(vertexShaderId));
        }

        // Compiling fragment shader
        glShaderSource(fragmentShaderId, fragmentCode);
        glCompileShader(fragmentShaderId);
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error compiling fragment shader: " + glGetShaderInfoLog(fragmentShaderId));
        }

        // Assembling the two shaders into one shader program
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error linking shader program: " + glGetProgramInfoLog(programId));
        }

        // Removing now unnecessary shaders
        if (vertexShaderId != 0) {
            GL20.glDetachShader(programId, vertexShaderId);
            GL20.glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(programId, fragmentShaderId);
            GL20.glDeleteShader(fragmentShaderId);
        }


        // Validate and check
        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            throw new OpenGLException("Error validating shader program: " + glGetProgramInfoLog(programId));
        }

        // Getting all uniforms
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer count = stack.mallocInt(1);
            glGetProgramiv(programId, GL_ACTIVE_UNIFORMS, count);
            for (int i = 0; i < count.get(0); i++) {

                IntBuffer size = stack.mallocInt(1);
                IntBuffer type = stack.mallocInt(1);
                String name = glGetActiveUniform(programId, i, size, type).trim();
                if (name.endsWith("[0]")) name = name.substring(0, name.length() - 3);
                if (size.get(0) > 1) {
                    for (int j = 0; j < size.get(0); j++) {
                        String arrayName = name + "[" + j + "]";
                        int location = glGetUniformLocation(programId, arrayName);
                        uniforms.put(arrayName, location);
                    }
                } else {
                    int location = glGetUniformLocation(programId, name);
                    uniforms.put(name, location);
                }
            }
        }
    }

    @Override
    public void use() {
        bind();
    }

    @Override
    public void disuse() {
        unbind();
    }

    @Override
    public void dispose() {
        cleanup();
    }
}
