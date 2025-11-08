package dev.xernas.photon.utils;

import dev.xernas.photon.exceptions.PhotonException;
import org.lwjgl.util.shaderc.Shaderc;

import java.nio.ByteBuffer;

public class ShaderCompiler {

    public static SPIRV compileShaderCodeToSPIRV(String filename, String source, ShaderType shaderType) throws PhotonException {
        long compiler = Shaderc.shaderc_compiler_initialize();
        if (compiler == 0) throw new PhotonException("Could not initialize GLSL compiler");
        long options = Shaderc.shaderc_compile_options_initialize();
        if (options == 0) {
            Shaderc.shaderc_compiler_release(compiler);
            throw new PhotonException("Could not initialize GLSL compiler options");
        }

        long result = Shaderc.shaderc_compile_into_spv(compiler, source, shaderType.toShadercConstant(), filename, "main", options);

        if (result == 0) {
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
            throw new PhotonException("Could not compile GLSL shader");
        }
        if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
            String errorMessage = Shaderc.shaderc_result_get_error_message(result);
            Shaderc.shaderc_result_release(result);
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
            throw new PhotonException("GLSL compilation failed: " + errorMessage);
        }
        Shaderc.shaderc_compile_options_release(options);
        Shaderc.shaderc_compiler_release(compiler);

        ByteBuffer byteCode = Shaderc.shaderc_result_get_bytes(result);
        return new SPIRV(result, byteCode);
    }

    public record SPIRV(long handle, ByteBuffer byteCode) {

    }

}
