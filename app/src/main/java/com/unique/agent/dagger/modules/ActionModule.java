package com.unique.agent.dagger.modules;

import com.unique.agent.actions.providers.LoginActionProvider;

import dagger.Module;
import dagger.Provides;

/**
 * Created by praveenpokuri on 09/08/17.
 */
@Module
public class ActionModule {

    @Provides
    LoginActionProvider providesLoginActionProvider(){
        return new LoginActionProvider();
    }
}
