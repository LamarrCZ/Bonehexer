package com.trentin.bonehexer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;

import timber.log.Timber;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int W_WIDTH = 428;
    private static final int W_HEIGHT = 240;

    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

    private Context context;
    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;
    private static Canvas frame = null;
    private SurfaceHolder surfaceHolder;
    private SpriteSheet ss;
    private VirtualJoystick virtualJoystick;
    private boolean cLocked = false;

    private int levelCounter = 0;
    private boolean loaded = false;

    private Bitmap floor = null;

    Point levelDims = null;
    Point realDims = null;

    private float scaleFactorX;
    private float scaleFactorY;

    public GamePanel(Context context) {
        super(context);

        SurfaceHolder h = getHolder();
        h.addCallback(this);

        this.context = context;

        setFocusable(true);
    }

    public void init() {
        camera = new Camera(0, 0);
        handler = new Handler(camera, frame);
        virtualJoystick = new VirtualJoystick(context, W_WIDTH, W_HEIGHT, handler);

        ss = new SpriteSheet(context);
        handler.setSpritesheet(ss);
        handler.setContext(context);
        //floor = ss.grabImage(8, 5, 32, 32);

        levelDims = new Point(Utils.randInt(16,  48), Utils.randInt(16, 48));
        //levelDims = new Point(8, 6); //testing purpose
        realDims = new Point(levelDims.x*32, levelDims.y*32); //calculation to real x y sizes
        handler.setLevelDims(realDims);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        scaleFactorX = metrics.widthPixels / (W_WIDTH * 1.f);
        scaleFactorY = metrics.heightPixels / (W_HEIGHT * 1.f);

        virtualJoystick.setScreenScaleX(scaleFactorX);
        virtualJoystick.setScreenScaleY(scaleFactorY);

        loadLevel();
    }

    public void nextLevel() {
        loaded = false;
        levelCounter++;
        init();
    }

    public void restart() {
        loaded = false;
        levelCounter = 0;
        init();
    }

    public void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.requestFocus();
        long now;
        long updateTime;
        long wait;

        while (isRunning) {
            now = System.nanoTime();

            if (!cLocked){
                frame = surfaceHolder.lockCanvas();
                cLocked = true;
            }

            update();
            draw(frame);

            updateTime = System.nanoTime() - now;
            //max 0 - fix for first negative values at start
            wait = Math.max(0, (OPTIMAL_TIME - updateTime) / 1000000);

            //Timber.i("RUNNING");

            if (cLocked) {
                surfaceHolder.unlockCanvasAndPost(frame);
                cLocked = false;
            }

            try {
                Thread.sleep(wait);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if(loaded) {
            if(handler.goingRestart()) {
                restart();
            }else {
                if(handler.GetPlayerObject() != null) {
                    camera.update(handler.GetPlayerObject());
                }
                handler.update();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if(canvas != null) {
            if (loaded) {
                final int savedState = canvas.save();
                canvas.scale(scaleFactorX, scaleFactorY);

                Paint paint = new Paint();

                paint.setColor(Color.parseColor("#341a27"));
                canvas.drawRect(0, 0, W_WIDTH, W_HEIGHT, paint);
                paint.setColor(Color.BLACK);
                ////
                canvas.translate(-camera.getX(), -camera.getY());
                //game optimization - render only visible background
                //System.out.println("SX: " + camera.getX() + " SY: " + camera.getY());
                int rounded_cameraX = Math.round(camera.getX() / 32) * 32;
                int rounded_cameraY = Math.round(camera.getY() / 32) * 32;

                int start_x = Math.max(0, rounded_cameraX - 32);
                int start_y = Math.max(0, rounded_cameraY - 48);
                int endX = Math.min(rounded_cameraX + W_WIDTH + 32, realDims.x);
                int endY = Math.min(rounded_cameraY + W_HEIGHT + 32, realDims.y);
                //System.out.println("SX: " + start_x + " SY: " + start_y + "EX: " + endX + " EY: " +endY);
                /*for (int xx = start_x; xx < endX; xx += 32) {
                    for (int yy = start_y; yy < endY; yy += 32) {
                        canvas.drawBitmap(floor, xx, yy, null);
                    }
                }*/
                paint.setColor(Color.parseColor("#625e4e"));
                //mobile optimization - dont draw tiled background, draw just color instead
                canvas.drawRect(start_x, start_y, endX, endY, paint);
                handler.draw(canvas);

                canvas.translate(camera.getX(), camera.getY());
                ////
                virtualJoystick.draw(canvas);

                canvas.restoreToCount(savedState);
            } else {
                //show black screen when not loaded
                final int savedState = canvas.save();
                canvas.scale(scaleFactorX, scaleFactorY);

                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                canvas.drawRect(0, 0, W_WIDTH, W_HEIGHT, paint);

                canvas.restoreToCount(savedState);
            }
        }
    }

    //loading level
    private void loadLevel() {
        //random level generator
        LevelGenerator level = new LevelGenerator(levelDims.x, levelDims.y);
        boolean[][] cellmap = level.generateMap();

        boolean playerCreated = false;
        boolean portalCreated = false;

        handler.setLevelDims(realDims);
        int enemyNumber = Utils.randInt(5, 6);
        //int enemyNumber = 0;
        int enemyCounter = 0;
        Random r = new Random();

        //random generate certain number of enemies on clear space
        int looping1 = 0;
        while (enemyCounter < enemyNumber) {
            looping1++;
            int xx = r.nextInt(levelDims.x);
            int yy = r.nextInt(levelDims.y);
            if (!cellmap[xx][yy]) {
                handler.addObject(new Enemy(xx * 32 + 12, yy * 32 + 12, 24, 24, ID.Enemy, handler, null));
                enemyCounter++;
            }
        }
        System.out.println("It took " + looping1 + " loop(s) to generate " + enemyNumber + " enemies.");

        int looping2 = 0;
        while (!portalCreated) {
            looping2++;
            int xx = r.nextInt(levelDims.x);
            int yy = r.nextInt(levelDims.y);
            if (!cellmap[xx][yy]) {
                handler.addObject(new Portal(xx * 32 + 12, yy * 32 + 12, 24, 24, ID.Portal, handler, null));
                System.out.println("It took " + looping2 + " loop(s) to generate portal at position [" + xx + "," + yy + "].");
                portalCreated = true;
            }
        }

        //create blocks
        int blocksCount = 0;
        //boolean[][] cellmapReal = new boolean[realDims.x][realDims.y];
        for (int i = 0; i < levelDims.x; i++) {
            for (int ii = 0; ii < levelDims.y; ii++) {
                if (cellmap[i][ii]) {

                    int block_col = Utils.randInt(1, 7);
                    int block_row = 6;
                    if (r.nextInt(11) == 9) {
                        block_row = 5;
                    }
                    handler.addObject(new Block(i * 32 + 16, ii * 32 + 16, 32, 32, ID.Block, ss.grabImage(block_col, block_row, 32, 32)));
                    blocksCount++;
                } else {
                    //if (!playerCreated && Utils.randInt(0, 100) < 10 && i > 3 && ii > 3) {
                    if (!playerCreated && Utils.randInt(0, 100) < 90 && i > 3 && ii > 3) {
                        Player p = new Player(i * 32 + 12, ii * 32 + 12, 24, 24, ID.Player, handler, null);
                        handler.SetPlayerObject(p);
                        playerCreated = true;
                    }
                }
            }
        }
        handler.setCellmap(cellmap);
        System.out.println("Generated " + blocksCount + " solid blocks in level of " + levelDims.x + "x" + levelDims.y + ".");
        loaded = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;

        init();

        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        /*scaleFactorX = getWidth() / (W_WIDTH * 1.f);
        scaleFactorY = getHeight() / (W_HEIGHT * 1.f);

        virtualJoystick.setScreenScaleX(scaleFactorX);
        virtualJoystick.setScreenScaleY(scaleFactorY);

        Timber.i("Scale factor is: %f, %f", scaleFactorX, scaleFactorY);*/
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        while(retry) {
            try {
                isRunning = false;
                thread.join();
            }catch(Exception e) {
                e.printStackTrace();
            }

            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        virtualJoystick.handleTouch(event);

        return true;
        //return super.onTouchEvent(event);
    }

    public float getScaleFactorX() {
        return scaleFactorX;
    }

    public void setScaleFactorX(float scaleFactorX) {
        this.scaleFactorX = scaleFactorX;
    }

    public float getScaleFactorY() {
        return scaleFactorY;
    }

    public void setScaleFactorY(float scaleFactorY) {
        this.scaleFactorY = scaleFactorY;
    }
}
