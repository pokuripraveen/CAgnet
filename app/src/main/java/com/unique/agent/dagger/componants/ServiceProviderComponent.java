package com.unique.agent.dagger.componants;

import com.unique.agent.dagger.CustomScope;
import com.unique.agent.dagger.modules.ActivityModule;
import com.unique.agent.dagger.modules.ServiceModule;
import com.unique.agent.dagger.modules.ViewModelModule;

import dagger.Component;

/**
 * Created by praveenpokuri on 23/08/17.
 */
@CustomScope
@Component(dependencies = ApplicationComponent.class,
        modules = {
                ServiceModule.class,
        }
)
public interface ServiceProviderComponent {

}
