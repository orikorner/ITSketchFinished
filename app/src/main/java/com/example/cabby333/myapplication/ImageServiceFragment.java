package com.example.cabby333.myapplication;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A Fragment class which is acts as a client. Upon creation, it first loads the Loading screen
 * and then creates an Http request with the image and sketches info and sends it to the server
 * for calculations and waits for the results in the response.
 */
public class ImageServiceFragment extends Fragment {

    private String TAG = "ImageServiceFragment";
    private String BITMAP_FILE_NAME = "bitmapFile.jpg";
    private byte[] mImgBytes;
    private String mSketchInfo;
    private int TIME_OUT = 3;
    OnImgResultsResponseListener mCallback;
    ImageView mImageView;
    AnimationDrawable mAnim;

    /**
     * A Singleton constructor for this fragment
     * @param imgBitmap The Image taken as Bitmap
     * @param sketchInfo The information about the different shape sketches
     * @return The fragment
     */
    public static ImageServiceFragment newInstance(Bitmap imgBitmap, String sketchInfo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteArray = stream.toByteArray();
//        imgBitmap.recycle();

        Bundle bundle = new Bundle();
        bundle.putString("sketchInfo", sketchInfo);
        bundle.putByteArray("imgByteArray", byteArray);

        ImageServiceFragment fragment = new ImageServiceFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated");

        mImageView = view.findViewById(R.id.anim_holder);
        mImageView.setBackgroundResource(R.drawable.animation_loading);

        mImageView.post(new Runnable() {
            @Override
            public void run() {
                mAnim = (AnimationDrawable)mImageView.getBackground();
                mAnim.start();
            }
        });


        Bundle args = getArguments();
        if (args != null) {
            // Info about the sketches (e.g shape types and coordinates)
            mSketchInfo = args.getString("sketchInfo", "");
            // The image taken in Bytes
            mImgBytes = args.getByteArray("imgByteArray");
            test();
//            makeRequest();
        } else {
            Log.i(TAG, "Did not receive arguments");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"Attached!");
        try {
            mCallback = (OnImgResultsResponseListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnImgResultsResponseListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
    }

    /**
     * Creates a http request with the Image and the Sketching information in it's body
     */
    private void makeRequest() {

        try {
            Log.i(TAG, "Making a Request");

            // Creating the client
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.MINUTES)
                    .writeTimeout(TIME_OUT, TimeUnit.MINUTES)
                    .readTimeout(TIME_OUT, TimeUnit.MINUTES)
                    .addInterceptor(interceptor)
                    .build();

            Log.i(TAG, "----1----");
            // Writing the image bytes to file
            File file = new File(getContext().getCacheDir(), BITMAP_FILE_NAME);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(mImgBytes);
            fos.flush();
            fos.close();

            Log.i(TAG, "----2----");

            String serverUrl = Service.SERVER_IP + ':' + Service.SERVER_PORT;

            // Creates the Wrapper Object for the client (this is the actual client from now on)
            Service clientService = new Retrofit.Builder()
                    .baseUrl(serverUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(Service.class);

            Log.i(TAG, "----3----");
            // Creates a request body where the content is the Image
            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            Log.i(TAG, "----4----");
            MultipartBody.Part reqImg = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            Log.i(TAG, "----5----");
            // Creates a request body where the content is the sketching information
            RequestBody reqSketchInfo = RequestBody.create(MediaType.parse("text/plain"), mSketchInfo);
            Log.i(TAG, "----6----");
            // Sending a POST request to server with the Image and Sketching info
            retrofit2.Call<ImageResponsePojo> call = clientService.postImage(reqImg, reqSketchInfo);
            Log.i(TAG, "----7----");
            call.enqueue(new Callback<ImageResponsePojo>() {
                @Override
                public void onResponse(Call<ImageResponsePojo> call, Response<ImageResponsePojo> response) {
                    if (response.isSuccessful())
                    {
                        Log.i(TAG, "Got a successful response");
                        Boolean hasCalib = response.body().hasCaliberator();
                        if (hasCalib) {
                            String numSketches = response.body().getCount();
                            String resultsData = response.body().getMeasuers();
                            JsonArray predictionsJsonArr = response.body().getPredictions();

                            Log.i(TAG, String.valueOf(hasCalib));
                            Log.i(TAG, "numSketches: " + numSketches);
                            Log.i(TAG, "resultsData: " + resultsData);
                            Log.i(TAG, "predictions: " + predictionsJsonArr.toString());

                            ArrayList<Map> predDataArr = parsePredictions(predictionsJsonArr);
                            ArrayList<Pair<String, String>> shapesDataArr = parseShapes(numSketches, resultsData);
                            mCallback.onImgResponse(shapesDataArr, predDataArr);
                        } else {
                            Log.e(TAG, "Caliberator Not Found");
                            Toast.makeText(getContext(), "Attempt failed: Make sure caliberator is placed within the image", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                        // TODO - might need backpressed manually
                    } else {
                        Log.e(TAG, "Got a response but it failed");
                        Toast.makeText(getContext(), "OnResponse: Attempt failed, please try again", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponsePojo> call, Throwable t) {
                    Log.e(TAG, "Response Failure");
                    Toast.makeText(getContext(), "OnFailure: Attempt failed, please try again", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                    t.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void test() {
        String numSketches = "4";
        String resultsData = "LINE,1;FREE_RECT,20,21;CIRCLE,30,31,32;RECT,40,41";
        ArrayList<Pair<String, String>> resDataArr = parseShapes(numSketches, resultsData);
        ArrayList<Map> predDataArr = new ArrayList<>();
        mCallback.onImgResponse(resDataArr, predDataArr);
    }

    private ArrayList<Pair<String, String>> parseShapes(String numSketches, String results) {
        // TODO regex check
        ArrayList<Pair<String, String>> resDataArr = new ArrayList<>();
        String[] resultsData = results.split(";");
        for (int i = 0; i < Integer.parseInt(numSketches); i++) {
            String shapeType = resultsData[i].substring(0,resultsData[i].indexOf(","));
            String shapeData = resultsData[i].substring(resultsData[i].indexOf(",") + 1);
            resDataArr.add(new Pair<>(shapeType, shapeData));
        }
        return resDataArr;
    }

    private ArrayList<Map> parsePredictions(JsonArray predictionJSArr) {
        ArrayList<Map> predDataArr = new ArrayList<>();
        Gson gson = new Gson();

        for (JsonElement prediction : predictionJSArr) {
            Map myMap = gson.fromJson(prediction, Map.class);
            predDataArr.add(myMap);
            Log.i(TAG, "label: " + myMap.get("label"));
            Log.i(TAG, myMap.get("topleft").toString());
            Log.i(TAG, myMap.get("bottomright").toString());

        }

        Toast.makeText(getActivity(), predictionJSArr.toString(),
                Toast.LENGTH_LONG).show(); // TODO remove later

        return predDataArr;
    }

    public interface OnImgResultsResponseListener {
        void onImgResponse(ArrayList<Pair<String, String>> shapesDataArr,
                           ArrayList<Map> predDataArr);
    }
}













