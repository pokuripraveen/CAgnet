package com.unique.agent.mvvm.view;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.unique.agent.R;
import com.unique.agent.base.BaseActivity;

/**
 * Created by praveenpokuri on 04/07/17.
 */

public class SignUpActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Spinner mCountrySpinner;
    private Spinner mUtypeSpinner;
    private AutoCompleteTextView mUsername;
    private AutoCompleteTextView mEmail;
    private EditText mMobile;
    private View mProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_singnup);

        mUsername = (AutoCompleteTextView) findViewById(R.id.userLabor);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mMobile = (EditText) findViewById(R.id.mobile);
        mCountrySpinner = (Spinner) findViewById(R.id.countries_spinner);
        mUtypeSpinner = (Spinner) findViewById(R.id.userType_spinner);
        initView();
    }

    private void initView() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.country_codes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mCountrySpinner.setAdapter(countryAdapter);
        mCountrySpinner.setOnItemSelectedListener(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> userTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.account_types, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        userTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mUtypeSpinner.setAdapter(userTypeAdapter);
        mUtypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void initializeDaggerComponent() {

    }
}
