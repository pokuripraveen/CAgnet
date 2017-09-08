package com.unique.agent.dagger.componants;

import com.unique.agent.dagger.CustomScope;
import com.unique.agent.dagger.modules.ActionModule;
import com.unique.agent.dagger.modules.ActivityModule;
import com.unique.agent.dagger.modules.ViewModelModule;
import com.unique.agent.mvvm.view.LoginActivity;
import com.unique.agent.mvvm.view.SignUpActivity;
import dagger.Component;

/**
 * Created by praveenpokuri on 08/08/17.
 */
@CustomScope
@Component(dependencies = ApplicationComponent.class,
        modules = {
                ActivityModule.class,
                ViewModelModule.class
        }
)
public interface AuthComponent {
    void inject(LoginActivity activity);
    void inject(SignUpActivity activity);
}
