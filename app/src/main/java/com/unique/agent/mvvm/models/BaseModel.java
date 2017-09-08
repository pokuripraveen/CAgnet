package com.unique.agent.mvvm.models;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseArray;

/**
 * Created by praveenpokuri on 08/08/17.
 */

public class BaseModel {
    private SparseArray<BaseModel> mBaseModelSparseArray;
    private Context mContext;

    public BaseModel (Context context, BaseModel... baseModels){
        mBaseModelSparseArray = new SparseArray<>(baseModels.length);
        mContext = context.getApplicationContext();
        for(int index=0; index<baseModels.length; index++){
            mBaseModelSparseArray.put(index, baseModels[index]);
        }
    }

    public void onCreate(Bundle savedInstanceState, Bundle extras) {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).onCreate(savedInstanceState, extras);
        }
    }

    public void onStart() {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).onStart();
        }
    }

    public void onStop() {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).onStop();
        }
    }

    public void onDestroy() {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).onDestroy();
        }
    }

    public void saveState(Bundle saveState) {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).saveState(saveState);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        for(int index=0; index<mBaseModelSparseArray.size(); index++){
            mBaseModelSparseArray.get(index).onConfigurationChanged(newConfig);
        }
    }
}
