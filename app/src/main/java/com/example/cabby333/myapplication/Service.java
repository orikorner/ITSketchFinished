package com.example.cabby333.myapplication;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    String SERVER_IP = "http://54.201.103.222";
    String SERVER_PORT = "8080";

    @Multipart
    @POST("/getMeasures")
    Call<ImageResponsePojo> postImage(@Part MultipartBody.Part image, @Part("info") RequestBody info);

    @GET("/getCalibrator")
    Call<String> getCaliberator(@Query("email") String mail);
}
