package com.e.mvvmexample.api.responses;

import androidx.annotation.Nullable;

import com.e.mvvmexample.models.MainModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemResponse {
    @SerializedName("recipe")
    @Expose()
    private MainModel model;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    @Nullable
    public MainModel getModel(){
        return model;
    }

    @Override
    public String toString() {
        return "ItemResponse{" +
                "model=" + model +
                ", error='" + error + '\'' +
                '}';
    }
}
