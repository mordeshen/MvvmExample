package com.e.mvvmexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.e.mvvmexample.models.MainModel;
import com.e.mvvmexample.util.Resource;
import com.e.mvvmexample.viewmodels.ItemViewModel;

public class ItemActivity extends BaseActivity {
    private static final String TAG = "RecipeActivity";

    // UI components
    private ImageView imageView;
    private TextView itemTitle, itemRank;
    private LinearLayout detailsContainer;
    private ScrollView mScrollView;

    private ItemViewModel itemViewModel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        imageView = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemRank = findViewById(R.id.item_social_score);
        detailsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);

        itemViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("mainModel")){
            MainModel mainModel = getIntent().getParcelableExtra("mainModel");
            Log.d(TAG, "getIncomingIntent: " + mainModel.getTitle());
            subscribeObservers(mainModel.getModel_id());
        }
    }

    private void subscribeObservers(final String recipeId){
        itemViewModel.searchItemApi(recipeId).observe(this, new Observer<Resource<MainModel>>() {
            @Override
            public void onChanged(@Nullable Resource<MainModel> itemResource) {
                if(itemResource != null){
                    if(itemResource.data != null){
                        switch (itemResource.status){

                            case LOADING:{
                                showProgressBar(true);
                                break;
                            }

                            case ERROR:{
                                Log.e(TAG, "onChanged: status: ERROR, Recipe: " + itemResource.data.getTitle() );
                                Log.e(TAG, "onChanged: ERROR message: " + itemResource.message );
                                showParent();showProgressBar(false);
                                setItemProperties(itemResource.data);
                                break;
                            }

                            case SUCCESS:{
                                Log.d(TAG, "onChanged: cache has been refreshed.");
                                Log.d(TAG, "onChanged: status: SUCCESS, Recipe: " + itemResource.data.getTitle());
                                showParent();
                                showProgressBar(false);
                                setItemProperties(itemResource.data);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    private void setItemProperties(MainModel model){
        if(model != null){
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.white_background)
                    .error(R.drawable.white_background);

            Glide.with(this)
                    .setDefaultRequestOptions(options)
                    .load(model.getImage_url())
                    .into(imageView);

            itemTitle.setText(model.getTitle());
            itemRank.setText(String.valueOf(Math.round(model.getSocial_rank())));

            setDetails(model);
        }
    }

    private void setDetails(MainModel model){
        detailsContainer.removeAllViews();

        if(model.getIngredients() != null){
            for(String ingredient: model.getIngredients()){
                TextView textView = new TextView(this);
                textView.setText(ingredient);
                textView.setTextSize(15);
                textView.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                detailsContainer.addView(textView);
            }
        }
        else{
            TextView textView = new TextView(this);
            textView.setText("Error retrieving ingredients.\nCheck network connection.");
            textView.setTextSize(15);
            textView.setLayoutParams(
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            detailsContainer.addView(textView);
        }
    }


    private void showParent(){
        mScrollView.setVisibility(View.VISIBLE);
    }
}
