package com.wfm.soundcollaborations.Editor.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wfm.soundcollaborations.R;
import android.view.Menu;

public class CreateCompositionActivity extends AppCompatActivity {
    // If Activity starts, following onCreate function will be executed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.create_composition_toolbar));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }
}