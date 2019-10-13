package com.wfm.soundcollaborations.Editor.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputEditText;
import com.wfm.soundcollaborations.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.Button;

public class CreateCompositionActivity extends AppCompatActivity {

    public static String compositionTitleInput;
    // private Object CreateCompositionActivity;
    // View view = view.ge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_composition);

        setSupportActionBar(findViewById(R.id.base_toolbar));
        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle(R.string.new_composition);
        // TODO: Implement back-button logic
        toolbar.setDisplayHomeAsUpEnabled(true);

        /*
        When user taps confirm button, start empty Editor Activity
        TODO: this logic has to be applied to menu button.
        When menu button is ready, delete this code.
          */
        Button confirm = findViewById(R.id.confirm);
        confirm.setOnClickListener(view -> startEditorActivity(view));
    }

    // TODO: still listens on deprecated button
    // See https://developer.android.com/training/basics/firstapp/starting-activity#java
    private void startEditorActivity(View view) {
        Intent intent = new Intent(view.getContext(), EditorActivity.class);
        startActivity(intent);

        getCompositionTitleInput();
    }

    public String getCompositionTitleInput() {
        // Get specific User Input data by looking inside the TextInputEditText Object
        TextInputEditText compositionTitleTextField = findViewById(R.id.composition_title_textfield);
        // Cast "editable" to normal String Type
        compositionTitleInput = compositionTitleTextField.getText().toString();
        return compositionTitleInput;
    }


    /*
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
        if (itemId == R.id.set_composition_title)
            // startEditorActivity(item.getActionView());

        return super.onOptionsItemSelected(item);
    }
    */
}
