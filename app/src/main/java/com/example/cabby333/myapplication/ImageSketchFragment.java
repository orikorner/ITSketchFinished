package com.example.cabby333.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import java.util.ArrayList;

import static com.example.cabby333.myapplication.SketcherActivity.mFile;

public class ImageSketchFragment extends Fragment {

    private static final String TAG = "ImageSketchFragment";
    private static final int COLOR_WIDTH = 10;
    private static final int SHAPE_COLOR = Color.BLUE;
    private static final int PRED_COLOR = Color.RED;

    private static int clicksCount = 0;
    private static float x1Coord;
    private static float y1Coord;
    private static float x2Coord;
    private static float y2Coord;
    private static Shape currShape;
    private static ArrayList<Line> linesArr = new ArrayList<>();

    private ArrayList<Pair<SketchType, Shape>> sketchesArr = new ArrayList<>();

    public enum SketchType {
        LINE, FREE_RECT, RECT, CIRCLE
    }
    private static SketchType currShapeType;

    public ImageView mImageView;
    private Bitmap undoBitmap;
    private Bitmap mutableBitmap;
    public Canvas workCanvas;
    public Paint mPaint;
    public Paint mPredPaint;
    private AtomicBoolean isDone = new AtomicBoolean();
    private View mView;
    OnApplySketchListener mCallback;

    public static ImageSketchFragment newInstance() {
        return new ImageSketchFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_image_sketch, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");
        mImageView = view.findViewById(R.id.full_sketch);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        initParams(true);
        handleImage();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = (OnApplySketchListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnApplySketchListener");
        }
    }

