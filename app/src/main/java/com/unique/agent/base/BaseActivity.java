package com.unique.agent.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.unique.agent.mvvm.viewmodels.BaseViewModel;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public abstract class BaseActivity<VM extends BaseViewModel> extends Activity {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDaggerComponent();
    }

    protected abstract void initializeDaggerComponent();
}
