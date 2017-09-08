package com.unique.agent.base;

import android.app.Application;
import android.content.Context;

import com.unique.agent.dagger.componants.ApplicationComponent;
import com.unique.agent.dagger.componants.DaggerApplicationComponent;
import com.unique.agent.dagger.modules.ApplicationModule;
import com.unique.agent.interfaces.base.ApplicationAgent;

import de.akquinet.android.androlog.Log;


/**
 * Created by praveenpokuri on 08/08/17.
 */

public class CounterAgentApplication extends Application implements ApplicationAgent {

    public static ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.init();
        initDagger(this);
    }

    private void initDagger(Context context) {
        mComponent = DaggerApplicationComponent.builder().
                applicationModule(new ApplicationModule(context)).build();
        AppCore.getInstance().init(mComponent);
    }

    @Override
    public ApplicationComponent getComponent() {
        return mComponent;
    }
}
