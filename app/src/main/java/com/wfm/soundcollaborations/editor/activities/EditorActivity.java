package com.wfm.soundcollaborations.editor.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.KlangfangApp;
import com.wfm.soundcollaborations.KlangfangSnackbar;
import com.wfm.soundcollaborations.MainActivity;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.editor.EditorComponent;
import com.wfm.soundcollaborations.editor.model.composition.EditorViewModel;
import com.wfm.soundcollaborations.editor.model.composition.StopReason;
import com.wfm.soundcollaborations.editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.fragments.ComposeFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();

    public static final String NOTIFICATION = "com.wfm.soundcollaborations.Editor.activities";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String START_MAIN_ACTIVITY = "START_MAIN_ACTIVITY";


    /**
     * This constant creates a placeholder for the user's consent of the startOrStopRecording audio permission.
     * It will be used when handling callback from the runtime permission (onRequestPermissionsResult)
     */
    private static final int RECORD_AUDIO_PERMISSIONS_DECISIONS = 1;

    private static final int CANCEL_BTN_ID = 16908332;

    @BindView(R.id.composition)
    public CompositionView compositionView;

    @BindView(R.id.btn_delete)
    ImageButton deletedBtn;

    @BindView(R.id.btn_record)
    FloatingActionButton recordBtn;

    @BindView(R.id.btn_play)
    PlayPauseView playBtn;
    private boolean pressPlay;

    private boolean create;

    EditorComponent editorComponent;

    @Inject
    EditorViewModel editorViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        init();

        editorComponent = ((KlangfangApp) getApplicationContext())
                .appComponent
                .editorComponent()
                .create();

        editorComponent.inject(this);

        prepareEditorViewModel();

        Log.d(TAG, "Activity is created");

    }


    @Override
    protected void onStart() {

        super.onStart();

        Log.d(TAG, "Activity is started");

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        preDestroy();

        Log.d(TAG, "Activity is destroyed");

    }


    @Override
    protected void onStop() {

        super.onStop();

        Log.d(TAG, "Activity is stopped");

    }


    public void preDestroy() {

        if (editorViewModel.isCompositionNotCanceled()) {

            editorViewModel.requestCancel();

        }

    }


    @Override
    public void onBackPressed() {

        // do nothing here

    }


    private void init() {

        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.base_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(CreateCompositionActivity.compositionTitleInput);

        deletedBtn.setEnabled(false);
        deletedBtn.setOnClickListener(this::deleteConfirmation);

    }


    private void prepareEditorViewModel() {

        editorViewModel.setCallBack(this::startMainActivity);

        Intent intent = getIntent();
        String compositionResponse = intent.getStringExtra(ComposeFragment.PICK_RESPONSE);
        if (StringUtils.isNotBlank(compositionResponse)) {

            create = false;

            List<RemoteSound> remoteSounds = editorViewModel.loadComposition(getApplicationContext(), compositionResponse);
            addRemoteSoundViews(remoteSounds);


        } else {

            String compositionTitle = intent.getStringExtra(String.valueOf(R.id.composition_title_textfield));
            editorViewModel.createNewComposition(compositionTitle);

            create = true;

        }

        compositionView.setOnScrollChanged(position -> {

            int milliseconds = (int) (position * 16.6666);
            editorViewModel.seek(milliseconds);
            Log.d(TAG, String.format("Milliseconds =>  %d position => %d", milliseconds, position));

        });

    }


    private void startOrStopRecording() {

        if (editorViewModel.isRecording()) {

            stopRecording();

        } else {

            startRecording();

        }

    }


    private void startRecording() {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        int scrollPositionInDP = compositionView.getScrollPosition();

        try {

            if (editorViewModel.canRecord(activeTrackIndex, scrollPositionInDP)) {

                compositionView.addLocalSoundView(this);

                editorViewModel.startRecording(activeTrackIndex, scrollPositionInDP, this::simulateRecording);

                playBtn.setEnabled(false);
                setRecordBtnColors(false, true);


            } else {

                String text = "Could not start recording at this position.\n" +
                        "The composition may has been exhausted.";

                KlangfangSnackbar.longShow(compositionView, text);

            }

        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

    }


    private void stopRecording() {

        editorViewModel.stopRecording();
        completeLocalSound();

    }


    /**
     * Work to do when sound recording is completed
     */
    void completeLocalSound() {

        try {

            String uuid = compositionView.completeLocalSoundView();

            int activeTrackIndex = compositionView.getActiveTrackIndex();
            int scrollPositionInDP = compositionView.getScrollPosition();

            editorViewModel.completeLocalSound(getApplicationContext(), activeTrackIndex, scrollPositionInDP, uuid);

            handleStop(false, StopReason.COMPLETE_RECORDING);

        } catch (Throwable t) {
            handleErrorOnRecording(t);
        }

    }


    /**
     * Simulates recording session
     */
    public void simulateRecording() {

        try {

            int activeTrackIndex = compositionView.getActiveTrackIndex();
            int scrollPosition = compositionView.getScrollPosition();

            if (editorViewModel.checkStop(activeTrackIndex, scrollPosition).equals(StopReason.NO_STOP)) {

                int maxAmplitude = editorViewModel.getMaxAmplitude(activeTrackIndex);
                updateSoundView(maxAmplitude);

            } else {

                completeLocalSound();

            }

        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

    }


    /**
     * Adds remote sounds of loaded composition
     */
    public void addRemoteSoundViews(List<RemoteSound> remoteSounds) {

        try {

            for (RemoteSound remoteSound : remoteSounds) {

                compositionView.addRemoteSoundView(this, remoteSound);

            }

        } catch (Throwable t) {
            handleErrorOnRecording(t);
        }

    }


    /**
     * Puts new sound waves as result of recording process
     */
    public void updateSoundView(int maxAmplitude) {

        try {

            compositionView.updateSoundView(maxAmplitude);

        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

    }


    /**
     * Increases track watches after deleting some sounds
     * int trackNumber, int soundWidths
     */
    public void updateTrackWatches(Map<Integer, Integer> values) {

        try {

            for (Map.Entry<Integer, Integer> entry : values.entrySet()) {

                compositionView.updateTrackWatches(entry.getKey(), entry.getValue());

            }


        } catch (Throwable t) {

            handleErrorOnRecording(t);
        }

    }


    @OnClick(R.id.btn_play)
    public void play(View view) {

        switchState(true);
        editorViewModel.playOrPause(pressPlay, this::increaseScrollPosition, this::handleStop);
        compositionView.enable(!pressPlay);

    }


    /**
     * Requests audio and storage permissions.
     */
    @OnClick(R.id.btn_record)
    public void record(View view) {

        boolean permissionNotGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
        if (permissionNotGranted) {

            // Show system permission dialog
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_PERMISSIONS_DECISIONS);

        } else {

            startOrStopRecording();

        }

    }


    /**
     * This method is called, when the user interacts with the system dialog for permissions.
     * It handles the callback of audio and storage permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == RECORD_AUDIO_PERMISSIONS_DECISIONS) {

            boolean allPermissionsGranted = grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED;
            boolean recordingPermissionGranted = grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            boolean storagePermissionGranted = grantResults.length > 1
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED;

            if (allPermissionsGranted) {

                startOrStopRecording();

            } else if (recordingPermissionGranted) {

                KlangfangSnackbar.longShow(compositionView,
                        this.getString(R.string.external_storage_permission_denied));

            } else if (storagePermissionGranted) {

                KlangfangSnackbar.longShow(compositionView,
                        this.getString(R.string.record_audio_permission_denied));

            } else {

                KlangfangSnackbar.longShow(compositionView,
                        this.getString(R.string.all_audio_permissions_denied));

            }

        }

    }


    /**
     * Adds menu to toolbar
     * This is a custom implementation of
     * {@link android.view.Window.Callback#onCreatePanelMenu}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return true;

    }


    /**
     * Event listener for menu item
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {

        boolean doNothing = super.onOptionsItemSelected(menuItem);

        try {

            if (editorViewModel.isRecording()) {

                KlangfangSnackbar.shortShow(compositionView, "Please cancel recording before continuing.");

                doNothing = true;

            } else {


                enableAll(false);

                int itemId = menuItem.getItemId();
                if (itemId == R.id.release_composition) {

                    if (create) {

                        editorViewModel.requestCreate();

                    } else {

                        editorViewModel.requestJoin();

                    }

                } else if (itemId == CANCEL_BTN_ID) {

                    cancelConfirmation();

                    doNothing = true;

                }
            }
        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

        return doNothing;

    }


    private void enableAll(boolean enable) {

        compositionView.enable(enable);
        playBtn.setEnabled(enable);
        recordBtn.setEnabled(enable);
        findViewById(R.id.release_composition).setEnabled(enable);
        findViewById(R.id.base_toolbar).setEnabled(enable); // TODO funktioniert nicht

    }


    /**
     * Switches all button states and sets record button default colors.
     */
    private void switchState(boolean togglePlay) {

        recordBtn.setEnabled(pressPlay);

        pressPlay = !pressPlay;

        if (togglePlay) {
            playBtn.toggle();
        }

        setRecordBtnColors(pressPlay, false);

    }


    private void setRecordBtnColors(boolean pressPlay, boolean recording) {

        //TODO define in CommonUtil as map maybe
        ColorStateList RECORD_ENABLED_BACKGROUND = getColorStateList(R.color.color_primary);
        ColorStateList RECORD_ENABLED_BACKGROUND_2 = getColorStateList(R.color.color_accent);
        ColorStateList RECORD_DISABLED_BACKGROUND = getColorStateList(R.color.grey_dark);
        ColorStateList RECORD_ENABLED_IMAGE = getColorStateList(R.color.white);
        ColorStateList RECORD_ENABLED_IMAGE_2 = getColorStateList(R.color.color_error);
        ColorStateList RECORD_DISABLED_IMAGE = getColorStateList(R.color.grey_middle);

        ColorStateList backgroundColor;
        ColorStateList imageColor;

        if (recording) {

            backgroundColor = RECORD_ENABLED_BACKGROUND_2;
            imageColor = RECORD_ENABLED_IMAGE_2;

        } else {
            if (pressPlay) {

                backgroundColor = RECORD_DISABLED_BACKGROUND;
                imageColor = RECORD_DISABLED_IMAGE;

            } else {

                backgroundColor = RECORD_ENABLED_BACKGROUND;
                imageColor = RECORD_ENABLED_IMAGE;

            }
        }


        recordBtn.setBackgroundTintList(backgroundColor);
        recordBtn.setImageTintList(imageColor);

    }


    /**
     * Asks user for delete confirmation. When the user accepts, all selected local sounds will be delete
     * {@link EditorActivity#deleteSounds()}
     */
    private void deleteConfirmation(View deleteBtnView) {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    deleteSounds();
                    deletedBtn.setEnabled(false);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        new AlertDialog.Builder(deleteBtnView.getContext())
                .setCancelable(false)
                .setMessage("Möchten Sie diesen Sound löschen?")
                .setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nein", dialogClickListener)
                .show();

    }


    /**
     * Asks user for cancel confirmation before canceling the composition
     */
    private void cancelConfirmation() {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    enableAll(true);
                    break;
            }
        };

        new AlertDialog.Builder(compositionView.getContext())
                .setCancelable(false)
                .setMessage("Ihre Änderungen werden verworfen. Möchten Sie wirklich fortfahren?")
                .setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nein", dialogClickListener)
                .show();
    }


    /**
     * Deletes all selected local sounds
     */
    private void deleteSounds() {

        try {

            List<String> soundUUIDs = compositionView.deleteSoundViews();

            Map<Integer, Integer> valuesToRecover = editorViewModel.deleteSounds(soundUUIDs);
            updateTrackWatches(valuesToRecover);

        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

    }


    /**
     * Resets all values to default and loges error reason
     */
    void handleErrorOnRecording(Throwable t) {

        reset(false, StopReason.UNKNOWN);

        KlangfangSnackbar.longShow(compositionView, t.getMessage());

        Log.e(TAG, t.getMessage(), t);


    }


    /**
     * Same as {@link EditorActivity#deleteSounds()}. This is called when sound recording is stopped
     */
    public void handleStop(boolean togglePlay, StopReason stopReason) {

        reset(togglePlay, stopReason);

        String message = stopReason.getReason();
        KlangfangSnackbar.longShow(compositionView, message);

        Log.d(TAG, message);

    }


    /**
     * Handles stop when end of composition is reached
     */
    public void handleStop() {

        handleStop(true, StopReason.COMPOSITION_END_REACHED);

    }


    /**
     * Resets activity default values
     */
    private void reset(boolean togglePlay, StopReason stopReason) {

        if (stopReason.equals(StopReason.COMPOSITION_END_REACHED)) {

            compositionView.setScrollPosition(0);

        }

        compositionView.enable(true);

        playBtn.setEnabled(true);
        pressPlay = true;
        recordBtn.setEnabled(true);

        switchState(togglePlay);

    }


    public void increaseScrollPosition() {

        compositionView.increaseScrollPosition();

    }


    private void startMainActivity(String text) {

        super.finish();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(MESSAGE_TEXT, text);

        startActivity(intent);

    }

}
