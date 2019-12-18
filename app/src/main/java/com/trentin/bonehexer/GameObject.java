package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class GameObject {
    protected int x, y, width = 0, height = 0;
    protected float velX = 0, velY = 0;
    protected ID id;
    protected Bitmap sprite;

    public GameObject(int x, int y, ID id, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.sprite = sprite;
    }

    public GameObject(int x, int y, int width, int height, ID id, Bitmap sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
        this.sprite = sprite;
    }

    public abstract void update();
    public abstract void draw(Canvas canvas);
    public abstract Rect getBounds();

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

}
