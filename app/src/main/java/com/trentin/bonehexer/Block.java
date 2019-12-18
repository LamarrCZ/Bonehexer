package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Block extends GameObject {
    private int xOrigin = 0;
    private int yOrigin = 0;

    public Block(int x, int y, ID id, Bitmap sprite) {
        super(x, y, id, sprite);
    }

    public Block(int x, int y, int width, int height, ID id, Bitmap sprite) {
        super(x, y, width, height, id, sprite);
        xOrigin = x - width/2;
        yOrigin = y - height/2;
    }

    public void update() {

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, xOrigin, yOrigin, null);
    }

    public Rect getBounds() {
        return new Rect(xOrigin, yOrigin, xOrigin + width, yOrigin + height);
    }
}
