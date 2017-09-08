package com.unique.agent.dagger.modules;

import android.content.Context;

import javax.inject.Named;

import com.unique.agent.actions.ActionExecutorProvider;
import com.unique.agent.actions.providers.LoginActionProvider;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.mvvm.models.LoginModel;
import dagger.Module;
import dagger.Provides;

/**
 * Created by praveenpokuri on 16/08/17.
 */
@Module
public class ModelModule {
    @Provides
    LoginModel providesLoginModel(Context applicationContext, LoginActionProvider loginActionProvider, @Named("ParallelExecutor") ActionExecutor parallelActionExecutor){
        return new LoginModel(applicationContext, loginActionProvider, parallelActionExecutor);
    }
}
