package com.unique.agent.base;

import com.unique.agent.dagger.componants.ApplicationComponent;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.KeyValueStorage;
import com.unique.agent.network.INetworkManager;
import com.unique.agent.room.data.AppDatabase;

/**
 * Created by praveenpokuri on 07/09/17.
 */

public class AppCore {

    private static AppCore APP_CORE;

    private KeyValueStorage mSharedPref;

    private ActionExecutor mUiExecutor;

    private ActionExecutor mParallelExecutor;

    private AppDatabase mAppDatabase;

    private INetworkManager mNetworkManager;


    private AppCore() {
        //
    }

    public static AppCore getInstance() {
        if (APP_CORE == null) {
            synchronized (AppCore.class) {
                if (APP_CORE == null) {
                    APP_CORE = new AppCore();
                }
            }
        }
        return APP_CORE;
    }

    public void init(ApplicationComponent mComponent) {
        mSharedPref = mComponent.sharedPrefStorage();
        mUiExecutor = mComponent.uIExecutor();
        mParallelExecutor = mComponent.actionExecutor();
        mAppDatabase = mComponent.appDatabase();
        mNetworkManager = mComponent.networkManager();
    }

    public KeyValueStorage getSharedPref() {
        return mSharedPref;
    }

    public ActionExecutor getUiExecutor() {
        return mUiExecutor;
    }

    public ActionExecutor getParallelExecutor() {
        return mParallelExecutor;
    }

    public AppDatabase getAppDatabase() {
        return mAppDatabase;
    }

    public INetworkManager getNetworkManager() {
        return mNetworkManager;
    }
}
