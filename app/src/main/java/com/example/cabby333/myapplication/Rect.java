package com.example.cabby333.myapplication;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * A class which represents a Rectangle
 */
@SuppressLint("ValidFragment")
public class Rect extends ImageSketchFragment implements Shape {

    private Line top;
    private Line right;
    private Line bottom;
    private Line left;

    Rect(Line top, Line right, Line bottom, Line left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        adjustRect();
    }

    /**
     * If adjacent lines are crossing Exactly, need to rectify them to make them so.
     * Such scenario occurs when user's clicks are not accurate.
     */
    private void adjustRect() {
        right.setFirstCoord(top.getSecondCoord().getxCoord(), top.getSecondCoord().getyCoord());
        bottom.setFirstCoord(right.getSecondCoord().getxCoord(), right.getSecondCoord().getyCoord());
        left.setFirstCoord(bottom.getSecondCoord().getxCoord(), bottom.getSecondCoord().getyCoord());
        left.setSecondCoord(top.getFirstCoord().getxCoord(), top.getFirstCoord().getyCoord());
    }

    @Override
    public void drawShape(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources) {
        top.drawShape(canvas, imageView, mutableBitmap, paint, resources);
        right.drawShape(canvas, imageView, mutableBitmap, paint, resources);
        bottom.drawShape(canvas, imageView, mutableBitmap, paint, resources);
        left.drawShape(canvas, imageView, mutableBitmap, paint, resources);
        imageView.setImageDrawable(new BitmapDrawable(resources, mutableBitmap));
    }

    @Override
    public void drawText(Canvas canvas, ImageView imageView, Bitmap mutableBitmap, Paint paint, Resources resources, String data) {
        paint.setTextSize(TEXT_SIZE);

        float midX = (top.getFirstCoord().getxCoord() + bottom.getFirstCoord().getxCoord()) / 2f;
        float midY = (top.getFirstCoord().getyCoord() + bottom.getFirstCoord().getyCoord()) / 2f;

        float minX = Math.min(top.getFirstCoord().getxCoord(),top.getSecondCoord().getxCoord());
        midX = (midX + minX) / 2.2f;
        float topMinY = Math.max(top.getFirstCoord().getyCoord(), top.getSecondCoord().getyCoord());
        midY = (midY + topMinY) / 2f;

        String[] dataArr = data.split(",");
        canvas.drawText("Width: " + dataArr[0] + " cm", midX, midY, paint);
        canvas.drawText("Height: " + dataArr[1] + " cm", midX, midY + TEXT_SIZE, paint);
        imageView.setImageDrawable(new BitmapDrawable(resources, mutableBitmap));
    }

    @Override
    /**
     * Order is Top,Right,Bottom,Left
     */
    public String toString() {
        return top.toString() + "," + bottom.toString();

    }
}
