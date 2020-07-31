package com.e.mvvmexample.persistance;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.e.mvvmexample.models.MainModel;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;
import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MainDao {
    @Insert(onConflict = IGNORE)
    long[] insertList(MainModel... mainModels);

    @Insert(onConflict = REPLACE)
    void insertObjectAndReplace(MainModel mainModel);

    @Query("UPDATE main_models SET title = :title, publisher = :publisher, image_url = :image_url, social_rank = :social_rank " +
            "WHERE model_id = :recipe_id")
    void updateModel(String recipe_id, String title, String publisher, String image_url, float social_rank);

    @Query("SELECT * FROM main_models WHERE title LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%' " +
            "ORDER BY social_rank DESC LIMIT (:pageNumber * 30)")
    LiveData<List<MainModel>> searchModel(String query, int pageNumber);

    @Query("SELECT * FROM main_models WHERE model_id = :model_id")
    LiveData<MainModel> getModel(String model_id);
}
