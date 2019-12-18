package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;

import java.util.Random;

public class Enemy extends GameObject {
    private Handler handler;

    private int speed = 1;
    private int dir = 0; //0 down, 1 left, 2 top, 3 right
    private boolean canMove = true;

    private int hp = 100;
    private Random r = new Random();
    private int choose = 0;
    private boolean playerChase = false;
    private boolean playerSee = false;
    private boolean inRadius = false;
    private int scoutTime = 0;
    private int lastSeenX = 0;
    private int lastSeenY = 0;

    private Bitmap shadow = null;
    private Bitmap[][] sprites = new Bitmap[4][3];

    private static int detectionRadius = 100;
    private int runningAnimation = 0;
    private int animationIndex = 0;

    private int xOrigin = 0;
    private int yOrigin = 0;
    private int pCenteredX = 0;
    private int pCenteredY = 0;


    public Enemy(int x, int y, int width, int height, ID id, Handler handler, Bitmap sprite) {
        super(x, y, width, height, id, sprite);
        this.handler = handler;

        sprites[0][0] = this.handler.getSpritesheet().grabImage(2, 2, 32, 32);
        sprites[0][1] = this.handler.getSpritesheet().grabImage(3, 2, 32, 32);
        sprites[0][2] = this.handler.getSpritesheet().grabImage(4, 2, 32, 32);
        sprites[3][0] = this.handler.getSpritesheet().grabImage(5, 2, 32, 32);
        sprites[3][1] = this.handler.getSpritesheet().grabImage(6, 2, 32, 32);
        sprites[3][2] = this.handler.getSpritesheet().grabImage(7, 2, 32, 32);
        sprites[1][0] = Utils.bitmapFlipHorizontal(sprites[3][0]); //revert from right img
        sprites[1][1] = Utils.bitmapFlipHorizontal(sprites[3][1]); //revert from right img
        sprites[1][2] = Utils.bitmapFlipHorizontal(sprites[3][2]); //revert from right img
        sprites[2][0] = this.handler.getSpritesheet().grabImage(8, 2, 32, 32);
        sprites[2][1] = this.handler.getSpritesheet().grabImage(1, 3, 32, 32);
        sprites[2][2] = this.handler.getSpritesheet().grabImage(2, 3, 32, 32);

        shadow = handler.getSpritesheet().grabImage(5, 4, 32, 32);
    }

    private void collision() {
        for(int i = 0; i < handler.getObject().size(); i++) {
            GameObject tempObject = handler.getObject().get(i);

            //game optimization - check only nearest objects for collision
            if(tempObject.getId() == ID.Block && Utils.GetDistance(x, tempObject.getX(), y, tempObject.getY()) < Handler.CollisionRadius) {
                if(!Utils.place_free((int) (xOrigin + velX), yOrigin, getBounds(), tempObject.getBounds())) {
                    velX = 0;
                }
                if(!Utils.place_free(xOrigin, (int) (yOrigin + velY), getBounds(), tempObject.getBounds())) {
                    velY = 0;
                }
            }
        }
    }

    public int GetHP() {
        return hp;
    }

    public static int getRandom(int[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public void SetHP(int hp2) {
        int[] sounds = new int[] {R.raw.hit1, R.raw.hit2, R.raw.hit3, R.raw.hit4};
        MediaPlayer mp = MediaPlayer.create(handler.getContext(), getRandom(sounds));
        mp.start();
        hp = hp2;
    }

    public void update() {
        if(hp < 1) {
            handler.removeObject(this);
        }

        if(playerChase && !playerSee) {
            scoutTime++;
            if(scoutTime > 90) { //will stay on place for 1.5 secs looking for player
                playerChase = false;
            }
        }

        xOrigin = x - width/2;
        yOrigin = y - height/2;

        choose = r.nextInt(40);

        if(canMove) {
            int newX = (int) (x + velX);
            int newY = (int) (y + velY);
            if(newX > width/2 && newX < handler.getLevelDims().x - width/2) {
                x = newX;
            }
            if(newY > height/2 && newY < handler.getLevelDims().y - height/2) {
                y = newY;
            }

            collision();
        }

        pCenteredX = handler.GetPlayerObject().x;
        pCenteredY = handler.GetPlayerObject().y;

        inRadius = false;
        playerSee = false;
        if(Utils.GetDistance(x, pCenteredX, y, pCenteredY) < detectionRadius) {
            //System.out.println("Enemy in radius");
            inRadius = true;
            if(Utils.raytrace(x, y, pCenteredX, pCenteredY, handler)) { //is in LOS
                //System.out.println("Detected clear vision path to the player");
                lastSeenX = pCenteredX;
                lastSeenY = pCenteredY;
                scoutTime = 0;
                playerChase = true;
                playerSee = true;
            }
        }

        //change direction
        if(!playerChase) {
            if(choose == 0) {
                int choose2 = r.nextInt(3);
                if(choose2 == 1) {
                    velX = speed;
                    dir = 3;
                }else if(choose2 == 2){
                    velX = -speed;
                    dir = 1;
                }else {
                    velX = 0;
                }
                choose2 = r.nextInt(3);
                if(choose2 == 1) {
                    velY = speed;
                    dir = 0;
                }else if(choose2 == 2) {
                    velY = -speed;
                    dir = 2;
                }else {
                    velY = 0;
                }
            }else if(choose == 1) {
                //stopping for a while
                velX = 0;
                velY = 0;
                dir = 0;
            }
        }else {
            //System.out.println("Enemy is chasing the player.");
            //very primitive, needs pathfinding algo
            if(x - lastSeenX < 0) {
                velX = speed;
                dir = 3;
            }else {
                velX = -speed;
                dir = 1;
            }
            if(y - lastSeenY < 0) {
                velY = speed;
                dir = 0;
            }else {
                velY = -speed;
                dir = 2;
            }
            if(x == lastSeenX && y == lastSeenY && !playerSee) {
                playerChase = false;
            }
        }

        //animation
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
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(shadow, x - 16, y-12, null); //shadow
        canvas.drawBitmap(sprites[dir][animationIndex], x - 16, y - 16, null);
        if(handler.getDebugMode()) {
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.0f);
            if(playerSee) {
                paint.setColor(Color.RED);
            }else if(inRadius) {
                paint.setColor(Color.GREEN);
            }else {
                paint.setColor(Color.GRAY);
            }
            canvas.drawLine(x, y, pCenteredX, pCenteredY, paint); //debugging LOS line
            paint.setColor(Color.CYAN);
            canvas.drawRect(xOrigin, yOrigin, xOrigin + width, yOrigin + height, paint);
            paint.setColor(Color.GRAY);
            canvas.drawRect(lastSeenX, lastSeenY, lastSeenX + 1, lastSeenY + 1, paint);
        }
    }

    public Rect getBounds() {
        return new Rect(xOrigin, yOrigin, xOrigin + width, yOrigin + height);
    }
}
