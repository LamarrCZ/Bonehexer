package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import timber.log.Timber;

public class Player extends GameObject {
    private Handler handler;
    private int speed = 2;
    private int dir = 0; //0 down, 1 left, 2 top, 3 right
    private double dirAngle = 0;
    private boolean going_to_shoot = false;
    private boolean can_shoot = true;
    private boolean canMove = true;

    private Bitmap shadow = null;
    private Bitmap[][] sprites = new Bitmap[4][3];
    private Bitmap bullet = null;

    private int timer_shooting = -1;
    private int runningAnimation = 0;
    private int animationIndex = 0;

    private int hp = 100;

    private int xOrigin = 0;
    private int yOrigin = 0;

    public Player(int x, int y, ID id, Handler handler, Bitmap sprite) {
        super(x, y, id, sprite);
        this.handler = handler;
    }

    public Player(int x, int y, int width, int height, ID id, Handler handler, Bitmap sprite) {
        super(x, y, width, height, id, sprite);
        this.handler = handler;
        //store animation
        sprites[0][0] = this.handler.getSpritesheet().grabImage(1, 1, 32, 32);
        sprites[0][1] = this.handler.getSpritesheet().grabImage(2, 1, 32, 32);
        sprites[0][2] = this.handler.getSpritesheet().grabImage(3, 1, 32, 32);
        sprites[3][0] = this.handler.getSpritesheet().grabImage(4, 1, 32, 32);
        sprites[3][1] = this.handler.getSpritesheet().grabImage(5, 1, 32, 32);
        sprites[3][2] = this.handler.getSpritesheet().grabImage(6, 1, 32, 32);
        sprites[1][0] = Utils.bitmapFlipHorizontal(sprites[3][0]); //revert from right img
        sprites[1][1] = Utils.bitmapFlipHorizontal(sprites[3][1]); //revert from right img
        sprites[1][2] = Utils.bitmapFlipHorizontal(sprites[3][2]); //revert from right img
        sprites[2][0] = this.handler.getSpritesheet().grabImage(7, 1, 32, 32);
        sprites[2][1] = this.handler.getSpritesheet().grabImage(8, 1, 32, 32);
        sprites[2][2] = this.handler.getSpritesheet().grabImage(1, 2, 32, 32);

        shadow = handler.getSpritesheet().grabImage(4, 4, 32, 32);

        bullet = handler.getSpritesheet().grabImage(6, 4, 32, 32);
    }

    public void update() {
        xOrigin = x - width/2;
        yOrigin = y - height/2;
        //System.out.println("X: " + x + "_" + xOrigin +" Y: " + y + "_" + yOrigin);

        collision();

        //level boundary check
        if(canMove) {
            int newX = (int) (x + velX);
            int newY = (int) (y + velY);
            if(newX > width/2 && newX < handler.getLevelDims().x - width/2) {
                x = newX;
            }
            if(newY > height/2 && newY < handler.getLevelDims().y - height/2) {
                y = newY;
            }
        }

        if(timer_shooting > -1) {
            timer_shooting++;
            if(timer_shooting > 15) { //14 ticks to shoot again
                can_shoot = true;
                timer_shooting = -1;
            }
        }

        //MOVEMENT
        if(handler.isUp()) {
            velY = -speed;
            dir = 2;
            dirAngle = 270.0;
        }
        else if(!handler.isDown()) velY = 0;

        if(handler.isDown()) {
            velY = speed;
            dir = 0;
            dirAngle = 90.0;
        }
        else if(!handler.isUp()) velY = 0;

        if(handler.isLeft()) {
            velX = -speed;
            dir = 1;
            dirAngle = 180.0;
        }
        else if(!handler.isRight()) velX = 0;

        if(handler.isRight()) {
            velX = speed;
            dir = 3;
            dirAngle = 0.0;
        }
        else if(!handler.isLeft()) velX = 0;

        if(velX != 0 || velY != 0) {
            runningAnimation++;
            if(runningAnimation / 7 > 1) {
                animationIndex++;
                if(animationIndex > 2) {
                    animationIndex = 0;
                }
                runningAnimation = 0;
            }
        } else {
            animationIndex = 0; //reset to idle position when not moving
        }

        //SHOOTING
        if(going_to_shoot && can_shoot) {
            going_to_shoot = false;
            handler.addObject(new Bullet(x, y, ID.Bullet, handler, dirAngle, bullet));
            can_shoot = false;
            timer_shooting = 0;
        }
    }

    public void shoot() {
        going_to_shoot = true;
    }

    public void setShooting(boolean shoot) {
        going_to_shoot = shoot;
    }

    private void collision() {
        int size = handler.getNearest().size();

        for(int i = 0; i < size; i++) {
            GameObject tempObject = handler.getNearest().get(i);

            if(tempObject.getId() == ID.Block) {
                /*if(!Utils.place_free((int) (xOrigin + velX), yOrigin, getBounds(), tempObject.getBounds())) {
                    velX = 0;
                }
                if(!Utils.place_free(xOrigin, (int) (yOrigin + velY), getBounds(), tempObject.getBounds())) {
                    velY = 0;
                }*/
                if(!Utils.place_free(getUpdatedBoundsX((int) velX), tempObject.getBounds())) {
                    velX = 0;
                }
                if(!Utils.place_free(getUpdatedBoundsY((int) velY), tempObject.getBounds())) {
                    velY = 0;
                }
            }
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(shadow, x-16, y-6, null); //shadow
        canvas.drawBitmap(sprites[dir][animationIndex], x-16, y-16, null); //-16 -16
        if(handler.getDebugMode()) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.0f);
            paint.setColor(Color.CYAN);
            canvas.drawRect(xOrigin, yOrigin, xOrigin + width, yOrigin + height, paint);
        }
    }

    public Rect getUpdatedBoundsX(int velX) {
        return new Rect(xOrigin + velX, yOrigin, xOrigin + velX + width, yOrigin + height);
    }

    public Rect getUpdatedBoundsY(int velY) {
        return new Rect(xOrigin, yOrigin + velY, xOrigin + width, yOrigin + velY + height);
    }

    public Rect getBounds() {
        return new Rect(xOrigin, yOrigin, xOrigin + width, yOrigin + height);
    }
}
