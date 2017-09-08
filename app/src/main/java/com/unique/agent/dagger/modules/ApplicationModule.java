package com.unique.agent.dagger.modules;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import com.unique.agent.actions.ActionExecutorProvider;
import com.unique.agent.actions.ParallelActionExecutor;
import com.unique.agent.datastore.SharedPrefStorageHelper;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.CancellableActionExecutor;
import com.unique.agent.interfaces.KeyValueStorage;
import com.unique.agent.network.INetworkManager;
import com.unique.agent.network.UrlConnectionNetworkManager;
import com.unique.agent.room.data.AppDatabase;

import dagger.Module;
import dagger.Provides;

/**
 * Created by praveenpokuri on 08/08/17.
 */
@Module
public class ApplicationModule {
    private Context mContext;

    public ApplicationModule(Context context){
        mContext = context;
    }

    @Singleton
    @Provides
    public Context providesApplicationContext(){
        return mContext;
    }

    @Singleton
    @Provides
    public KeyValueStorage providesSharedPrefStorage(Context context){
        return new SharedPrefStorageHelper(context);
    }

    @Singleton
    @Provides
    @Named("ParallelExecutor")
    public ActionExecutor providesActionExecutor() {
        return ActionExecutorProvider.getParallelExecutor();
    }

    @Singleton
    @Provides
    @Named("UIExecutor")
    public ActionExecutor providesUIExecutor() {
        return ActionExecutorProvider.getUIActionExecutor();
    }

    @Singleton
    @Provides
    @Named("ParallelCancellableExecutor")
    public CancellableActionExecutor providesCancelableActionExecutor() {
        return ActionExecutorProvider.getParallelCancellableExecutor();
    }

    @Singleton
    @Provides
    public AppDatabase providesAppDatabase(Context contex){
        return AppDatabase.getDatabase(contex);
    }

    @Singleton
    @Provides
    public INetworkManager providesNetworkManager(Context context, @Named("ParallelExecutor") ActionExecutor executor){
        return new UrlConnectionNetworkManager(context,executor.getThreadPool());
    }
}
