package com.unique.agent.mvvm.viewmodels;

import android.content.Context;

import com.unique.agent.datastore.model.User;
import com.unique.agent.error.ErrorInfo;
import com.unique.agent.model.UserLabor;
import com.unique.agent.mvvm.models.LoginModel;
import com.unique.agent.mvvm.models.ModelCallback;

/**
 * Created by praveenpokuri on 08/08/17.
 */

public class LoginViewModel extends BaseViewModel {
    private LoginModel mLogimModel;
    private Context mContext;

     public LoginViewModel(Context context, LoginModel loginModel){
         super(loginModel);
         mContext = context;
         mLogimModel = loginModel;
     }

     public void createUser(String name, String pwd, User.LoginType type ){
         mLogimModel.createUserAccount(name,pwd, type, loginCallback);
     }

     private ModelCallback<UserLabor> loginCallback = new ModelCallback<UserLabor>() {
         @Override
         public void onResponse(UserLabor response) {

         }

         @Override
         public void onError(ErrorInfo error) {

         }
     };
}
