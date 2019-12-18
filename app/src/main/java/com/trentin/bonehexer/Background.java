package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Background {

    private Bitmap bitmap;
    private int x, y, dx;

    public Background(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void update() {
        x += dx;

        if(x < 0) {
            x = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public void setVector(int dx) {
        this.dx = dx;
    }
}
