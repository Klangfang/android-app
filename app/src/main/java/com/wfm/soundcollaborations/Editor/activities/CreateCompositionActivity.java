package com.wfm.soundcollaborations.Editor.activities;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Response;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;

import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;


public class CreateCompositionActivity extends AppCompatActivity {


    public static final Long COMPOSITION_ID = 1L;
    public static final String PICK_RESPONSE = "PICK";

    private CompositionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.create_composition_toolbar));

        // When user taps confirm button
        // Capture button from layout to add functionality
        Button testButtonId = findViewById(R.id.testButtonId);
        testButtonId.setOnClickListener(view -> doRequest(view));

    }

    // Add Menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }


    private void doRequest(View view) {
        if (client == null) {
            client = new CompositionServiceClient(view.getContext());
        }
        Response.Listener<String> listener = response -> startEditorActivity(view, response);
        //TODO client.pick(COMPOSITION_ID, listener);
    }


    private void startEditorActivity(View view, String response) {
        Intent intent = new Intent(view.getContext(), EditorActivity.class);
        intent.putExtra(PICK_RESPONSE, response);
        view.getContext().startActivity(intent);
    }
}
