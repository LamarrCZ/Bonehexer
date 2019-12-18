package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Portal extends GameObject {
    private Handler handler;

    private Bitmap[] sprites = new Bitmap[4];

    private int runningAnimation = 0;
    private int animationIndex = 0;

    private int xOrigin = 0;
    private int yOrigin = 0;

    public Portal(int x, int y, int width, int height, ID id, Handler handler, Bitmap sprite) {
        super(x, y, width, height, id, sprite);
        this.handler = handler;

        sprites[0] = this.handler.getSpritesheet().grabImage(1, 8, 32, 32);
        sprites[1] = this.handler.getSpritesheet().grabImage(2, 8, 32, 32);
        sprites[2] = this.handler.getSpritesheet().grabImage(3, 8, 32, 32);
        sprites[3] = sprites[1];

        xOrigin = x - width/2;
        yOrigin = y - height/2;
    }

    private void collision() {
        GameObject pObject = handler.GetPlayerObject();
        //optimization - not precise collision
        if(pObject != null) {
            if(Utils.GetDistance(x, pObject.getX(), y, pObject.getY()) < 14) {
                handler.setRestart();
            }
        }
    }

    public void update() {
        collision();

        runningAnimation++;
        if(runningAnimation / 7 > 1) {
            animationIndex++;
            if(animationIndex > 2) {
                animationIndex = 0;
            }
            runningAnimation = 0;
        }

    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprites[animationIndex], xOrigin, yOrigin, null);
    }

    public Rect getBounds() {
        return new Rect(xOrigin, yOrigin, width, height);
    }

}
