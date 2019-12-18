package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Bullet extends GameObject {
    private Handler handler;
    private static int damage = 20;

    public Bullet(int x, int y, ID id, Handler handler, double angle, Bitmap sprite) {
        super(x, y, id, sprite);
        this.handler = handler;
        int speed = 6;

        velX = (float)(Math.cos(Math.toRadians(angle))*speed);
        velY = (float)(Math.sin(Math.toRadians(angle))*speed);
    }

    public void update() {
        for(int i = 0; i < handler.getObject().size(); i++) {
            GameObject tempObject = handler.getObject().get(i);

            //optimization
            if((tempObject.getId() == ID.Block || tempObject.getId() == ID.Enemy) && Utils.GetDistance(x, tempObject.getX(), y, tempObject.getY()) < Handler.CollisionRadius) {
                if(!Utils.place_free((int) (x + velX), y, getBounds(), tempObject.getBounds()) || !Utils.place_free(x, (int) (y + velY), getBounds(), tempObject.getBounds())) {
                    if(tempObject.getId() == ID.Enemy) {
                        Enemy enemy = (Enemy) tempObject;
                        //knockback
                        enemy.setX(enemy.getX() + (int) (velX));
                        enemy.setY(enemy.getY() + (int) (velY));
                        //hp reduction
                        enemy.SetHP(enemy.GetHP() - damage);
                    }
                    handler.removeObject(this);
                }
            }
        }

        x += velX;
        y += velY;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(sprite, x-16, y-16, null); //centering x - (32-24)/2; y (32-28)/2
    }

    public Rect getBounds() {
        return new Rect(x-4, y-4, x + 8, y + 8);
    }

}
