package com.e.mvvmexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.e.mvvmexample.adapters.OnItemListener;
import com.e.mvvmexample.adapters.RecyclerAdapter;
import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.util.Resource;
import com.e.mvvmexample.util.VerticalSpacingItemDecorator;
import com.e.mvvmexample.viewmodels.ListViewModel;

import java.util.List;

import static com.e.mvvmexample.viewmodels.ListViewModel.QUERY_EXHAUSTED;

public class ListActivity extends BaseActivity implements OnItemListener {

    private static final String TAG = "MainActivity";

    private ListViewModel listViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mRecyclerView = findViewById(R.id.rv_list);
        mSearchView = findViewById(R.id.search_view);

        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);

        initRecyclerView();
        initSearchView();
        subscribeObservers();
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
    }

    private void subscribeObservers(){
        Log.d(TAG, "subscribeObservers: ");
        listViewModel.getMainModelsList().observe(this, new Observer<Resource<List<MainModel>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<MainModel>> listResource) {
                if(listResource != null){
                    Log.d(TAG, "onChanged: status: " + listResource.status);

                    if(listResource.data != null){
                        switch (listResource.status){
                            case LOADING:{
                                if(listViewModel.getPageNumber() > 1){
                                    mAdapter.displayLoading();
                                }
                                else{
                                    mAdapter.displayOnlyLoading();
                                }
                                break;
                            }

                            case ERROR:{
                                Log.e(TAG, "onChanged: cannot refresh the cache." );
                                Log.e(TAG, "onChanged: ERROR message: " + listResource.message );
                                Log.e(TAG, "onChanged: status: ERROR, #recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                Toast.makeText(ListActivity.this, listResource.message, Toast.LENGTH_SHORT).show();

                                if(listResource.message.equals(QUERY_EXHAUSTED)){
                                    mAdapter.setQueryExhausted();
                                }
                                break;
                            }

                            case SUCCESS:{
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, #Recipes: " + listResource.data.size());
                                mAdapter.hideLoading();
                                mAdapter.setList(listResource.data);
                                break;
                            }
                        }
                    }
                }
                Log.d(TAG, "onChanged: list is null");
            }
        });

        listViewModel.getViewState().observe(this, new Observer<ListViewModel.ViewState>() {
            @Override
            public void onChanged(@Nullable ListViewModel.ViewState viewState) {
                if(viewState != null){
                    switch (viewState){

                        case ITEMS:{
                            // items will show automatically from other observer
                            break;
                        }

                        case CATEGORIES:{
                            displaySearchCategories();
                            break;
                        }
                    }
                }
            }
        });
    }

    private RequestManager initGlide(){

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private void searchRecipesApi(String query){
        mRecyclerView.smoothScrollToPosition(0);
        listViewModel.searchModelsApi(query, 1);
        mSearchView.clearFocus();
    }

    private void initRecyclerView(){
        ViewPreloadSizeProvider<String> viewPreloader = new ViewPreloadSizeProvider<>();
        mAdapter = new RecyclerAdapter(this, initGlide(), viewPreloader);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(30);
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<String>(
                Glide.with(this),
                mAdapter,
                viewPreloader,
                30);

        mRecyclerView.addOnScrollListener(preloader);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(!mRecyclerView.canScrollVertically(1)
                        && listViewModel.getViewState().getValue() == ListViewModel.ViewState.ITEMS){
                    listViewModel.searchNextPage();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initSearchView(){
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                searchRecipesApi(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("mainModel", mAdapter.getSelectedMainModel(position));
        startActivity(intent);
        Toast.makeText(this,"clicked", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCategoryClick(String category) {
        searchRecipesApi(category);
        Toast.makeText(this,"clicked", Toast.LENGTH_LONG).show();

    }

    private void displaySearchCategories(){
        mAdapter.displaySearchCategories();
    }


    @Override
    public void onBackPressed() {
        if(listViewModel.getViewState().getValue() == ListViewModel.ViewState.CATEGORIES){
            super.onBackPressed();
        }
        else{
            listViewModel.cancelSearchRequest();
            listViewModel.setViewCategories();
        }
    }



}
