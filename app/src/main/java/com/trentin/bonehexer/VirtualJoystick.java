package com.trentin.bonehexer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;

import timber.log.Timber;

public class VirtualJoystick {
    private Bitmap bit_left, bit_right, bit_top, bit_bottom, bit_a, bit_b;
    private Bitmap abit_left, abit_right, abit_top, abit_bottom, abit_a, abit_b;

    private int left_x = 16;
    private int right_x = 84;
    private int top_x = 50;
    private int bottom_x = top_x;

    private Handler handler;

    private Rect rect_left, rect_right, rect_top, rect_bottom, rect_a, rect_b;

    private ArrayList<Rect> buttons;

    private int left_y, right_y, top_y, bottom_y, a_x, a_y, b_x, b_y;

    private boolean left, right, top, bottom, a, b;

    private float screenScaleX, screenScaleY = 0;

    public VirtualJoystick(Context context, int w, int h, Handler handler) {
        buttons = new ArrayList<>();

        this.handler = handler;

        bit_left = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_left));
        bit_right = Utils.bitmapFlipHorizontal(bit_left);
        bit_top = Utils.bitmapRotate(bit_left, 90.0f);
        bit_bottom = Utils.bitmapFlipVertical(bit_top);

        bit_a = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_a));
        bit_b = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_b));

        abit_left = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_left_active));
        abit_right = Utils.bitmapFlipHorizontal(abit_left);
        abit_top = Utils.bitmapRotate(abit_left, 90.0f);
        abit_bottom = Utils.bitmapFlipVertical(abit_top);

        abit_a = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_a_active));
        abit_b = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.button_b_active));

        left_y = h - 82;
        right_y = left_y;

        top_y = h - 116;
        bottom_y = h - 48;

        b_x = w - 48;
        b_y = 16;

        a_x = w - 48;
        a_y = h - 82;

        rect_left = new Rect(left_x, left_y, left_x + 32, left_y + 32);
        rect_right = new Rect(right_x, right_y, right_x + 32, right_y + 32);
        rect_top = new Rect(top_x, top_y, top_x + 32, top_y + 32);
        rect_bottom = new Rect(bottom_x, bottom_y, bottom_x + 32, bottom_y + 32);

        rect_b = new Rect(b_x, b_y, b_x + 32, b_y + 32);
        rect_a = new Rect(a_x, a_y, a_x + 32, a_y + 32);

        buttons.add(rect_left);
        buttons.add(rect_right);
        buttons.add(rect_top);
        buttons.add(rect_bottom);
        buttons.add(rect_b);
        buttons.add(rect_a);
    }

    public void handleTouch(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        /*handler.setLeft(false);
        handler.setRight(false);
        handler.setUp(false);
        handler.setDown(false);
        left = right = top = bottom = false;*/

        //multitouch support
        for (int i = 0; i < pointerCount; i++) {
            int x = Math.round(event.getX(i) / screenScaleX);
            int y = Math.round(event.getY(i) / screenScaleY);

            Timber.i("Touch registered: X - %d, Y - %d", x, y);

            int motion = event.getAction();
            switch(motion) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    if (Utils.pointCollision(rect_left, x, y)) {
                        handler.setLeft(true);
                        left = true;
                    }

                    if (Utils.pointCollision(rect_right, x, y)) {
                        handler.setRight(true);
                        right = true;
                    }

                    if (Utils.pointCollision(rect_top, x, y)) {
                        handler.setUp(true);
                        top = true;
                    }

                    if (Utils.pointCollision(rect_bottom, x, y)) {
                        handler.setDown(true);
                        bottom = true;
                    }

                    if (Utils.pointCollision(rect_a, x, y)) {
                        handler.setButtonA(true);
                        a = true;
                    }

                    if (Utils.pointCollision(rect_b, x, y)) {
                        handler.setButtonB(true);
                        b = true;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.setLeft(false);
                    handler.setRight(false);
                    handler.setUp(false);
                    handler.setDown(false);
                    handler.setButtonA(false);
                    handler.setButtonB(false);
                    left = right = top = bottom = a = b = false;
                    break;
            }
        }
    }

    public void draw(Canvas canvas) {
        if(left) {
            canvas.drawBitmap(abit_left, null, rect_left, null);
        }else{
            canvas.drawBitmap(bit_left, null, rect_left, null);
        }

        if(right) {
            canvas.drawBitmap(abit_right, null, rect_right, null);
        }else{
            canvas.drawBitmap(bit_right, null, rect_right, null);
        }

        if(top) {
            canvas.drawBitmap(abit_top, null, rect_top, null);
        }else{
            canvas.drawBitmap(bit_top, null, rect_top, null);
        }

        if(bottom) {
            canvas.drawBitmap(abit_bottom, null, rect_bottom, null);
        }else{
            canvas.drawBitmap(bit_bottom, null, rect_bottom, null);
        }

        if(a) {
            canvas.drawBitmap(abit_a, null, rect_a, null);
        }else{
            canvas.drawBitmap(bit_a, null, rect_a, null);
        }

        if(b) {
            canvas.drawBitmap(abit_b, null, rect_b, null);
        }else{
            canvas.drawBitmap(bit_b, null, rect_b, null);
        }

    }

    public void setScreenScaleX(float screenScaleX) {
        this.screenScaleX = screenScaleX;
    }

    public void setScreenScaleY(float screenScaleY) {
        this.screenScaleY = screenScaleY;
    }
}
