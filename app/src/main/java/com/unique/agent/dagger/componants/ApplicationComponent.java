package com.unique.agent.dagger.componants;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import com.unique.agent.base.AppCore;
import com.unique.agent.dagger.modules.ApplicationModule;
import com.unique.agent.dagger.modules.NetworkModule;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.CancellableActionExecutor;
import com.unique.agent.interfaces.KeyValueStorage;
import com.unique.agent.network.INetworkManager;
import com.unique.agent.room.data.AppDatabase;

import dagger.Component;
import dagger.Provides;

/**
 * Created by praveenpokuri on 08/08/17.
 */

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {


    Context applicationContext();

    KeyValueStorage sharedPrefStorage();

    @Named("ParallelExecutor")
    ActionExecutor actionExecutor();

    @Named("ParallelCancellableExecutor")
    CancellableActionExecutor cancelableActionExecutor();

    @Named("UIExecutor")
    ActionExecutor uIExecutor();

    AppDatabase appDatabase();

    INetworkManager networkManager();

}
