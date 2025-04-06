package dev.xernas.photon.window;

import dev.xernas.photon.Initializable;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public interface IWindow extends Initializable {

    @Override
    void init() throws PhotonException;

    void update() throws PhotonException;

    void updateInput() throws PhotonException;

    String getDefaultTitle();

    String getTitle();

    void setTitle(String title);

    int getWidth();

    int getHeight();

    void resize(int width, int height);

    boolean isKeyPressed(Key key);

    boolean isMouseButtonPressed(Key button);

    WindowHints getHints();

    Input getInput();

    Color getBackgroundColor();

    void setBackgroundColor(Color color);

    void show();

    void maximize();

    void hide();

    boolean shouldClose();

    void close();

    void stop();

    default float getAspectRatio() {
        return getWidth() / (float) getHeight();
    }
}
