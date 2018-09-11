package com.example.cabby333.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * A class which represents a 2D Coordinate
 */
public class Coord implements Shape {

    private float xCoord;
    private float yCoord;

    Coord(float x, float y) {
        xCoord = x;
        yCoord = y;
    }

    public float getxCoord() {
        return xCoord;
    }

    public float getyCoord() {
        return yCoord;
    }

    public void setxCoord(float xCoord) {
        this.xCoord = xCoord;
    }

    public void setyCoord(float yCoord) {
        this.yCoord = yCoord;
    }

    @Override
    public void drawShape(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources) {

    }

    @Override
    public void drawText(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources, String data) {

    }

    @Override
    public String toString() {
        return String.valueOf(xCoord) + "," + String.valueOf(yCoord);
    }
}
