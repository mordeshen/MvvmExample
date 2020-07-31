package com.e.mvvmexample.api;

import androidx.lifecycle.LiveData;

import com.e.mvvmexample.api.responses.ApiResponse;
import com.e.mvvmexample.api.responses.ItemResponse;
import com.e.mvvmexample.api.responses.ListResponse;

import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainApi {
    @GET("api/search")
    LiveData<ApiResponse<ListResponse>> searchList(
            @Query("key") String key,
            @Query("q") String query,
            @Query("page") String page
    );

    @GET("api/get")
    LiveData<ApiResponse<ItemResponse>> getItem(
            @Query("key") String key,
            @Query("rId") String id

    );
}
