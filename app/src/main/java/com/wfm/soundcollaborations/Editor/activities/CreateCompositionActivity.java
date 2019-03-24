package com.wfm.soundcollaborations.Editor.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wfm.soundcollaborations.R;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class CreateCompositionActivity extends AppCompatActivity {
    // If Activity starts, following onCreate function will be executed.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.create_composition_toolbar));

        // When user taps confirm button
        // Capture button from layout to add functionality
        Button testButtonId = (Button) findViewById(R.id.testButtonId);
        testButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // First parameter: context
                // Second Parameter: Activity we want to start after tapping confirm item
                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                // Start an instance of the DisplayMessageActivity specified by the Intent
                view.getContext().startActivity(intent);
            }
        });

    }
    // Add Menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }
}
