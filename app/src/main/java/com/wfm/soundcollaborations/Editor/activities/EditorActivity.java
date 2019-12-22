package com.wfm.soundcollaborations.Editor.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.Editor.model.composition.Composition;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.fragments.ComposeFragment;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();
    @BindView(R.id.composition)
    CompositionView compositionView;

    private Composition composition;

    private Handler handler;

    private boolean recording;
    private int startPositionInWidth;
    private int soundLength;

    private Timer recordTimer = new Timer();

    @BindView(R.id.btn_delete)
    ImageButton deletedBtn;

    @BindView(R.id.btn_record)
    FloatingActionButton recordBtn;

    @BindView(R.id.btn_play)
    PlayPauseView playBtn;


    /**
     * This constant creates a placeholder for the user's consent of the record audio permission.
     * It will be used when handling callback from the runtime permission (onRequestPermissionsResult)
     */
    private final int RECORD_AUDIO_PERMISSIONS_DECISIONS = 1;

    private CompositionResponse compositionResponse;
    private CompositionServiceClient client;

    private boolean create;


    /**
     * create soundViews to be added to the corresponding sounds
     * let SoundDownloader update these views using listener
     * when a view finished downloading it add itself to the track
     * when all sounds are loaded the CompositionOverviewResp will be ready to play the sounds
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.base_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(CreateCompositionActivity.compositionTitleInput);

        // create soundViews to be added to the corresponding sounds
        // let SoundDownloader update these views using listener
        // when a view finished downloading it add itself to the track
        // when all sounds are loaded the CompositionOverviewResp will be ready to play the sounds
        Intent intent = getIntent();
        String compositionTitle = intent.getStringExtra(String.valueOf(R.id.composition_title_textfield));
        String compositionJson = intent.getStringExtra(ComposeFragment.PICK_RESPONSE);
        //builder = new CompositionBuilder(compositionView, 4, compositionTitle);
        // create new composition has no json response
        if (StringUtils.isNotBlank(compositionJson)) {

            create = false;
            compositionResponse = JsonUtil.fromJson(compositionJson, CompositionResponse.class);
            if (compositionResponse != null) {

                try {
                    composition = new Composition.CompositionConfigurer(compositionView)
                            .build(compositionResponse);
                } catch (Throwable t) {
                    handleErrorOnRecording(t.getMessage());
                }

            }

        } else {

            create = true;
            composition = new Composition.CompositionConfigurer(compositionView)
                    .title(compositionTitle)
                    .build();

        }

        // ImageButton has to be set to disabled first because this can't be done in xml
        deletedBtn.setEnabled(false);
        deletedBtn.setOnClickListener(delBtnview -> deleteConfirmation(delBtnview.getContext()));

        client = new CompositionServiceClient();
    }

    // Add Menu to Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Check which items are being clicked by checking ID
        int itemId = item.getItemId();
        if (itemId == R.id.release_composition) {

            if (create) {

                composition.create();
                Toast.makeText(this, "Congratulations! Your composition has been created!", Toast.LENGTH_LONG).show();


            } else {

                // Code for releasing the composition comes here
                composition.join();
                Toast.makeText(this, "Congratulations! Your composition collaboration has been released!", Toast.LENGTH_LONG).show();

            }

        } else if (itemId == 16908332) {

            composition.cancel();

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }


    private void deleteConfirmation(Context context) {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    composition.deleteSounds();
                    deletedBtn.setEnabled(false);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Möchten Sie diesen Sound löschen?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nein", dialogClickListener).show();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.btn_play)
    public void play(View view) {

        composition.play();
        updateStatusOnPlay();
        ((PlayPauseView) view).toggle();

        // Toggle enabled and disabled state for recordBtn
        ColorStateList backgroundColor;
        ColorStateList imageColor;
        if (composition.isNotPlaying()) {

            backgroundColor = ColorStateList.valueOf(getResources().
                    getColor(R.color.color_primary));

            imageColor = ColorStateList.valueOf(getResources().
                    getColor(R.color.white));

        } else {

            backgroundColor = ColorStateList.valueOf(getResources().
                    getColor(R.color.grey_dark));

            imageColor = ColorStateList.valueOf(getResources().
                    getColor(R.color.grey_middle));
        }

        recordBtn.setBackgroundTintList(backgroundColor);
        recordBtn.setImageTintList(imageColor);

    }

    private void updateStatusOnPlay() {

        recordBtn.setEnabled(composition.isNotPlaying());

    }

    /**
     * This method is called, when the record-button is clicked.
     * It requests audio and storage permissions.
     */
    public void requestRecordingPermissions(View view) {
        // First: check if the permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //Permission is not granted. Show system permission dialog
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_PERMISSIONS_DECISIONS);
        } else {
            //Permission is granted. Start recording
            record();
        }
    }

    /**
     * This method is called, when the user interacts with the system dialog for permissions.
     * It handles the callback of audio and storage permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSIONS_DECISIONS: {
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    record(); // All permissions were granted! Start recording
                } else if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Only recording permission was granted
                    Toast.makeText(this,
                            this.getString(R.string.external_storage_permission_denied),
                            Toast.LENGTH_LONG).show();
                } else if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Only Storage permission was granted
                    Toast.makeText(this,
                            this.getString(R.string.record_audio_permission_denied),
                            Toast.LENGTH_LONG).show();
                } else {
                    // All permissions were denied.
                    Toast.makeText(this,
                            this.getString(R.string.all_audio_permissions_denied),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void record() {

        try {

            startRecording();

            //TODO warum braucht er lange bis er stoppt? sauberes runnable mit auto stop?
            recordTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {

                        // do your work right here
                        try {
                            if (recording) { // Die Aufnahme laueft weiter, wenn man nicht pausieren moechte.
                                composition.checkLimits(soundLength, startPositionInWidth);
                                soundLength += 3;
                                // Aufnahme Animation
                                composition.updateSoundView();
                            }

                        } catch (Throwable e) {

                            handleErrorOnRecording(e.getMessage());

                        }
                    });
                }
            }, 0, 50);
        } catch (Throwable t) {

            handleErrorOnRecording(t.getMessage());

        }

    }


    private void handleErrorOnRecording(String message) {

        recording = false;
        resetEditorValues();

        Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show();

        Log.e(TAG, message);


    }

    private void startRecording() throws Throwable {

        if (recording) {

            resetEditorValues();

            composition.finishRecording(soundLength, startPositionInWidth);

        } else {

            startPositionInWidth = composition.createSoundView(this);

            setEditorValues();

        }
    }


    private void resetEditorValues() {

        recordTimer.cancel();

        recording = false;

        playBtn.setEnabled(true);

    }


    private void setEditorValues() {

        soundLength = 0;

        playBtn.setEnabled(false);

        recording = true;

        recordTimer = new Timer();

        handler = new Handler();

    }

}
