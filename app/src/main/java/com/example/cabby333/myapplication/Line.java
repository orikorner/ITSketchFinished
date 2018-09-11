package com.example.cabby333.myapplication;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * A class which represents a Straight Line
 */
public class Line implements Shape {

    private Coord firstCoord;
    private Coord secondCoord;

    Line(float x1, float y1, float x2, float y2) {
        firstCoord = new Coord(x1, y1);
        secondCoord = new Coord(x2, y2);
    }

    public void setFirstCoord(float x, float y) {
        firstCoord.setxCoord(x);
        firstCoord.setyCoord(y);
    }

    public void setSecondCoord(float x, float y) {
        secondCoord.setxCoord(x);
        secondCoord.setyCoord(y);
    }

    public Coord getFirstCoord() {
        return firstCoord;
    }

    public Coord getSecondCoord() {
        return secondCoord;
    }

    @Override
    public void drawShape(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources) {
        canvas.drawLine(firstCoord.getxCoord(), firstCoord.getyCoord(), secondCoord.getxCoord(), secondCoord.getyCoord(), paint);
    }

    @Override
    public void drawText(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources, String data) {
        paint.setTextSize(TEXT_SIZE);

        float midX = (firstCoord.getxCoord() + secondCoord.getxCoord()) / 2f;
        float midY = (firstCoord.getyCoord() + secondCoord.getyCoord()) / 2f;

        if (Math.abs(secondCoord.getyCoord()-firstCoord.getyCoord()) < 150)
        {
            float minX = Math.min(firstCoord.getxCoord(),secondCoord.getxCoord());
            midX = (midX + minX) / 2f;
            midY += TEXT_SIZE;
        } else {
            if (firstCoord.getyCoord() < secondCoord.getyCoord()) {
                midX += (TEXT_SIZE / 2);
                midY -= (TEXT_SIZE / 2);
            } else {
                midX += (TEXT_SIZE / 2);
                midY += (TEXT_SIZE / 2);
            }
        }
        // TODO make more robust to flat slope lines

        canvas.drawText("Length: " + data + " cm", midX, midY, paint);

        imageView.setImageDrawable(new BitmapDrawable(resources, mutableBitmap));
    }

    @Override
    public String toString() {
        return firstCoord.toString() + "," + secondCoord.toString();
    }
}
