package com.wfm.soundcollaborations.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 21.10.16.
 */
public class RegistrationActivity extends Activity {

    private EditText etUsername;
    private Button btnRegister;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.registration);

        //Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        etUsername = (EditText) findViewById(R.id.et_username);
        btnRegister = (Button) findViewById(R.id.btn_register_account);

    }
}
