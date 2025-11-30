package dev.xernas.photon.opengl;

import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.Framebuffer;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.api.window.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class OpenGLRenderer implements IRenderer<GLFramebuffer, GLShader, GLMesh, GLTexture> {

    private final Window window;
    private final boolean vsync;
    private final boolean debug;

    private final List<GLFramebuffer> loadedFramebuffers = new ArrayList<>();
    private final List<GLShader> loadedShaders = new ArrayList<>();
    private final List<GLMesh> loadedMeshes = new ArrayList<>();
    private final List<GLTexture> loadedTextures = new ArrayList<>();

    public OpenGLRenderer(Window window, boolean vsync, boolean debug) {
        this.window = window;
        this.vsync = vsync;
        this.debug = debug;
    }

    @Override
    public void render(GLFramebuffer framebuffer, GLShader shader, GLMesh mesh, BiConsumer<GLMesh, GLShader> operations) {
        framebuffer.bind();
        // Binds
        shader.bind();
        if (mesh.getModel().is3D()) GLUtils.enableBackfaceCulling();
        else GLUtils.disableBackfaceCulling();

        mesh.bind();
        // Operations
        operations.accept(mesh, shader);
        // Draw call
        GLUtils.draw(0, mesh.getVertexCount());
    }

    @Override
    public GLFramebuffer getDefaultFramebuffer() throws PhotonException {
        return OpenGLConstants.DEFAULT_FRAMEBUFFER;
    }

    @Override
    public void swapBuffers() throws PhotonException {
        resizeFramebuffers();
        GLFW.glfwSwapBuffers(window.getHandle());
    }

    @Override
    public void clear(Color color) {
        GLUtils.clear(color);
    }

    @Override
    public void start() throws PhotonException {
        if (!PhotonAPI.isInitialized()) throw new PhotonException("PhotonAPI not initialized");
        GLFW.glfwMakeContextCurrent(window.getHandle());
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        GL.createCapabilities();
        GLUtils.viewport(window);
        GLUtils.enableDepthTest(); // Enable depth testing by default (TODO: Make configurable)
        if (debug) {
            System.out.println("[Photon] OpenGL Starting with Renderer: " + GLUtils.getRendererInfo());
            GLUtils.setupDebugMessageCallback();
        }
    }

    private void resizeFramebuffers() throws PhotonException {
        if (!window.framebufferResized()) return;
        for (GLFramebuffer framebuffer : loadedFramebuffers) framebuffer.resize(window.getWidth(), window.getHeight());
        window.setFramebufferResized(false);
    }

    @Override
    public GLFramebuffer loadFramebuffer(Framebuffer framebuffer) throws PhotonException {
        GLFramebuffer glFramebuffer = new GLFramebuffer(window.getWidth(), window.getHeight(), framebuffer.getAttachments());
        glFramebuffer.start();
        loadedFramebuffers.add(glFramebuffer);
        return glFramebuffer;
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
    public GLTexture loadTexture(Texture texture) throws PhotonException {
        GLTexture glTexture = new GLTexture(texture);
        glTexture.start();
        loadedTextures.add(glTexture);
        return glTexture;
    }



    @Override
    public void dispose() throws PhotonException {
        for (GLTexture texture : loadedTextures) texture.dispose();
        for (GLMesh mesh : loadedMeshes) mesh.dispose();
        for (GLShader shader : loadedShaders) shader.dispose();
        for (GLFramebuffer framebuffer : loadedFramebuffers) framebuffer.dispose();
        loadedTextures.clear();
        loadedMeshes.clear();
        loadedShaders.clear();
        loadedFramebuffers.clear();
    }
}
