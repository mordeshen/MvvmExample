package com.e.mvvmexample.api.responses;

import androidx.annotation.Nullable;

import com.e.mvvmexample.models.MainModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListResponse {
    @SerializedName("count")
    @Expose()
    private int count;

    @SerializedName("recipes")
    @Expose()
    private List<MainModel> models;

    @SerializedName("error")
    @Expose()
    private String error;

    public String getError() {
        return error;
    }

    public int getCount() {
        return count;
    }

    @Nullable
    public List<MainModel> getModels() {
        return models;
    }

    @Override
    public String toString() {
        return "RecipeSearchResponse{" +
                "count=" + count +
                ", models=" + models +
                ", error='" + error + '\'' +
                '}';
    }
}
