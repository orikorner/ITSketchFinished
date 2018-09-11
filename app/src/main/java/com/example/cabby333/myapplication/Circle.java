package com.example.cabby333.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * A class which represents a Circle, it is defined by a 2D coordinate of it's center and radius
 */
public class Circle implements Shape {

    private Coord center;
    private double radius;

    Circle(float xMid, float yMid, float xRad, float yRad) {
        center = new Coord(xMid, yMid);
        radius = Math.hypot(xMid-xRad, yMid-yRad);
    }

    @Override
    public void drawShape(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources) {
        canvas.drawCircle(center.getxCoord(), center.getyCoord(), (float)radius, paint);
        imageView.setImageDrawable(new BitmapDrawable(resources, mutableBitmap));
    }

    @Override
    public void drawText(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources, String data) {
        paint.setTextSize(TEXT_SIZE);
        String[] dataArr = data.split(",");
        float midX = center.getxCoord() - ((float)radius / 1.3f);
        float midY = center.getyCoord() - ((float)radius / 5f);
        canvas.drawText("Radius: " + dataArr[0] + " cm", midX, midY, paint);
        canvas.drawText("Circumference: " + dataArr[1] + " cm", midX, midY + TEXT_SIZE, paint);
        canvas.drawText("Area: " + dataArr[2] + " cm^2", midX, midY + TEXT_SIZE * 2, paint);
        imageView.setImageDrawable(new BitmapDrawable(resources, mutableBitmap));
    }



    @Override
    public String toString() {
        return center.toString() + "," + String.valueOf(radius);
    }
}
