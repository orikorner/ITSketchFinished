package com.example.cabby333.myapplication;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResponsePojo {

    @SerializedName("status")
    @Expose
    private Boolean foundCaliberator;
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("predictions")
    @Expose
    private JsonArray predictions;
    @SerializedName("measures")
    @Expose
    private String measuers;

    public String getCount() {
        return count;
    }

    public Boolean hasCaliberator() {
        return foundCaliberator;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getMeasuers() {
        return measuers;
    }

    public JsonArray getPredictions() {
        return predictions;
    }

    public void setMeasuers(String measuers) {
        this.measuers = measuers;
    }

}
