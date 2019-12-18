package com.trentin.bonehexer;

public class Camera {
    private float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(GameObject object) {
        x += ((object.getX() - x) - 428/2.0f) * 0.05f;
        y += ((object.getY() - y) - 240/2.0f) * 0.05f;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
