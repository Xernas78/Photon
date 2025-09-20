package dev.xernas.photon.window;

import dev.xernas.photon.Initializable;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;
import org.joml.Vector2f;

import java.awt.*;

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

    void setCursorPosition(int x, int y);

    void disableCursor();

    void hideCursor();

    void showCursor();

    void setCursorLocked(boolean locked);

    void setCursorShape(CursorShape mode) throws PhotonException;

    WindowHints getHints();

    Input getInput();

    Color getBackgroundColor();

    void setBackgroundColor(Color color);

    void show(int monitorIndex, boolean maximized);

    void minimize();

    void maximize();

    void restore();

    boolean isMaximized();

    void hide();

    boolean shouldClose();

    void setShouldClose(boolean shouldClose);

    void setPosition(int x, int y);

    void close();

    void stop();

    default boolean isHorizontal() {
        return getWidth() > getHeight();
    }

    default boolean isVertical() {
        return getHeight() > getWidth();
    }

    default float getAspectRatio() {
        if (isHorizontal()) return (float) getWidth() / getHeight();
        else return (float) getHeight() / getWidth();
    }

    default Vector2f getAspectRatios() {
        float horizontalAspectRatio = (float) getWidth() / getHeight();
        float verticalAspectRatio = (float) getHeight() / getWidth();
        return new Vector2f(horizontalAspectRatio, verticalAspectRatio);
    }
}
