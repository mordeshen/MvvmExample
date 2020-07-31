package com.e.mvvmexample.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.repository.Repository;
import com.e.mvvmexample.util.Resource;

import java.util.List;

public class ListViewModel extends AndroidViewModel {

    private static final String TAG = "ListViewModel";
    public static final String QUERY_EXHAUSTED = "No more results.";

    public enum ViewState{CATEGORIES, ITEMS}

    private MutableLiveData<ViewState> viewState;
    private MediatorLiveData<Resource<List<MainModel>>> mainModelsList = new MediatorLiveData<>();
    private Repository repository;

    private boolean isQueryExhausted;
    private boolean isPerformingQuery;
    private int pageNumber;
    private String query;
    private boolean cancelRequest;
    private long requestStartTime;

    public ListViewModel(@NonNull Application application) {
        super(application);
        repository = Repository.getInstance(application);

        init();
    }

    private void init() {
        if (viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);
        }
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }

    public MediatorLiveData<Resource<List<MainModel>>> getMainModelsList() {
        return mainModelsList;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setViewCategories(){
        viewState.setValue(ViewState.CATEGORIES);
    }

    public void searchModelsApi(String query, int pageNumber){
        if (!isPerformingQuery){
            if (pageNumber == 0){
                pageNumber = 1;
            }
            this.pageNumber = pageNumber;
            this.query = query;
            isQueryExhausted = false;
            executeSearch();
        }
    }

    public void searchNextPage(){
        if (!isQueryExhausted && !isPerformingQuery) {
            pageNumber++;
            executeSearch();
        }
    }

    private void executeSearch() {
        requestStartTime = System.currentTimeMillis();
        cancelRequest = false;
        isPerformingQuery = true;
        viewState.setValue(ViewState.ITEMS);
        final LiveData<Resource<List<MainModel>>> repositorySource = repository.searchListApi(query,pageNumber);
        mainModelsList.addSource(repositorySource, listResource -> {
            if (!cancelRequest){
                if (listResource != null){
                    if (listResource.status == Resource.Status.SUCCESS) {
                        Log.d(TAG, "onChanged: RQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                        Log.d(TAG, "onChanged: page number: " + pageNumber);
                        Log.d(TAG, "onChanged: " + listResource.data);

                        isPerformingQuery = false;
                        if (listResource.data != null) {
                            if (listResource.data.size() == 0) {
                                Log.d(TAG, "onChanged: query is exhausted");
                                mainModelsList.setValue(
                                        new Resource<List<MainModel>>(
                                                Resource.Status.ERROR,
                                                listResource.data,
                                                QUERY_EXHAUSTED
                                        )
                                );
                                isQueryExhausted = true;
                            }
                        }
                        mainModelsList.removeSource(repositorySource);
                    }
                        else if (listResource.status == Resource.Status.ERROR){
                            Log.d(TAG, "onChanged: REQUEST TIME: " + (System.currentTimeMillis() - requestStartTime) / 1000 + " seconds.");
                            isPerformingQuery = false;
                            if (listResource.message.equals(QUERY_EXHAUSTED)){
                                isQueryExhausted = true;
                            }
                            mainModelsList.removeSource(repositorySource);
                        }
                        mainModelsList.setValue(listResource);
                    }
                    else {
                        mainModelsList.removeSource(repositorySource);
                    }
                }
            else {
                mainModelsList.removeSource(repositorySource);
            }
        });

    }

    public void cancelSearchRequest(){
        if (isPerformingQuery){
            Log.d(TAG, "cancelSearchRequest: canceling the search request");
            cancelRequest = true;
            isPerformingQuery = false;
            pageNumber = 1;
        }
    }
}
