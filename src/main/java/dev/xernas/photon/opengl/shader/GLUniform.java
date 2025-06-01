package dev.xernas.photon.opengl.shader;

import dev.xernas.photon.render.shader.IUniform;
import org.joml.*;
import org.lwjgl.system.MemoryStack;

import java.awt.*;

import static org.lwjgl.opengl.GL20.*;

public class GLUniform<T> implements IUniform<T> {

    private final String name;
    private final int location;

    private T value;

    public GLUniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    public void set(T value) {
        this.value = value;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (value instanceof Integer) {
                glUniform1i(location, (Integer) value);
            } else if (value instanceof Float) {
                glUniform1f(location, (Float) value);
            } else if (value instanceof Double) {
                glUniform1f(location, ((Double) value).floatValue());
            } else if (value instanceof Boolean) {
                glUniform1i(location, (Boolean) value ? 1 : 0);
            } else if (value instanceof Matrix4f) {
                glUniformMatrix4fv(location, false, ((Matrix4f) value).get(stack.mallocFloat(16)));
            } else if (value instanceof Matrix3f) {
                glUniformMatrix3fv(location, false, ((Matrix3f) value).get(stack.mallocFloat(9)));
            } else if (value instanceof Vector3f v) {
                glUniform3f(location, v.x, v.y, v.z);
            } else if (value instanceof Vector2f v) {
                glUniform2f(location, v.x, v.y);
            } else if (value instanceof Vector2i v) {
                glUniform2i(location, v.x, v.y);
            } else if (value instanceof Vector3i v) {
                glUniform3i(location, v.x, v.y, v.z);
            } else if (value instanceof Color color) {
                glUniform3f(location, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
            }
        }
    }

    public T get() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getLocation() {
        return location;
    }

}
