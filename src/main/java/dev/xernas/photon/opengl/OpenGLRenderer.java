package dev.xernas.photon.opengl;

import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.api.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class OpenGLRenderer implements IRenderer<GLShader, GLMesh> {

    private final Window window;
    private final boolean vsync;
    private final boolean debug;

    private final List<GLShader> loadedShaders = new ArrayList<>();
    private final List<GLMesh> loadedMeshes = new ArrayList<>();

    private Color clearColor;

    public OpenGLRenderer(Window window, boolean vsync, boolean debug) {
        this.window = window;
        this.vsync = vsync;
        this.debug = debug;
    }

    @Override
    public void render(GLShader shader, GLMesh mesh, BiConsumer<GLMesh, GLShader> operations) throws PhotonException {
        // Binds
        shader.bind();
        mesh.bind();
        // Operations
        operations.accept(mesh, shader);
        // Draw call
        GLUtils.draw(0, mesh.getVertexCount());
    }

    @Override
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(window.getHandle());
        GLUtils.clear(clearColor);
    }

    @Override
    public void setClearColor(Color color) throws PhotonException {
        clearColor = color;
    }

    @Override
    public void start() throws PhotonException {
        if (!PhotonAPI.isInitialized()) throw new PhotonException("PhotonAPI not initialized");
        GLFW.glfwMakeContextCurrent(window.getHandle());
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
        GLUtils.viewport(window);
        if (debug) {
            System.out.println("OpenGL Starting with Renderer: " + GLUtils.getRendererInfo());
            GLUtil.setupDebugMessageCallback(System.err);
        }
    }

    @Override
    public GLShader loadShader(Shader shader) throws PhotonException {
        GLShader glShader = new GLShader(shader);
        glShader.start();
        loadedShaders.add(glShader);
        return glShader;
    }

    @Override
    public GLMesh loadMesh(Model model) throws PhotonException {
        GLMesh glMesh = new GLMesh(model);
        glMesh.start();
        loadedMeshes.add(glMesh);
        return glMesh;
    }

    @Override
    public void dispose() throws PhotonException {
        for (GLMesh mesh : loadedMeshes) mesh.dispose();
        for (GLShader shader : loadedShaders) shader.dispose();
    }
}
