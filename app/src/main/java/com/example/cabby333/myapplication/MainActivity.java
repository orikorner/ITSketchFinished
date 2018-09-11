package com.example.cabby333.myapplication;


import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity implements NewPortfolioFragment.OnNewItemListener {

    private static final String TAG = "MainActivity";

    private static final String MAIN_TO_NEW_TAG = "main2newport";
    private static final String MAIN_TO_CALIB_TAG = "main2calib";

    public static final String KEY_HOUSES = "Houses";

    private Thread workerThread;

    private Semaphore filesOpeningSem = new Semaphore(1);

    private ArrayList<String> mFileNameArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workerThread = new Thread(new ThreadWorker());
        workerThread.start();

        ImageView sendCalibImg = findViewById(R.id.get_caliberator);
        sendCalibImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Get Caliberator Button Clicked");

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_main, Caliberator.newInstance())
                        .addToBackStack(MAIN_TO_CALIB_TAG)
                        .commit();

            }
        });

        ImageView myHousesImg = findViewById(R.id.my_houses);
        myHousesImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Existing Portfolio Button Clicked");

                Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);

                Bundle b = new Bundle();
                b.putStringArrayList(KEY_HOUSES, mFileNameArr);
                intent.putExtras(b);
                try {
                    workerThread.join();
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });

        ImageView newHouseImg = findViewById(R.id.new_house);
        newHouseImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "New Portfolio Button Clicked");

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container_main, NewPortfolioFragment.newInstance(null))
                        .addToBackStack(MAIN_TO_NEW_TAG)
                        .commit();

            }
        });
    }


    private ArrayList<String> loadDirNames() {
        Log.i(TAG, "loadExistingDirectories");

        ArrayList<String> fileNameArr = new ArrayList<>();
        File mainDir = getExternalFilesDir(null);
        if (mainDir != null && mainDir.exists()) {
            File[] files = mainDir.listFiles();
            for (File file : files) {
                if (file.isDirectory())
                    fileNameArr.add(file.getPath());
            }
        }

        return fileNameArr;
    }

    private void createNewDirectory(String name) {
        Log.i(TAG, "createNewDirectory");

        File house = new File(getExternalFilesDir(null), name);
        if (house.exists()) {
            Log.i(TAG, "house already exists");
            deleteDirectory(house);
        }
        house.mkdirs(); // TODO if exists?
        mFileNameArr.add(house.getPath());
    }

    private boolean deleteDirectory(File fileToDel) {
        Log.i(TAG, "deleteDirectory");
        if (fileToDel.exists()) {
            File[] files = fileToDel.listFiles();
            if (files == null) {
                return true;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    Log.i(TAG, "deleteFile");
                    file.delete();
                }
            }
        }
        return (fileToDel.delete());
    }

    private void deleteAllDirectorys() {
        File mainDir = getExternalFilesDir(null);
        if (mainDir != null && mainDir.exists()) {
            for (File file : mainDir.listFiles()) {
                if (file.isDirectory())
                    deleteDirectory(file);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "BackPressed");
        super.onBackPressed();
    }

    @Override
    public void onApplyNewItem(String name, PortfolioActivity.ROOM_TYPES roomTypes) {
        Log.i(TAG, "New portfolio callback with name: " + name);

        getSupportFragmentManager().popBackStack(MAIN_TO_NEW_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Creating a directory of the new Portfolio with the given name
        createNewDirectory(name);

        // loading the rest of the directories along with the new one
        mFileNameArr.add(name);

        Intent intent = new Intent(MainActivity.this, PortfolioActivity.class);
        Bundle b = new Bundle();
        b.putStringArrayList(KEY_HOUSES, mFileNameArr);
        intent.putExtras(b);
        startActivity(intent);
    }

    public class ThreadWorker implements Runnable {

        private String TAG = "WorkerThread";

        @Override
        public void run() {
            Log.i(TAG, "run");
            try {
                filesOpeningSem.acquire();
                mFileNameArr = loadDirNames();
                Log.i(TAG, "Done loading dirs");
                filesOpeningSem.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "WorkerDied");
        }

    }

}