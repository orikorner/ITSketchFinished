package com.example.cabby333.myapplication;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PortfolioActivity extends AppCompatActivity
        implements MessageAdapter.OnClickListener, NewPortfolioFragment.OnNewItemListener,
        ItemListFragment.OnAppendItemListener, ImgAdapter.ItemClickListener,
        MessageAdapter.OnDeletedItemDirListener, PhotoCollageFragment.OnTakeImageListener,
        ResultsDisplayFragment.onDiscardResultsListener, ResultsDisplayFragment.onSaveResultsListener,
        ResultsDisplayFragment.OnDisplayResultsListener, ResultsDisplayFragment.onSaveImgInfoListener,
        ImageServiceFragment.OnImgResultsResponseListener, ImageSketchFragment.OnApplySketchListener,
        SketcherActivity.onImgSketchListener {

    private static final String TAG = "PortfolioActivity";
    private static final String PROP_ROOMS_TAG = "prop2rooms";
    private static final String ROOMS_PHOTOS_TAG = "rooms2photos";
    private static final String PROP_NEW_PORT_TAG = "prop2newport";
    private static final String ROOMS_NEW_PORT_TAG = "rooms2newport";
    private static final String PHOTOS_NEW_PHOTO_TAG = "photos2newphoto";
    private static final String PHOTO_SKETCH_TAG = "photo2sketch";
    private static final String PHOTO_DISPLAY_TAG = "photo2display";
    private static final String SKETCH_SERVICE_TAG = "sketch2service";
    private static final String SERVICE_DISPLAY_TAG = "service2display";

    private static final String ROOM_TYPE_DELIMETER = "_";

    private static final String ID_REPLACE = "idReplace";

    public static final String KEY_HOUSES = "Houses";
    public static final String PROPERTIES_TITLE = "Houses Navigator";
    public static final String CURR_HOUSE = "CurrHouse";
    public static final String CURR_ROOM = "CurrRoom";
    public static final String FROM_TAKE_IMG = "FromTakeImage";
    public static final String COLLAGE_IMG_COUNT = "CollageImgCount";

    public enum STATES {
        ESTATES, ROOMS, PHOTOS
    }

    public enum ROOM_TYPES {
        HOUSE, KITCHEN, BATHROOM, DEFAULT, BEDROOM, LIVINGROOM
    }

    private Boolean inNewPort = false;
    private Boolean inDisplayImg = false;
    private Boolean inCanvas = false;
    private Boolean inTakeImg = false;
    private String mCurrRoom;
    private String mCurrRoomType;
    private String mCurrHouse;
    public STATES mCurrState;
    public ArrayList<String> mPropertiesArr = new ArrayList<>();
    public ArrayList<String> mImgNamesArr = new ArrayList<>();

    public Map<String, ArrayList<Pair<String, String>>> mPropRoomsMap = new HashMap<>();
    public Map<String, ArrayList<String>> mRoomImgsMap = new HashMap<>();

    private ItemListFragment mPropertiesFrag;
    private ItemListFragment mRoomsFrag;
    private PhotoCollageFragment mPhotoCollageFrag;
    private SketcherActivity mSketcherFrag;
    private ImageSketchFragment mImgSketchFrag;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        Log.i(TAG, "OnCreate");
        setTitle(PROPERTIES_TITLE);
        Bundle bundle = getIntent().getExtras();
        ArrayList<String> fileNameArr = null;
        if(bundle != null)
            fileNameArr = bundle.getStringArrayList(KEY_HOUSES);

        mCurrState = STATES.ESTATES;

        initNewItemListFrag(fileNameArr);
    }

    private void initNewItemListFrag(ArrayList<String> filePathsArr) {

        if (filePathsArr != null) {
            for (String fPath : filePathsArr) {

                ArrayList<Pair<String, String>> currRoomNames = new ArrayList<>();

                File currHouseDir = new File(fPath);
                if( currHouseDir.exists() ) {
                    String currHouseName = currHouseDir.getName();
                    mPropertiesArr.add(currHouseName);

                    File[] roomDirs = currHouseDir.listFiles();
                    if (roomDirs != null) {
                        for (File roomDir : roomDirs) {
                            if (roomDir.isDirectory()) {
                                String currRoomName = roomDir.getName();

                                int idx = currRoomName.indexOf(ROOM_TYPE_DELIMETER);

                                currRoomNames.add(new Pair<String, String>(currRoomName.substring(0, idx), currRoomName.substring(idx + 1)));
                                ArrayList<String> currImgNames = new ArrayList<>();

                                File[] imgFiles = roomDir.listFiles();
                                if (imgFiles != null) {
                                    for (File imgFile : imgFiles) {
                                        if (imgFile.isFile()) {
                                            String currImgName = imgFile.getName();
                                            currImgNames.add(currImgName);
                                        }
                                    }
                                }
                                mRoomImgsMap.put(currHouseName + "." + currRoomName, currImgNames);
                            }
                        }
                    }
                    mPropRoomsMap.put(currHouseName, currRoomNames);
                }
            }
        }

        mPropertiesFrag = ItemListFragment.newInstance(STATES.ESTATES, null, null);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_frame_container, mPropertiesFrag)
                .addToBackStack(null)
                .commit();
    }


    private void addItemToCurrArr(String name, ROOM_TYPES roomType) {
        switch (mCurrState) {
            case ESTATES:
                mPropertiesArr.add(name);
                mPropRoomsMap.put(name, new ArrayList<Pair<String, String>>());
                break;
            case ROOMS:
                mPropRoomsMap.get(mCurrHouse).add(new Pair<String, String>(name, roomType.toString()));
                break;
            case PHOTOS:
                mImgNamesArr.add(name);
                break;
        }
    }

    private void removeItemFromCurrArr(String name) {
        switch (mCurrState) {
            case ESTATES:
                mPropertiesArr.remove(name);
                mPropRoomsMap.remove(name);
                break;
            case ROOMS:
                int j = 0;

                ArrayList<Pair<String, String>> tmp = mPropRoomsMap.get(mCurrHouse);

                for (int i = 0; i < tmp.size(); i++) {
                    if (Objects.requireNonNull(tmp.get(i).first).equals(name)) {
                        j = i;
                    }
                }
                mPropRoomsMap.get(mCurrHouse).remove(j);
                break;
            case PHOTOS:
                mImgNamesArr.remove(name);
                break;
        }
    }

    private Fragment getCurrFragment() {
        Fragment currFrag = null;
        switch (mCurrState) {
            case ESTATES:
                currFrag = mPropertiesFrag;
                break;
            case ROOMS:
                currFrag = mRoomsFrag;
                break;
            case PHOTOS:
                currFrag = mPhotoCollageFrag;
                break;
        }
        return currFrag;
    }

    public ArrayList<String> getCurrItemsArr(String name) {
        ArrayList<String> currItemsArr = null;
        switch (mCurrState) {
            case ESTATES:
                currItemsArr = mPropertiesArr;
                break;
//            case ROOMS:
//                currItemsArr = mPropRoomsMap.get(name);
//                break;
            case PHOTOS:
                currItemsArr = mImgNamesArr;
                break;
        }
        return currItemsArr;
    }

    public ArrayList<Pair<String, String>> getmRoomsArr(String name) {
        return mPropRoomsMap.get(name);
    }


    private String getCurrPath() {
        StringBuilder builder = new StringBuilder();
        builder.append(getExternalFilesDir(null).getPath());
        if (mCurrState == STATES.ROOMS) {
            builder.append("/").append(mCurrHouse);
        } else if (mCurrState == STATES.PHOTOS) {
            builder.append("/").append(mCurrHouse).append("/").append(mCurrRoom).append(ROOM_TYPE_DELIMETER).append(mCurrRoomType);
        }

        return  builder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (inTakeImg) {
            Log.i(TAG, "onBackPressed - Back from Image taker");
            setTitle("Room: " + mCurrRoom);
            getSupportFragmentManager().popBackStack(ROOMS_PHOTOS_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TODO wasnt here on working version
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_frame_container, mPhotoCollageFrag)
                    .addToBackStack(ROOMS_PHOTOS_TAG)
                    .commit();

            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mSketcherFrag)
                    .commit();
            inTakeImg = false;

        } else if (inCanvas) {
            Log.i(TAG, "onBackPressed - Back from Sketcher");

            setTitle("Take A Photo");

            getSupportFragmentManager().popBackStack(PHOTO_SKETCH_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_frame_container, mSketcherFrag)
                    .addToBackStack(null)
                    .commit();

            inCanvas = false;
            inTakeImg = true;

        } else if (inNewPort || inDisplayImg) {
            Log.i(TAG, "onBackPressed - Back from NewPort");

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_frame_container, getCurrFragment())
                    .addToBackStack(null)
                    .commit();

            inNewPort = false;
            inDisplayImg = false;

        } else if (mCurrState == STATES.ROOMS) {
            Log.i(TAG, "onBackPressed - in ROOMS");

            setTitle(PROPERTIES_TITLE);
            mCurrState = STATES.ESTATES;
            mCurrHouse = "";

            getSupportFragmentManager().popBackStack(PROP_ROOMS_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TODO wasnt here on working version

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_frame_container, mPropertiesFrag)
                    .addToBackStack(null)
                    .commit();

            ((ItemListFragment)getCurrFragment()).loadItems();

        } else if (mCurrState == STATES.PHOTOS) {
            Log.i(TAG, "onBackPressed - in PHOTOS");
            setTitle("Room: " + mCurrHouse);
            mCurrRoom = "";
            mCurrState = STATES.ROOMS;

            getSupportFragmentManager().popBackStack(PROP_ROOMS_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE); // TODO wasnt here on working version
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_frame_container, mRoomsFrag)
                    .addToBackStack(PROP_ROOMS_TAG)
                    .commit();

