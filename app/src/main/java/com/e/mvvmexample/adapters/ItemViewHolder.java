package com.e.mvvmexample.adapters;
//author: mor


import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.util.ViewPreloadSizeProvider;
import com.e.mvvmexample.R;
import com.e.mvvmexample.models.MainModel;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView title, publisher, socialScore;
    AppCompatImageView image;
    OnItemListener onItemListener;
    RequestManager requestManager;
    ViewPreloadSizeProvider viewPreloadSizeProvider;

    public ItemViewHolder(@NonNull View itemView,
                          OnItemListener onItemListener,
                          RequestManager requestManager,
                          ViewPreloadSizeProvider preloadSizeProvider) {
        super(itemView);

        this.onItemListener = onItemListener;
        this.requestManager = requestManager;
        this.viewPreloadSizeProvider = preloadSizeProvider;

        title = itemView.findViewById(R.id.item_title);
        publisher = itemView.findViewById(R.id.item_publisher);
        socialScore = itemView.findViewById(R.id.item_social_score);
        image = itemView.findViewById(R.id.item_image);

        itemView.setOnClickListener(this);
    }

    public void onBind(MainModel mainModel){

        requestManager
                .load(mainModel.getImage_url())
                .into(image);

        title.setText(mainModel.getTitle());
        publisher.setText(mainModel.getPublisher());
        socialScore.setText(String.valueOf(Math.round(mainModel.getSocial_rank())));

        viewPreloadSizeProvider.setView(image);
    }

    @Override
    public void onClick(View v) {
        onItemListener.onItemClick(getAdapterPosition());
    }
}





