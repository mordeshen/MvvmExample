package com.e.mvvmexample.util;

import android.util.Log;

import com.e.mvvmexample.models.MainModel;

import java.util.List;

public class Testing {

    public static void printRecipes(List<MainModel> list, String tag){
        for(MainModel mainModel: list){
            Log.d(tag, "onChanged: " + mainModel.getTitle());
        }
    }
}
