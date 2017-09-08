package com.unique.agent.actions.providers;

import com.unique.agent.actions.Action;
import com.unique.agent.actions.action.LoginAction;
import com.unique.agent.model.UserLabor;

/**
 * Created by praveenpokuri on 09/08/17.
 */

public class LoginActionProvider {

    private UserLabor mUserLabor;

    public LoginActionProvider(){

    }

    public Action<UserLabor,UserLabor> providesLoginAction(UserLabor userLabor){
        return new LoginAction(mUserLabor);
    }
}
