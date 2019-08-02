package com.wfm.soundcollaborations.Editor.activities;

import android.content.Intent;
import android.os.Bundle;

import com.wfm.soundcollaborations.R;

import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;

public class CreateCompositionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.base_toolbar));

        /*
        When user taps confirm button, start empty Editor Activity
        TODO: this logic has to be applied to menu button.
        When menu button is ready, delete this code.
          */
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(view -> startEditorActivity(view));
    }

    // Add Menu to Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_composition_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Check which items are being clicked by checking ID
        int itemId = item.getItemId();

        // TODO: Go to empty editor activity instead. See startEditorActivity() below...
        if (itemId == R.id.set_composition_title) {
             Toast.makeText(this, "Geklickt!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // TODO: still listens on deprecated button
    // See https://developer.android.com/training/basics/firstapp/starting-activity#java
    private void startEditorActivity(View view) {
        Intent intent = new Intent(view.getContext(), EditorActivity.class);
        //intent.putExtra(PICK_RESPONSE, response);
        view.getContext().startActivity(intent);
    }
}
