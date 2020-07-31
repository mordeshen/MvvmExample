package com.e.mvvmexample.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.e.mvvmexample.R;
import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPreloader.PreloadModelProvider<String>
{

    private static final int ITEM_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    private static final int EXHAUSTED_TYPE = 4;

    private List<MainModel> modelList;
    private OnItemListener onItemListener;
    private RequestManager requestManager;
    private ViewPreloadSizeProvider<String> preloadSizeProvider;

    public RecyclerAdapter(
            OnItemListener onItemListener,
            RequestManager requestManager,
            ViewPreloadSizeProvider<String> viewPreloadSizeProvider){
        this.onItemListener = onItemListener;
        this.requestManager = requestManager;
        this.preloadSizeProvider = viewPreloadSizeProvider;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = null;

       switch (viewType){
           case ITEM_TYPE:{
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
                return new ItemViewHolder(view,onItemListener,requestManager,preloadSizeProvider);
           }

           case LOADING_TYPE:{
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_list_item, parent, false);
               return new LoadingViewHolder(view);
           }

           case EXHAUSTED_TYPE:{
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_search_exhausted, parent, false);
               return new SearchExhaustedViewHolder(view);
           }

           case CATEGORY_TYPE:{
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_list_item, parent, false);
               return new CategoryViewHolder(view, onItemListener, requestManager);
           }

           default:{
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_mainitem_list_item, parent, false);
               return new ItemViewHolder(view, onItemListener, requestManager, preloadSizeProvider);
           }
       }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int itemViewType = getItemViewType(position);
        if(itemViewType == ITEM_TYPE){
            ((ItemViewHolder)holder).onBind(modelList.get(position));
        }
        else if(itemViewType == CATEGORY_TYPE){
            ((CategoryViewHolder)holder).onBind(modelList.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(modelList.get(position).getSocial_rank() == -1){
            return CATEGORY_TYPE;
        }
        else if(modelList.get(position).getTitle().equals("LOADING...")){
            return LOADING_TYPE;
        }
        else if(modelList.get(position).getTitle().equals("EXHAUSTED...")){
            return EXHAUSTED_TYPE;
        }
        else{
            return ITEM_TYPE;
        }
    }


    public void setQueryExhausted(){
        hideLoading();
        MainModel exhaustedMainModel = new MainModel();
        exhaustedMainModel.setTitle("EXHAUSTED...");
        modelList.add(exhaustedMainModel);
        notifyDataSetChanged();
    }

    public void hideLoading(){
        if(isLoading()){
            if(modelList.get(0).getTitle().equals("LOADING...")){
                modelList.remove(0);
            }
            else if(modelList.get(modelList.size() - 1).equals("LOADING...")){
                modelList.remove(modelList.size() - 1);
            }
            notifyDataSetChanged();
        }
    }

    // pagination loading
    public void displayLoading(){
        if(modelList == null){
            modelList = new ArrayList<>();
        }
        if(!isLoading()){
            MainModel mainModel = new MainModel();
            mainModel.setTitle("LOADING...");
            modelList.add(mainModel);
            notifyDataSetChanged();
        }
    }

    private boolean isLoading(){
        if(modelList != null){
            if(modelList.size() > 0){
                if(modelList.get(modelList.size() - 1).getTitle().equals("LOADING...")){
                    return true;
                }
            }
        }
        return false;
    }

    public void displaySearchCategories(){
        List<MainModel> categories = new ArrayList<>();
        for(int i = 0; i< Constants.DEFAULT_SEARCH_CATEGORIES.length; i++){
            MainModel MainModel = new MainModel();
            MainModel.setTitle(Constants.DEFAULT_SEARCH_CATEGORIES[i]);
            MainModel.setImage_url(Constants.DEFAULT_SEARCH_CATEGORY_IMAGES[i]);
            MainModel.setSocial_rank(-1);
            categories.add(MainModel);
        }
        modelList = categories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(modelList != null){
            return modelList.size();
        }
        return 0;
    }

    public void setList(List<MainModel> mainModels){
        modelList = mainModels;
        notifyDataSetChanged();
    }

    public MainModel getSelectedMainModel(int position){
        if(modelList != null){
            if(modelList.size() > 0){
                return modelList.get(position);
            }
        }
        return null;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        String url = modelList.get(position).getImage_url();
        if(TextUtils.isEmpty(url)){
            return Collections.emptyList();
        }
        return Collections.singletonList(url);
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String item) {
        return requestManager.load(item);
    }
    private void clearList(){
        if(modelList == null){
            modelList = new ArrayList<>();
        }
        else{
            modelList.clear();
        }
        notifyDataSetChanged();
    }

    // display loading during search request
    public void displayOnlyLoading(){
        clearList();
        MainModel mainModel = new MainModel();
        mainModel.setTitle("LOADING...");
        modelList.add(mainModel);
        notifyDataSetChanged();
    }
}

