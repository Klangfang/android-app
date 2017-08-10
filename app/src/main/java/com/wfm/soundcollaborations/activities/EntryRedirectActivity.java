package com.wfm.soundcollaborations.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Markus Eberts on 21.10.16.
 */
public class EntryRedirectActivity extends Activity{
    public final static String TAG = EntryRedirectActivity.class.getSimpleName();

    // Preferences
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String accountName = preferences.getString(MainActivity.KEY_ACCOUNT_NAME, null);

        if (accountName == null) {
            Log.v(TAG, "Start registration");
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        } else {
            Log.v(TAG, "Start main menu");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        // Destroy the activity because the user was just redirected to an other activity
        finish();
    }
}
