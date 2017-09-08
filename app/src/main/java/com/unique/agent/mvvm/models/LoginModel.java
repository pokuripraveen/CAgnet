package com.unique.agent.mvvm.models;

import android.content.Context;

import com.unique.agent.actions.providers.LoginActionProvider;
import com.unique.agent.datastore.model.User;
import com.unique.agent.error.ErrorInfo;
import com.unique.agent.interfaces.ActionCallback;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.model.UserLabor;

import java.lang.ref.WeakReference;

import de.akquinet.android.androlog.Log;

/**
 * Created by praveenpokuri on 08/08/17.
 */

public class LoginModel extends BaseModel {
    LoginActionProvider mLoginProvider;
    ActionExecutor<UserLabor,UserLabor> mActionExecutor;
    WeakReference<ModelCallback> mLoginCallback;

    public LoginModel(Context context, BaseModel... baseModels) {
        super(context, baseModels);
    }

    public LoginModel(Context context, LoginActionProvider loginActionProvider, ActionExecutor actionExecutor){
        super(context);
        mLoginProvider = loginActionProvider;
        mActionExecutor = actionExecutor;

    }

    public void createUserAccount(String userName, String password, User.LoginType type, ModelCallback<UserLabor> loginCallback) {
        Log.i("PRAV", "Create userLabor Account userName "+userName+"  pwd "+password + "  Type "+type.name());
        UserLabor userLabor = new UserLabor();
        mLoginCallback = new WeakReference<ModelCallback>(loginCallback);
        mActionExecutor.execute(mLoginProvider.providesLoginAction(userLabor), null, new ActionCallback<UserLabor>() {
            @Override
            public void onSuccess(UserLabor response) {
                android.util.Log.i("PRAV","Grand onSuccess");
                mLoginCallback.get().onResponse(response);
            }

            @Override
            public void onError(ErrorInfo exception) {

            }
        });

    }
}
