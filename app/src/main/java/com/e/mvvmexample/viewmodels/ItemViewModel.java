package com.e.mvvmexample.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.repository.Repository;
import com.e.mvvmexample.util.Resource;


public class ItemViewModel extends AndroidViewModel {

    private Repository repository;

    public ItemViewModel(@NonNull Application application){
        super(application);
        repository = Repository.getInstance(application);
    }

    public LiveData<Resource<MainModel>> searchItemApi(String id){
        return repository.searchItemApi(id);
    }

}
