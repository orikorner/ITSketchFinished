package com.example.cabby333.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Caliberator extends Fragment {

    private String TAG = "Caliberator";
    private EditText mEditTextMail;

    public static Caliberator newInstance() {
        return new Caliberator();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_caliberator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        mEditTextMail = view.findViewById(R.id.editText);

        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i(TAG, "Send button clicked");
                makeCalibRequest(mEditTextMail.getText().toString());
            }
        });

        Button returnButton = view.findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i(TAG, "Return button clicked");
                getActivity().onBackPressed();
            }
        });
    }

    /**
     * Creates a http request with the Image and the Sketching information in it's body
     */
    private void makeCalibRequest(String email) {

        Log.i(TAG, "Making a Request: " + email);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


            String serverUrl = Service.SERVER_IP + ':' + Service.SERVER_PORT;
            Service clientService = new Retrofit.Builder().baseUrl(serverUrl).client(client).addConverterFactory(GsonConverterFactory.create()).build().create(Service.class);

            // Sending a GET request to server with the Email info
            retrofit2.Call<String> call = clientService.getCaliberator(email);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.i(TAG, "Got a response");
                    if (response.isSuccessful())
                    {
                        getActivity().onBackPressed();
                        // TODO make this a interface communication
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Response Failure");
                    t.printStackTrace();
                    getActivity().onBackPressed();
                }
            });
    }

}
