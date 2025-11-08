package dev.xernas.photon.utils;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.VK10;

public enum ShaderType {

    VERTEX,
    FRAGMENT,
    COMPUTE;

    public int toShadercConstant() {
        return switch (this) {
            case VERTEX ->  Shaderc.shaderc_glsl_vertex_shader;
            case FRAGMENT -> Shaderc.shaderc_glsl_fragment_shader;
            case COMPUTE -> Shaderc.shaderc_glsl_compute_shader;
        };
    }

    public int toVulkanConstant() {
        return switch (this) {
            case VERTEX -> VK10.VK_SHADER_STAGE_VERTEX_BIT;
            case FRAGMENT -> VK10.VK_SHADER_STAGE_FRAGMENT_BIT;
            case COMPUTE -> VK10.VK_SHADER_STAGE_COMPUTE_BIT;
        };
    }

    public int toOpenGLConstant() {
        return switch (this) {
            case VERTEX -> GL20.GL_VERTEX_SHADER;
            case FRAGMENT -> GL20.GL_FRAGMENT_SHADER;
            case COMPUTE -> GL43.GL_COMPUTE_SHADER;
        };
    }

}
