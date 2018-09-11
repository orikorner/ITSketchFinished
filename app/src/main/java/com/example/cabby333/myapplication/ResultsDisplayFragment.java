package com.example.cabby333.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.v7.widget.AppCompatImageButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class ResultsDisplayFragment extends Fragment {

    private static final String TAG = "ResultsDisplayFragment";
    private ImageView mImageView;
    private Activity mCallback;
    private Bitmap bm;
    private String mImgPath = "";
    private String mInfoText = "";

    public static ResultsDisplayFragment newInstance(@NonNull  Bitmap imgBitmap, Boolean inSketchMode, String imgPath) {
        Log.i(TAG, "newInstance");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteArray = stream.toByteArray();
//        imgBitmap.recycle();

        Bundle bundle = new Bundle();
        bundle.putByteArray("imgBitmap", byteArray);
        bundle.putBoolean("sketchMode", inSketchMode);
        bundle.putString("imgPath", imgPath);

        ResultsDisplayFragment fragment = new ResultsDisplayFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {

        Log.i(TAG, "onViewCreated");
        mImageView = view.findViewById(R.id.full_sketch2);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Boolean inSketchMode = false;
        Bundle args = getArguments();
        if (args != null) {
            byte[] imgBytes = args.getByteArray("imgBitmap");
            inSketchMode = args.getBoolean("sketchMode");
            mImgPath = args.getString("imgPath");
            if (imgBytes != null) {
                bm = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                mImageView.setImageBitmap(bm);
            }
        }

        Button cancelButton = view.findViewById(R.id.discard);
        Button saveButton = view.findViewById(R.id.save);
        Button addNotesButton = view.findViewById(R.id.add_notes);
        AppCompatImageButton showNotesButton = view.findViewById(R.id.show_notes);

        if (inSketchMode) {
            showNotesButton.setVisibility(View.GONE);
            ((OnDisplayResultsListener)mCallback).onDisplayResults(mImageView);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(TAG, "Discard button clicked");

                    Bitmap mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
                    mutableBitmap.eraseColor(android.graphics.Color.TRANSPARENT);
                    mImageView.setImageBitmap(mutableBitmap);
                    ((onDiscardResultsListener)mCallback).onDiscardResults();

                }
            });

            saveButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(TAG, "Save button clicked");
                    mImageView.buildDrawingCache();
                    Bitmap bm = mImageView.getDrawingCache();
                    ((onSaveResultsListener)mCallback).onSaveResults(bm);
                }
            });

            addNotesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(TAG, "Add Notes button clicked");

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Add Notes");

                    // Set up the input
                    final EditText input = new EditText(getContext());

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mInfoText = input.getText().toString();
                            Log.i(TAG, mInfoText);
                            ((onSaveImgInfoListener)mCallback).onSaveImgInfo(mInfoText);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });

        } else {

            cancelButton.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            addNotesButton.setVisibility(View.GONE);
            showNotesButton.setVisibility(View.VISIBLE);
            showNotesButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.i(TAG, "Show Notes button clicked");

                    byte[] data = "".getBytes();
                    try {
                        File imgInfoFile = new File(mImgPath);
                        Log.i(TAG, "Path is: " + imgInfoFile.getAbsolutePath());
                        FileInputStream fis = new FileInputStream(imgInfoFile);
                        data = new byte[(int) imgInfoFile.length()];
                        fis.read(data);
                        fis.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String imgInfoText = new String(data);
                    Log.i(TAG, "got: " + imgInfoText);

                    new AlertDialog.Builder(mCallback)
                            .setMessage(imgInfoText)
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            });
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnDisplayResultsListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    public interface OnDisplayResultsListener {
        void onDisplayResults(ImageView fragImgView);
    }

    public interface onDiscardResultsListener {
        void onDiscardResults();
    }

    public interface onSaveResultsListener {
        void onSaveResults(Bitmap imgBitmap);
    }

    public interface onSaveImgInfoListener {
        void onSaveImgInfo(String imgInfo);
    }

}
