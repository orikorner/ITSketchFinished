package com.example.cabby333.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

public interface Shape {
    public int TEXT_SIZE = 100;
    public void drawShape(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources);
    public void drawText(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources, String data);
}
