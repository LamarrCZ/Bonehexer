package com.trentin.bonehexer;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static Bitmap bitmapFlip(Bitmap image, float cx, float cy) {
        Matrix matrix = new Matrix();
        matrix.preScale(cx, cy);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    public static Bitmap bitmapFlipVertical(Bitmap image) {
        return bitmapFlip(image, 1.0f, -1.0f);
    }

    public static Bitmap bitmapFlipHorizontal(Bitmap image) {
        return bitmapFlip(image, -1.0f, 1.0f);
    }

    public static Bitmap bitmapRotate(Bitmap image, float rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    public static boolean pointCollision(Rect object, int x, int y) {
        if(object.contains(x, y)) {
            return true;
        }
        return false;
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean place_free(int x, int y, Rect myRect, Rect otherRect) {
        myRect.left = x;
        myRect.top = y;
        if (myRect.intersect(otherRect)) {
            return false;
        }
        return true;
    }

    public static boolean place_free(Rect myRect, Rect otherRect) {
        if (myRect.intersect(otherRect)) {
            return false;
        }
        return true;
    }

    public static boolean raytrace(int x0, int y0, int x1, int y1, Handler handler)
    {
        //ultra fast LOS algorithm
        //credit http://playtechs.blogspot.com/2007/03/raytracing-on-grid.html
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int x = x0;
        int y = y0;
        int n = 1 + dx + dy;
        int x_inc = (x1 > x0) ? 1 : -1;
        int y_inc = (y1 > y0) ? 1 : -1;
        int error = dx - dy;
        dx *= 2;
        dy *= 2;

        for (; n > 0; --n)
        {
            if(handler.getCellmap()[x/32][y/32] == true) {
                return false; //there is an obstacle between
                //simplified to 32x32 grid - much faster
            }

            if (error > 0)
            {
                x += x_inc;
                error -= dy;
            }
            else
            {
                y += y_inc;
                error += dx;
            }
        }
        return true;
    }

    public static double GetDistance(int x1, int x2, int y1, int y2) {
        return Math.hypot(x1-x2, y1-y2);
    }
}