//            ((ItemListFragment)getCurrFragment()).loadItems();

        } else {
            // curr state is ESTATES
            Log.i(TAG, "onBackPressed - in ESTATES");
            getSupportFragmentManager().popBackStack();
            finish();
        }
    }

    /**
     * Called after clicking the append new item when in the list view screen.
     * the NewPortfolioFragment (e.g new house or new room) fragment is created here.
     */
    @Override
    public void onAppendNewItem() {
        Log.i(TAG, "onAppendNewItem");

        inNewPort = true;

        String transactionTag = "";
        if (mCurrState == STATES.ESTATES)
            transactionTag = PROP_NEW_PORT_TAG;
        else // means that mCurrState equals STATES.ROOMS
            transactionTag = ROOMS_NEW_PORT_TAG;

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_frame_container, NewPortfolioFragment.newInstance(mCurrState))
                .addToBackStack(transactionTag)
                .commit();

    }

    /**
     * Called after creating new item from NewPortfolioFragment (e.g new house or new room)
     * @param name of the new item
     */
    @Override
    public void onApplyNewItem(String name, ROOM_TYPES roomType) {
        Log.i(TAG, "onApplyNewItem");

        inNewPort = false;

        String transactionTag = "";
        String fullName = "";
        if (mCurrState == STATES.ESTATES) {
            transactionTag = PROP_NEW_PORT_TAG;
            fullName = name;
        }
        else {
            // means that mCurrState equals STATES.ROOMS
            transactionTag = ROOMS_NEW_PORT_TAG;
            fullName = name + ROOM_TYPE_DELIMETER + roomType.toString();
        }

        ItemListFragment currFrag = (ItemListFragment)getCurrFragment();

        addItemToCurrArr(name, roomType);
        currFrag.addItem(name, roomType);
        createNewDirectory(fullName);

        getSupportFragmentManager().popBackStack(transactionTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_frame_container, currFrag)
                .commit();
    }

    /**
     * Called when clicking on one of the item in Rooms or Home list.
     * @param state
     * @param message
     */
    @Override
    public void onClick(STATES state, MessagePojo message) {
        Log.i(TAG, "OnClick: " + message.getName());

        String itemNameClicked = message.getName();

        String transactionTag = "";

        if (state == STATES.ESTATES) {
            setTitle("House: " + itemNameClicked);
            mCurrState = STATES.ROOMS;
            mCurrHouse = itemNameClicked;
            mRoomsFrag = ItemListFragment.newInstance(mCurrState, message, getCurrPath());
            transactionTag = PROP_NEW_PORT_TAG;
        } else if (state == STATES.ROOMS) {
            setTitle("Room: " + itemNameClicked);
            mCurrState = STATES.PHOTOS;
            mCurrRoom = itemNameClicked;
            mCurrRoomType = message.getRoomType().toString();
            mPhotoCollageFrag = PhotoCollageFragment.newInstance(getCurrPath());
            transactionTag = ROOMS_NEW_PORT_TAG;
        }

        // Creating new fragment according to clicked selection
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_frame_container, getCurrFragment())
                .addToBackStack(transactionTag)
                .commit();
    }

    /**
     * Called from image collage view. In order to take a new image that current house and room
     * information is needed to know where to save the new image. Also item count is required to add
     * the new image to the adapter
     */
    @Override
    public void onTakeImage() {
        Log.i(TAG, "onTakeImage");
        setTitle("Take A Photo");
        mSketcherFrag = SketcherActivity.newInstance(mCurrHouse, mCurrRoom, mPhotoCollageFrag.mAdapter.getItemCount());
        inTakeImg = true;

        // Popping everything up to transition from rooms to collage
        getSupportFragmentManager().popBackStack(ROOMS_PHOTOS_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_frame_container, mSketcherFrag)
                .addToBackStack(PHOTOS_NEW_PHOTO_TAG)
                .commit();

    }

    /**
     * Called after "picture" button from the SketcherActivity was pressed
     * (when camera is open and we are taking the image).
     * @throws InterruptedException
     */
    @Override
    public void sketchOnImage() throws InterruptedException {
        Log.i(TAG, "sketchOnImage");
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // Tried new Handler(Looper.myLopper()) also
            @Override
            public void run() {

                mImgSketchFrag = ImageSketchFragment.newInstance();

                getSupportFragmentManager().popBackStack(PHOTO_SKETCH_TAG, 0);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.activity_frame_container, mImgSketchFrag, ID_REPLACE)
                        .addToBackStack(PHOTO_SKETCH_TAG)
                        .commit();

                inTakeImg = false;
                inCanvas = true;
                setTitle("Sketch On Your Photo");
            }
        }, 500);
    }

    /**
     * Called from image collage view. When clicking on one of the images in the collage this
     * callback is called and it creates the fragment which displays the image over the whole screen
     * @param view current view
     * @param position the index of the image clicked in the adapter
     */
    @Override
    public void onItemClick(View view, int position) {
        Log.i(TAG, "onItemClick - img position is: " + String.valueOf(position));

        String imgPath = getCurrPath() + "/" + String.valueOf(position) + "info.txt";
        inDisplayImg = true;
        Bitmap selectedImg = mPhotoCollageFrag.mAdapter.getItem(position);

        getSupportFragmentManager().popBackStack(PHOTO_DISPLAY_TAG, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_frame_container, ResultsDisplayFragment.newInstance(selectedImg, false, imgPath), ID_REPLACE)
                .addToBackStack(PHOTO_DISPLAY_TAG)
                .commit();
    }

    /**
     * Adds the shapes information measured into the image that was previously taken
     * @param fragImgView the ImageView holder where the image taken was inserted
     */
    @Override
    public void onDisplayResults(ImageView fragImgView) {
        Log.i(TAG, "onDisplayResults");
        mSketcherFrag.displayResults(fragImgView, getResources());
    }

    /**
     * Callback called from ImageServiceFragment after a response was received from server
     * @param shapesDataArr Information about the shapes (length, area etc..)
     * @param predDataArr Information about the predictions (Electricity, Caliberator)
     */
    @Override
    public void onImgResponse(ArrayList<Pair<String, String>> shapesDataArr, ArrayList<Map> predDataArr) {
        Log.i(TAG, "onImgResponse");
        mSketcherFrag.mShapeInfoArr = shapesDataArr;
        mSketcherFrag.mPredInfoArr = predDataArr;

        getSupportFragmentManager().popBackStack(PHOTOS_NEW_PHOTO_TAG, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_frame_container, ResultsDisplayFragment.newInstance(mSketcherFrag.mCurrBitmap, true, ""), ID_REPLACE)
                .addToBackStack(SERVICE_DISPLAY_TAG)
                .commit();
    }

    @Override
    public void onApplySketch(Bitmap imgViewBitmap, ArrayList<Pair<ImageSketchFragment.SketchType, Shape>> shapesArr, String shapesInfo) {
        Log.i(TAG, "onApplySketch");

        Log.i(TAG, "Shapes info is: " + shapesInfo);
        setTitle("");

        mSketcherFrag.mShapesArr = shapesArr;
        mSketcherFrag.mCurrBitmap = imgViewBitmap;
        mSketcherFrag.mCurrCanvas = mImgSketchFrag.workCanvas;
        mSketcherFrag.mShapePaint = mImgSketchFrag.mPaint;
        mSketcherFrag.mPredPaint = mImgSketchFrag.mPredPaint;

        getSupportFragmentManager().popBackStack(PHOTOS_NEW_PHOTO_TAG, 0);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_frame_container, ImageServiceFragment.newInstance(imgViewBitmap, shapesInfo), ID_REPLACE)
                .addToBackStack(SKETCH_SERVICE_TAG)
                .commit();
    }


    @Override
    public void onDiscardResults() {
        Log.i(TAG, "onDiscardResults");

        inTakeImg = true;
        inCanvas = false;
        inNewPort = false;
        inDisplayImg = false;

        getSupportFragmentManager().popBackStack(PHOTOS_NEW_PHOTO_TAG, 0);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_frame_container, mSketcherFrag, ID_REPLACE)
                .addToBackStack(PHOTOS_NEW_PHOTO_TAG)
                .commit();
    }

    @Override
    public void onSaveResults(Bitmap imgBitmap) {
        Log.i(TAG, "onSaveResults");
        FileOutputStream fOut;
        try {

            String newImgName = String.valueOf(mPhotoCollageFrag.mAdapter.getItemCount()) + ".jpg";
            File test = new File(getCurrPath(), newImgName);
            Log.e(TAG, test.getPath());
            fOut = new FileOutputStream(test);
            imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            Log.i(TAG, "-----Image was saved successfully-----");
        } catch (IOException e) {
            e.printStackTrace();
        }
        inTakeImg = false;
        inCanvas = false;
        inNewPort = false;
        inDisplayImg = false;

        getSupportFragmentManager().popBackStack(PHOTOS_NEW_PHOTO_TAG, 0);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_frame_container, mPhotoCollageFrag)
                .addToBackStack(PHOTOS_NEW_PHOTO_TAG)
                .commit();
    }

    @Override
    public void onSaveImgInfo(String imgInfo) {
        Log.i(TAG, "onSaveResults");
        FileOutputStream fOut;
        try {

            String newImgName = String.valueOf(mPhotoCollageFrag.mAdapter.getItemCount()) + "info.txt";
            File imgInfoFile = new File(getCurrPath(), newImgName);
            Log.e(TAG, imgInfoFile.getPath());
            fOut = new FileOutputStream(imgInfoFile); // TODo if exists remove

            Log.e(TAG, "Writing info: " + imgInfo);
            fOut.write(imgInfo.getBytes(Charset.forName("UTF-8")));

            fOut.flush();
            fOut.close();
            Log.i(TAG, "-----Image Info File was saved successfully-----");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewDirectory(String name) {
        Log.i(TAG, "createNewDirectory: + " + getCurrPath());
        // TODO regex check
        File newFile = new File(getCurrPath(), name);
        if (!newFile.exists()) {
            newFile.mkdirs();
            Log.i(TAG, "New House Directory created in: " + newFile.getPath());
        }
    }

    @Override
    public void onDeleteItemDirByName(String name, String fileName) {
        Log.i(TAG, "onDeleteItemDir - Name: " + fileName);
        File fileToDel = new File(getCurrPath(), fileName);
        if( fileToDel.exists() ) {
            File[] files = fileToDel.listFiles();
            if (files == null) {
                return;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    Boolean b = deleteDirectoryRecursiveByFile(file);
                } else {
                    Log.i(TAG, "File was deleted");
                    file.delete();
                }
            }
            removeItemFromCurrArr(name);
            fileToDel.delete();
        }
    }

    public void onDeleteItemFileByName(int imgIndex) {
        Log.i(TAG, "onDeleteItemFile - Name: " + imgIndex);
        String imgFileName = String.valueOf(imgIndex);
        File fileToDel = new File(getCurrPath(), imgFileName + ".jpg");
        if( fileToDel.exists() && !fileToDel.isDirectory()) {
            if (fileToDel.delete()) {
                Log.i(TAG, "File was deleted");
                removeItemFromCurrArr(imgFileName);
                File infoFile = new File(getCurrPath(), imgFileName + "info.txt");
                if (infoFile.exists())
                    infoFile.delete();
            }
        }

        File currDir = new File(getCurrPath());
        if (currDir.exists() && currDir.isDirectory()) {
            File[] files = currDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jpg");
                }
            });

            for (int i = imgIndex; i < files.length; i++) {
                File newNameFile = new File(getCurrPath(), String.valueOf(i) + ".jpg");
                files[i].renameTo(newNameFile);

                File infoFile = new File(getCurrPath(), String.valueOf(i + 1) + "info.txt");
                if (infoFile.exists())
                {
                    File newNameInfoFile = new File(getCurrPath(), String.valueOf(i) + "info.txt");
                    infoFile.renameTo(newNameInfoFile);
                }
            }
        }

    }


    private boolean deleteDirectoryRecursiveByFile(File fileToDel) {
        if( fileToDel.exists() ) {
            File[] files = fileToDel.listFiles();
            if (files == null) {
                return true;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursiveByFile(file);
                } else {
                    Log.i(TAG, "deleteFile");
                    file.delete();
                }
            }
        }
        return( fileToDel.delete() );
    }

}