package com.oni.onlyflashnight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

/**
 * Created by CuD HniM on 19/03/14.
 */
public class Util {
    public static BitmapDrawable getDrawableForDevice(Context context, float baseDeviceWidthDP,
            float baseImageWidthDP, float baseImageHeightDP, BitmapDrawable image,
            boolean scaleUpOnly) {

        // get the current device width
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float widthDP = metrics.widthPixels / metrics.density;

        // calculate scaled image width and height
        float scaleFactor = widthDP / baseDeviceWidthDP;

        // set scale factor to 1 if the scale factor is smaller than 1
        if (scaleUpOnly && scaleFactor < 1) {
            scaleFactor = 1;
        }

        // get scaled image size (Pixel)
        float width = baseImageWidthDP * scaleFactor * metrics.density;
        float height = baseImageHeightDP * scaleFactor * metrics.density;

        // get scaled bitmap
        Bitmap scaledBitmap =
                Bitmap.createScaledBitmap(image.getBitmap(), (int) width, (int) height, true);

        return new BitmapDrawable(context.getResources(), scaledBitmap);
    }
}