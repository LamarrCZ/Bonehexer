package com.trentin.bonehexer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;

import java.io.InputStream;

public class SpriteSheet {
    private BitmapRegionDecoder decoder = null;

    public SpriteSheet(Context context) {
        //converting to 8bit helped so much, also using 128px instead 256px, let the bitmap scale
        InputStream inputStream = context.getResources().openRawResource(R.raw.spritesheet2128);

        try {
            decoder = BitmapRegionDecoder.newInstance(inputStream, false);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap grabImage(int col, int row, int width, int height) {
        int swidth = width / 2;
        int sheight = height / 2;
        Rect rect = new Rect((col-1)*swidth, (row-1)*sheight, col*swidth, row*sheight);

        if(decoder != null) {
            Bitmap b = decoder.decodeRegion(rect, null);
            return Bitmap.createScaledBitmap(b, width, height, false);
        }
        return null;
    }
}
