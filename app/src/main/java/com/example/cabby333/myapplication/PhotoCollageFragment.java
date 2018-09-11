package com.example.cabby333.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class PhotoCollageFragment extends Fragment {

    private static final String TAG = "PhotoCollageFragment";
    public static final String IMGS_PATH = "imgsPath";
    public static final int numberOfColumns = 3;

    private OnTakeImageListener mCallback;
    private PortfolioActivity mCurrActivity;
    private RecyclerView mGridList;
    public ImgAdapter mAdapter;
    private ArrayList<Bitmap> mImgList;
    private View mView;

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        Log.i(TAG, "onSaveInstanceState");
//        ArrayList<Bitmap> imgBitmaps = new ArrayList<>(mAdapter.imgList.size());
//        for (Bitmap msg : mAdapter.imgList) {
//            imgBitmaps.add(msg);
//        }
//        outState.putStringArrayList(MESSAGES, imgBitmaps);
//
//
//        super.onSaveInstanceState(outState);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_collage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "onViewCreated");

        mView = view;

        mImgList = getImages(savedInstanceState);

        mGridList = view.findViewById(R.id.imgsGrid);
        mGridList.setLayoutManager(new GridLayoutManager(mCurrActivity, numberOfColumns));

        mAdapter = new ImgAdapter(mCurrActivity, mImgList);
        mAdapter.setCallbackListener(mCurrActivity);
        mGridList.setAdapter(mAdapter);

        Bundle args = getArguments();
        if (args != null) {
            String imgsPath = args.getString(IMGS_PATH);
            loadImgs(imgsPath);
        }

        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_create_new_item);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "New item clicked");

                // TODO add new Image after sketched !
                mCallback.onTakeImage();

            }
        });
    }

    private void loadImgs(String imgsPath) {
        Log.i(TAG, "loadImgs");

        File imgsDir = new File(imgsPath);
        if (imgsDir.exists()) {
            File[] imgFiles = imgsDir.listFiles();
            for (File imgFile : imgFiles) {
                if (!imgFile.getName().endsWith(".txt")) {
                    Log.e(TAG, imgFile.getName());
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    mImgList.add(bitmap);
                    Log.e(TAG, "-----IMG ADDED-----");
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void removeItem(int id) {
        mImgList.remove(id);
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = (OnTakeImageListener) getActivity();
            mCurrActivity = (PortfolioActivity) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnClickListener");
        }
    }

    public static PhotoCollageFragment newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(IMGS_PATH, path);
        PhotoCollageFragment fragment = new PhotoCollageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private ArrayList<Bitmap> getImages(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "getImages");
        if (savedInstanceState == null) {
            Log.e(TAG, "null1");
            return new ArrayList<>();
        }
        if (savedInstanceState.getString(IMGS_PATH) == null) {
            Log.e(TAG, "null2");
            return new ArrayList<>();
        }
        ArrayList<Bitmap> imgBitmapArr = new ArrayList<>();
        String imgsDirPath = savedInstanceState.getString(IMGS_PATH);
        Log.e(TAG, imgsDirPath);
        File imgsDir = new File(imgsDirPath);
        if (imgsDir.exists()) {
            File[] imgFiles = imgsDir.listFiles();
            for (File imgFile : imgFiles) {
                if (!imgFile.getName().endsWith(".jpg")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imgBitmapArr.add(bitmap);
                    Log.e(TAG, "-----IMG ADDED-----");
                }
            }
        }

        return imgBitmapArr;
    }


    public interface OnTakeImageListener {
        void onTakeImage();
    }


}
