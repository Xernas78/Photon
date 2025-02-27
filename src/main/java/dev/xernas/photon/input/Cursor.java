package dev.xernas.photon.input;

public class Cursor {

    private double x, y;

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void update(double x, double y) {
        this.x = x;
        this.y = y;
    }

}
