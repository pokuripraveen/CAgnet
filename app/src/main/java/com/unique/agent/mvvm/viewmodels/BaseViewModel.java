package com.unique.agent.mvvm.viewmodels;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseArray;

import com.unique.agent.mvvm.models.BaseModel;
import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 08/08/17.
 */

public class BaseViewModel {
    private SparseArray<BaseModel> mBaseModelSparseArray;
    private String mClassName;

    public BaseViewModel(BaseModel... baseModels) {
       
        mClassName = BaseViewModel.class.getSimpleName();
        mBaseModelSparseArray = new SparseArray<>(baseModels.length);
        for (int index = 0; index < baseModels.length; index++) {
            mBaseModelSparseArray.put(index, baseModels[index]);
        }
    }

    public void onCreate(Bundle savedInstanceState, Bundle extras) {
        Log.i(mClassName, "BaseViewModel OnCreate");
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).onCreate(savedInstanceState, extras);
        }
    }

    public void onStart() {
        Log.i(mClassName, "BaseViewModel onStart");
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).onStart();
        }
    }

    public void onStop() {
        Log.i(mClassName, "BaseViewModel onStop");
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).onStop();
        }
    }

    public void onDestroy() {
        Log.i(mClassName, "BaseViewModel onDestroy");
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).onDestroy();
        }
    }

    public void saveState(Bundle saveState) {
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).saveState(saveState);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Log.i(mClassName, "BaseViewModel onConfigurationChanged");
        for (int index = 0; index < mBaseModelSparseArray.size(); index++) {
            mBaseModelSparseArray.get(index).onConfigurationChanged(newConfig);
        }
    }
}