    private void handleImage() {
        try {
            SketcherActivity.mSketchSem.acquire();
            openImage();
            registerButtons();
            SketcherActivity.mSketchSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void openImage() {
        Log.i(TAG,"opened image");
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inSampleSize = 0;

        mImageView.setImageBitmap(BitmapFactory.decodeFile(mFile.getAbsolutePath(), bounds));

    }

    @SuppressLint("ClickableViewAccessibility")
    private void registerButtons() {

        undoBitmap = ((BitmapDrawable)mImageView.getDrawable()).getBitmap();
        mutableBitmap = undoBitmap.copy(Bitmap.Config.ARGB_8888, true);
        workCanvas = new Canvas(mutableBitmap);

        mPaint = new Paint();
        mPaint.setColor(SHAPE_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(COLOR_WIDTH);

        mPredPaint = new Paint();
        mPredPaint.setColor(PRED_COLOR);
        mPredPaint.setStyle(Paint.Style.STROKE);
        mPredPaint.setStrokeWidth(COLOR_WIDTH);

        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                float xRatio = (float)undoBitmap.getWidth() / mImageView.getWidth();
                float yRatio = (float)undoBitmap.getHeight() / mImageView.getHeight();

                if (!isDone.get() && event.getAction() == MotionEvent.ACTION_UP) {
                    switch (currShapeType) {
                        case LINE:
                            if (clicksCount == 0) {
                                x1Coord = event.getX() * xRatio;
                                y1Coord = event.getY() * yRatio;
                                clicksCount = 1;
                            } else {
                                x2Coord = event.getX() * xRatio;
                                y2Coord = event.getY() * yRatio;

                                currShape = new Line(x1Coord, y1Coord, x2Coord, y2Coord);
                                currShape.drawShape(workCanvas, mImageView, mutableBitmap, mPaint, getResources());
                                mImageView.setImageDrawable(new BitmapDrawable(getResources(), mutableBitmap));
//                                clicksCount = 0;
                                appendShape();
                            }
                            break;
                        case FREE_RECT:
                            if (clicksCount == 0) {
                                x1Coord = event.getX() * xRatio;
                                y1Coord = event.getY() * yRatio;
                                clicksCount++;
                            } else if (clicksCount >= 1 && clicksCount <= 3) {
                                x2Coord = event.getX() * xRatio;
                                y2Coord = event.getY() * yRatio;
                                linesArr.add(new Line(x1Coord, y1Coord, x2Coord, y2Coord));
                                x1Coord = x2Coord;
                                y1Coord = y2Coord;
                                clicksCount++;
                            }

                            if (clicksCount == 4) {
                                linesArr.add(new Line(x1Coord, y1Coord, x2Coord, y2Coord));
                                currShape = new Rect(linesArr.get(0), linesArr.get(1), linesArr.get(2), linesArr.get(3));
                                currShape.drawShape(workCanvas, mImageView, mutableBitmap, mPaint, getResources());
//                                clicksCount = 0;
                                appendShape();
                            }

                            break;
                        case RECT:
                            if (clicksCount == 0) {
                                x1Coord = event.getX() * xRatio;
                                y1Coord = event.getY() * yRatio;
                                clicksCount = 1;
                            } else {
                                x2Coord = event.getX() * xRatio;
                                y2Coord = event.getY() * yRatio;

                                Line top = new Line(x1Coord, y1Coord, x2Coord, y1Coord);
                                Line right = new Line(x2Coord, y1Coord, x2Coord, y2Coord);
                                Line bottom = new Line(x2Coord, y2Coord, x1Coord, y2Coord);
                                Line left = new Line(x1Coord, y2Coord, x1Coord, y1Coord);

                                currShape = new Rect(top, right, bottom, left);
                                currShape.drawShape(workCanvas, mImageView, mutableBitmap, mPaint, getResources());
//                                clicksCount = 0;
                                appendShape();
                            }
                            break;
                        case CIRCLE:
                            if (clicksCount == 0) {
                                x1Coord = event.getX() * xRatio;
                                y1Coord = event.getY() * yRatio;
                                clicksCount = 1;
                            } else {
                                x2Coord = event.getX() * xRatio;
                                y2Coord = event.getY() * yRatio;

                                currShape = new Circle(x1Coord, y1Coord, x2Coord, y2Coord);
                                currShape.drawShape(workCanvas, mImageView, mutableBitmap, mPaint, getResources());
//                                clicksCount = 0;
                                appendShape();
                            }
                            break;
                        default:
                            Log.i(TAG, "Invalid sketch shape chosen");
                            break;
                    }
                }

                return true;
            }
        });

        ImageButton lineButton = mView.findViewById(R.id.line);
        lineButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currShapeType = SketchType.LINE;
                initParams(false);
                Log.i(TAG, "Line button Clicked");
            }
        });

        ImageButton freeRectButton = mView.findViewById(R.id.free_rect);
        freeRectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currShapeType = SketchType.FREE_RECT;
                initParams(false);
                Log.i(TAG, "Line button Clicked");
            }
        });

        ImageButton rectButton = mView.findViewById(R.id.rect);
        rectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currShapeType = SketchType.RECT;
                initParams(false);
                Log.i(TAG, "Rect button Clicked");
            }
        });

        ImageButton squareButton = mView.findViewById(R.id.circle);
        squareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currShapeType = SketchType.CIRCLE;
                initParams(false);
                Log.i(TAG, "Square button Clicked");
            }
        });

        Button okButton = mView.findViewById(R.id.save);
        okButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Ok button Clicked");
                try {
                    initImgService();

                } catch(Exception e){
                    e.printStackTrace();
                } finally {
                    initParams(false);
                    initImageAndCanvas();
                }
            }
        });

        Button clearButton = mView.findViewById(R.id.clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Clear button Clicked");
                initParams(true);
                initImageAndCanvas();
            }
        });
    }

    private void appendShape() {
        sketchesArr.add(new Pair<>(currShapeType, currShape));
        initParams(false); // TODO might not be needed
    }

    private void initImgService() {

        String shapesInfo = makeSketchInfo();
        // Send Image and Shapes information back to activity
        mCallback.onApplySketch(((BitmapDrawable)mImageView.getDrawable()).getBitmap(), sketchesArr, shapesInfo);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, ImageServiceFragment.newInstance(((BitmapDrawable)mImageView.getDrawable()).getBitmap(), shapesInfo))
//                .addToBackStack(null).commit();
    }

    private String makeSketchInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.valueOf(sketchesArr.size()));
        for (Pair pair : sketchesArr) {
            String currType = String.valueOf(pair.first);
            String currCoordsInfo = Objects.requireNonNull(pair.second).toString();
            builder.append(';');
            builder.append(currType);
            builder.append(",");
            builder.append(currCoordsInfo);
        }
        String shapesInfo = builder.toString();
        return shapesInfo;
    }

    private void initParams(Boolean doneSketching) {
        x1Coord = 0;
        y1Coord = 0;
        x2Coord = 0;
        y2Coord = 0;
        clicksCount = 0;
        isDone.set(false);
        linesArr.clear();
        if (doneSketching)
            sketchesArr.clear();
    }

    private void initImageAndCanvas() {
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), undoBitmap));
        mutableBitmap = undoBitmap.copy(Bitmap.Config.ARGB_8888, true);
        workCanvas = new Canvas(mutableBitmap);
    }

    public interface OnApplySketchListener {
        void onApplySketch(Bitmap imgViewBitmap, ArrayList<Pair<SketchType, Shape>> shapesArr, String shapesInfo);
    }
}
















