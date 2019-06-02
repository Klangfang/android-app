package com.wfm.soundcollaborations.Editor.activities;

import android.os.Bundle;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.common.NetworkActivity;
import com.wfm.soundcollaborations.webservice.HttpUtils;

import android.view.Menu;
import android.widget.Button;

public class CreateCompositionActivity extends NetworkActivity {


    // If Activity starts, following onCreate function will be executed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.create_composition_toolbar));

        initNetworking(HttpUtils.COMPOSITION_PICK_URL);

        // When user taps confirm button
        // Capture button from layout to add functionality
        Button testButtonId = findViewById(R.id.testButtonId);
        testButtonId.setOnClickListener(view -> startDownload(view));

    }

    // Add Menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }
}
