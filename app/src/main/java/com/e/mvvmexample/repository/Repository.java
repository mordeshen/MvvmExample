package com.e.mvvmexample.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.e.mvvmexample.AppExecutors;
import com.e.mvvmexample.api.ServiceGenerator;
import com.e.mvvmexample.api.responses.ApiResponse;
import com.e.mvvmexample.api.responses.ItemResponse;
import com.e.mvvmexample.api.responses.ListResponse;
import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.persistance.MainDao;
import com.e.mvvmexample.persistance.MainDataBase;
import com.e.mvvmexample.util.Constants;
import com.e.mvvmexample.util.NetworkBoundResource;
import com.e.mvvmexample.util.Resource;

import java.util.List;

public class Repository {
    private static final String TAG = "Repository";

    private static Repository instatnce;
    private MainDao mainDao;

    public static Repository getInstance(Context context){
        if (instatnce == null){
            instatnce = new Repository(context);
        }
        return instatnce;
    }

    private Repository(Context context){
        mainDao = MainDataBase.getInstance(context).getMainDao();
    }

    public LiveData<Resource<List<MainModel>>> searchListApi(final String query, final int pageNumber){
        return new NetworkBoundResource<List<MainModel>, ListResponse>(AppExecutors.getInstance()){

            @Override
            protected void saveCallResult(ListResponse item) {
                if(item.getModels() != null){

                    MainModel[] mainModels = new MainModel[item.getModels().size()];

                    int index = 0;
                    for(long rowid: mainDao.insertList((MainModel[]) (item.getModels().toArray(mainModels)))){
                        if(rowid == -1){
                            Log.d(TAG, "saveCallResult: CONFLICT... This recipe is already in the cache");
                            mainDao.updateModel(
                                    mainModels[index].getModel_id(),
                                    mainModels[index].getTitle(),
                                    mainModels[index].getPublisher(),
                                    mainModels[index].getImage_url(),
                                    mainModels[index].getSocial_rank()
                            );
                        }
                        index++;
                    }
                }
            }

            @Override
            protected boolean shouldFetch(List<MainModel> data) {
                return true;
            }

            @Override
            protected LiveData<List<MainModel>> loadFromDb() {
                return mainDao.searchModel(query,pageNumber);
            }

            @Override
            protected LiveData<ApiResponse<ListResponse>> createCall() {
                return ServiceGenerator.getMainApi()
                        .searchList(
                                Constants.API_KEY,
                                query,
                                String.valueOf(pageNumber)
                        );
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<MainModel>> searchItemApi(final String id){
        return new NetworkBoundResource<MainModel, ItemResponse>(AppExecutors.getInstance()){


            @Override
            protected void saveCallResult(@NonNull ItemResponse item) {
                if (item.getModel()!= null){
                    item.getModel().setTimestamp((int) System.currentTimeMillis()/1000);
                    mainDao.insertObjectAndReplace(item.getModel());
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable MainModel data) {
                Log.d(TAG, "shouldFetch: " + data.toString());
                int currentTime = (int)(System.currentTimeMillis()/1000);
                Log.d(TAG, "shouldFetch: current time " + currentTime);
                int lastRefresh = data.getTimestamp();
                Log.d(TAG, "shouldFetch: last refresh: " + lastRefresh);
                Log.d(TAG, "shouldFetch: it's been " +((currentTime - lastRefresh) / 60 / 60 / 24) +
                        " days since this model was refeshed.");
                if ((currentTime - data.getTimestamp())>= Constants.MODEL_REFRESH_TIME){
                    Log.d(TAG, "shouldFetch: SHOULD REFRESH MODEL?!" + true);
                    return true;
                }
                Log.d(TAG, "shouldFetch: SHOULD REFRESH MODEL?!" + false);
                return false;
            }

            @NonNull
            @Override
            protected LiveData<MainModel> loadFromDb() {
                return mainDao.getModel(id);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ItemResponse>> createCall() {
                return ServiceGenerator.getMainApi().getItem(
                        Constants.API_KEY,
                        id

                );
            }
        }.getAsLiveData();
    }
}

