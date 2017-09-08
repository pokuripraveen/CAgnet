package com.unique.agent.dagger.modules;

import android.app.Activity;
import android.content.Context;

import java.lang.ref.WeakReference;

import com.unique.agent.interfaces.base.ApplicationAgent;
import com.unique.agent.mvvm.view.LoginActivity;
import dagger.Module;
import dagger.Provides;

/**
 * Created by praveenpokuri on 09/08/17.
 */
@Module
public class ActivityModule {
    private WeakReference<Activity> mActivity;
    private WeakReference<ApplicationAgent> mApplicationAgent;
    public ActivityModule(Activity activity) {
        mActivity = new WeakReference<>(activity);
        if (activity == null)
            mApplicationAgent = new WeakReference<>(null);
        else
            mApplicationAgent = new WeakReference<>((ApplicationAgent)activity.getApplicationContext());
    }

    @Provides
    public Activity providesActivity(){
        return mActivity.get();
    }

    @Provides
    public ApplicationAgent providesApplicationAgent() {
        return mApplicationAgent.get();
    }
}
