package com.e.mvvmexample.persistance;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.e.mvvmexample.models.MainModel;

@Database(entities = {MainModel.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class MainDataBase extends RoomDatabase {

    public static final String DATABASE_NAME = "App_DB";

    private static MainDataBase instance;

    public static MainDataBase getInstance(final Context context){
        if (instance == null){
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    MainDataBase.class,
                    DATABASE_NAME
            ).build();
        }
        return instance;
    }
    public abstract MainDao getMainDao();
}
