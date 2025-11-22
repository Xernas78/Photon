package dev.xernas.photon.opengl;

import dev.xernas.photon.api.shader.IUniform;
import org.joml.*;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;

import java.awt.*;

public class GLUniform<T> implements IUniform<T> {

    private final String name;
    private final int location;

    private T value;

    public GLUniform(String name, int location) {
        this.name = name;
        this.location = location;
    }

    @Override
    public void set(T value) {
        this.value = value;
        if (value == null) throw new IllegalArgumentException("Trying to set uniform with null value: " + name);
        try (MemoryStack stack = MemoryStack.stackPush()) {
            if (value instanceof Integer) {
                GL45.glUniform1i(location, (Integer) value);
            } else if (value instanceof Float) {
                GL45.glUniform1f(location, (Float) value);
            } else if (value instanceof Double) {
                GL45.glUniform1f(location, ((Double) value).floatValue());
            } else if (value instanceof Boolean) {
                GL45.glUniform1i(location, (Boolean) value ? 1 : 0);
            } else if (value instanceof Matrix4f) {
                GL45.glUniformMatrix4fv(location, false, ((Matrix4f) value).get(stack.mallocFloat(16)));
            } else if (value instanceof Matrix3f) {
                GL45.glUniformMatrix3fv(location, false, ((Matrix3f) value).get(stack.mallocFloat(9)));
            } else if (value instanceof Vector3f v) {
                GL45.glUniform3f(location, v.x, v.y, v.z);
            } else if (value instanceof Vector2f v) {
                GL45.glUniform2f(location, v.x, v.y);
            } else if (value instanceof Vector2i v) {
                GL45.glUniform2i(location, v.x, v.y);
            } else if (value instanceof Vector3i v) {
                GL45.glUniform3i(location, v.x, v.y, v.z);
            } else if (value instanceof Color color) {
                GL45.glUniform3f(location, color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
            }
        }
    }

    @Override
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
