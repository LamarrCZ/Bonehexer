package com.trentin.bonehexer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

import java.util.ArrayList;

public class Handler {
    private ArrayList<GameObject> object = new ArrayList<GameObject>();
    private ArrayList<GameObject> nearest = new ArrayList<GameObject>();
    private ArrayList<GameObject> inview = new ArrayList<GameObject>();
    private Player pref = null;
    private Camera camera;
    private static Canvas frame = null;
    private SpriteSheet ss = null;
    private Context context;

    private boolean modeDebug = true;

    private boolean up , down , right , left, button_a, button_b = false;
    public static final int CollisionRadius = 50;

    private static final int W_WIDTH = 428;
    private static final int W_HEIGHT = 240;
    private static final int W_OUTSIDE_TOLERANCE = 32;
    private Point levelDims = new Point(0, 0);
    private boolean goingRestart = false;
    private boolean[][] cellmapReal = null;

    public Handler(Camera camera, Canvas frame) {
        this.camera = camera;
        Handler.frame = frame;
    }

    public SpriteSheet getSpritesheet() {
        return ss;
    }

    public boolean goingRestart() {
        return goingRestart;
    }

    public void setCellmap(boolean[][] cellmap) {
        cellmapReal = cellmap;
    }

    public boolean[][] getCellmap() {
        return cellmapReal;
    }

    public void setRestart() {
        object.clear(); //faster than removeAll();
        nearest.clear();
        inview.clear();
        goingRestart = true;
    }

    public void setContext(Context context) { this.context = context; }

    public Context getContext() { return context; }

    public void setSpritesheet(SpriteSheet ss) {
        this.ss = ss;
    }

    public boolean isButtonA() { return button_a; }

    public void setButtonA(boolean a) { button_a = a; }

    public boolean isButtonB() { return button_b; }

    public void setButtonB(boolean b) { button_b = b; }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setLevelDims(Point lvldims) {
        levelDims = lvldims;
    }

    public ArrayList<GameObject> getObject() {
        return object;
    }

    public ArrayList<GameObject> getNearest() {
        return nearest;
    }

    public Point getLevelDims() {
        return levelDims;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setDebugMode(boolean d) {
        modeDebug = d;
    }

    public boolean getDebugMode() {
        return modeDebug;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void update() {
        if(!goingRestart()) {
            if(isButtonB()) {
                setRestart();
            }

            if(isButtonA()) {
                pref.shoot();
            }

            nearest.clear();
            inview.clear();

            int camera_x = (int) camera.getX();
            int camera_y = (int) camera.getY();
            Rect camera_view = new Rect(camera_x - W_OUTSIDE_TOLERANCE, camera_y - W_OUTSIDE_TOLERANCE, camera_x + W_WIDTH + W_OUTSIDE_TOLERANCE, camera_y + W_HEIGHT + W_OUTSIDE_TOLERANCE);
            //System.out.println("Camera X: " + camera_view.left + ", Y: " + camera_view.top + ", W: " + camera_view.right + ", H: " +camera_view.bottom);

            for(int i = 0; i < object.size(); i++) {
                GameObject tempObject = object.get(i);
                //game optimization #1 - check if object is in view
                //Point tempObjectPoint = new Point(tempObject.getX(), tempObject.getY());
                if(camera_view.contains(tempObject.getBounds())) {
                    inview.add(tempObject);
                    //game optimization #4 - tick only inview objects??
                    //game optimization #2 - don't tick solid objects..
                    if(tempObject.getId() != ID.Block) {
                        tempObject.update();
                    }
                    //game optimization #3 - fill nearest objects array with nearest objects, skip player obviously
                    if(pref != null) {
                        if(Utils.GetDistance(pref.getX(), tempObject.getX(), pref.getY(), tempObject.getY()) < CollisionRadius) {
                            nearest.add(tempObject);
                        }
                    }
                }
            }

            //tick player after all of this
            if(pref != null) {
                pref.update();
            }
            //for (GameObject s : nearest) { System.out.println(s.x); }
            /*System.out.println(inview.size());
            System.out.println(nearest.size());*/
        }
    }

    public void draw(Canvas canvas) {
        //drawing only objects in view
        if(!goingRestart()) {
            for (GameObject o : inview) { o.draw(canvas); }
            if(pref != null) {
                pref.draw(canvas);
            }
        }
    }

    public void SetPlayerObject(Player tempObject) {
        pref = tempObject;
    }

    public Player GetPlayerObject() {
        if(pref != null) {
            return pref;
        }
        return null;
    }

    public GameObject addObject(GameObject tempObject) {
        object.add(tempObject);
        return object.get(object.size()-1);
    }

    public void removeObject(GameObject tempObject) {
        object.remove(tempObject);
    }
}
