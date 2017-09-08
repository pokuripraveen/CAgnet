package com.unique.agent.mvvm.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import javax.inject.Inject;
import javax.inject.Named;

import com.unique.agent.R;
import com.unique.agent.actions.providers.LoginActionProvider;
import com.unique.agent.base.AppCore;
import com.unique.agent.dagger.componants.AuthComponent;
import com.unique.agent.dagger.componants.DaggerAuthComponent;
import com.unique.agent.dagger.modules.ActivityModule;
import com.unique.agent.dagger.modules.NetworkModule;
import com.unique.agent.datastore.model.User;
import com.unique.agent.interfaces.ActionExecutor;
import com.unique.agent.interfaces.KeyValueStorage;
import com.unique.agent.interfaces.base.ApplicationAgent;
import com.unique.agent.base.BaseActivity;
import com.unique.agent.databinding.ActivityLoginBinding;
import com.unique.agent.model.UserLabor;
import com.unique.agent.mvvm.viewmodels.LoginViewModel;
import com.unique.agent.network.INetworkManager;
import com.unique.agent.room.data.AppDatabase;

import java.util.List;

import de.akquinet.android.androlog.Log;
import okhttp3.OkHttpClient;

import static com.unique.agent.datastore.model.User.*;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class LoginActivity extends BaseActivity<LoginViewModel> implements AdapterView.OnItemSelectedListener {

    private Spinner mSpinner;
    private Spinner mUtypeSpinner;
    private AutoCompleteTextView mUsername;
    private AutoCompleteTextView mEmail;
    private TextInputLayout userOption;
    private EditText mMobile;
    private View mProgress;

    @Inject
    LoginActionProvider loginActionProvider;

    @Inject
    OkHttpClient mOkHttpClient;

    @Inject
    LoginViewModel mLoginModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ActivityLoginBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_login);

        mUsername = (AutoCompleteTextView) findViewById(R.id.userLabor);
        userOption = (TextInputLayout) findViewById(R.id.userContainer);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mMobile = (EditText) findViewById(R.id.mobile);
        mSpinner = (Spinner) findViewById(R.id.top_spinner);
        mUtypeSpinner = (Spinner) findViewById(R.id.userType_spinner);

        initView();
    }

    private void initLogin() {

        android.util.Log.i("PRAV"," initLogin START");
        mLoginModel.createUser("Praveen","Durga", LoginType.USERNAME);

        Runnable myrun = new Runnable(){

            @Override
            public void run() {
                android.util.Log.i("PRAV","myrun ");
                com.unique.agent.room.model.User user = new com.unique.agent.room.model.User();
                user.setFirstName("Praveen");
                user.setLastName("Pokuri");
                AppDatabase mDB =  AppCore.getInstance().getAppDatabase();
                mDB.getUserDao().insertAll(user);
                List<com.unique.agent.room.model.User> users = mDB.getUserDao().getAll();
                android.util.Log.i("PRAV","Users Length "+users.size());
                for(com.unique.agent.room.model.User storedUser :users){
                    android.util.Log.i("PRAV"," First "+storedUser.getFirstName()+"  secondname "+storedUser.getLastName());
                }
            }
        };
        new Thread(myrun).start();;

    }


    private void initView() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.account_options, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(countryAdapter);
        mSpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> userAdapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mUtypeSpinner.setAdapter(userAdapter);
        mUtypeSpinner.setOnItemSelectedListener(this);

        Button signIn = (Button) findViewById(R.id.login_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initLogin();
            }
        });

        Button signup = (Button) findViewById(R.id.signup_button);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSignupScreen();
            }
        });

    }

    private void launchSignupScreen() {
        Intent singnup_intent = new Intent(LoginActivity.this, SignUpActivity.class);
        LoginActivity.this.startActivity(singnup_intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(i){
            case 0:
                userOption.setHint("Enter Email");
                break;
            case 1:
                userOption.setHint("Enter Mobile");
                break;
            case 2:
                userOption.setHint("Enter Username");
                break;
            default:
                userOption.setHint("Enter Username");

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void initializeDaggerComponent() {
        android.util.Log.i("PRAV","initializeDaggerComponent");
        AuthComponent loginComponant = DaggerAuthComponent.builder()
                .activityModule(new ActivityModule(this))
                .networkModule(new NetworkModule(""))
                .applicationComponent(((ApplicationAgent) getApplicationContext()).getComponent()).build();

        loginComponant.inject(this);

    }
}
