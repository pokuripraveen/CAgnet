package com.unique.agent.dagger.modules;

import android.content.Context;

import com.unique.agent.actions.providers.LoginActionProvider;
import com.unique.agent.mvvm.models.LoginModel;
import com.unique.agent.mvvm.viewmodels.LoginViewModel;
import dagger.Module;
import dagger.Provides;

/**
 * Created by praveenpokuri on 09/08/17.
 */
@Module(includes = {
        ActionModule.class,
        NetworkModule.class,
        ModelModule.class
})
public class ViewModelModule {

    @Provides
    LoginViewModel providesLoginViewModel(Context context, LoginModel model){
        return new LoginViewModel(context, model);
    }

}
