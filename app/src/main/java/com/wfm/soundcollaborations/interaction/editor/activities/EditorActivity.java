package com.wfm.soundcollaborations.interaction.editor.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Preconditions;

import com.wfm.soundcollaborations.KlangfangApp;
import com.wfm.soundcollaborations.KlangfangSnackbar;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.databinding.ActivityEditorBinding;
import com.wfm.soundcollaborations.interaction.editor.EditorComponent;
import com.wfm.soundcollaborations.interaction.editor.model.composition.EditorViewModel;
import com.wfm.soundcollaborations.interaction.editor.model.composition.StopReason;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.interaction.editor.service.CompositionRecoverService;
import com.wfm.soundcollaborations.interaction.main.MainActivity;
import com.wfm.soundcollaborations.interaction.main.fragments.ComposeFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();

    public static final String NOTIFICATION = "com.wfm.soundcollaborations.Editor.activities";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";
    public static final String START_MAIN_ACTIVITY = "START_MAIN_ACTIVITY";

    private ActivityEditorBinding binding;

    /**
     * This constant creates a placeholder for the user's consent of the startOrStopRecording audio permission.
     * It will be used when handling callback from the runtime permission (onRequestPermissionsResult)
     */
    private static final int RECORD_AUDIO_PERMISSIONS_DECISIONS = 1;

    private static final int CANCEL_BTN_ID = 16908332;

    private boolean pressPlay;

    private boolean create;

    EditorComponent editorComponent;

    @Inject
    EditorViewModel editorViewModel;

    boolean mBounded;
    CompositionRecoverService compositionRecoverService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnPlay.setOnClickListener(view -> play(view));
        binding.btnRecord.setOnClickListener(view -> record(view));

        editorComponent = ((KlangfangApp) getApplicationContext())
                .appComponent
                .editorComponent()
                .create();

        editorComponent.inject(this);

        prepareEditorViewModel();

        setSupportActionBar(findViewById(R.id.base_toolbar));
        ActionBar actionBar = getSupportActionBar();
        Preconditions.checkNotNull(actionBar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(editorViewModel.getCompositionTitle());

        binding.btnDelete.setEnabled(false);
        binding.btnDelete.setOnClickListener(this::deleteConfirmation);

        prepareCompositionRecoverService();

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

        if (mBounded) {
            unbindService(mServiceConnection);
        }

        binding = null;

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

        binding.composition.setOnScrollChanged(position -> {

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

        int activeTrackIndex = binding.composition.getActiveTrackIndex();
        int scrollPositionInDP = binding.composition.getScrollPosition();

        try {

            if (editorViewModel.canRecord(activeTrackIndex, scrollPositionInDP)) {

                binding.composition.addLocalSoundView(this);

                editorViewModel.startRecording(activeTrackIndex, scrollPositionInDP, this::simulateRecording);

                binding.btnPlay.setEnabled(false);
                setRecordBtnColors(false, true);


            } else {

                String text = "Could not start recording at this position.\n" +
                        "The selected track may has been exhausted.";

                KlangfangSnackbar.longShow(binding.composition, text);

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

            String uuid = binding.composition.completeLocalSoundView();

            int activeTrackIndex = binding.composition.getActiveTrackIndex();
            int scrollPositionInDP = binding.composition.getScrollPosition();

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

            int activeTrackIndex = binding.composition.getActiveTrackIndex();
            int scrollPosition = binding.composition.getScrollPosition();

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

                binding.composition.addRemoteSoundView(this, remoteSound);

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

            binding.composition.updateSoundView(maxAmplitude);

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

                binding.composition.updateTrackWatches(entry.getKey(), entry.getValue());

            }


        } catch (Throwable t) {

            handleErrorOnRecording(t);
        }

    }


    public void play(View view) {

        switchState(true);
        editorViewModel.playOrPause(pressPlay, this::increaseScrollPosition, this::handleStop);
        binding.composition.enable(!pressPlay);

    }


    /**
     * Requests audio and storage permissions.
     */
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

                KlangfangSnackbar.longShow(binding.composition,
                        this.getString(R.string.external_storage_permission_denied));

            } else if (storagePermissionGranted) {

                KlangfangSnackbar.longShow(binding.composition,
                        this.getString(R.string.record_audio_permission_denied));

            } else {

                KlangfangSnackbar.longShow(binding.composition,
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

                KlangfangSnackbar.shortShow(binding.composition, "Please cancel recording before continuing.");

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

        binding.composition.enable(enable);
        binding.btnPlay.setEnabled(enable);
        binding.btnRecord.setEnabled(enable);
        findViewById(R.id.release_composition).setEnabled(enable);
        findViewById(R.id.base_toolbar).setEnabled(enable); // TODO funktioniert nicht

    }


    /**
     * Switches all button states and sets record button default colors.
     */
    private void switchState(boolean togglePlay) {

        binding.btnRecord.setEnabled(pressPlay);

        pressPlay = !pressPlay;

        if (togglePlay) {
            binding.btnPlay.toggle();
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


        binding.btnRecord.setBackgroundTintList(backgroundColor);
        binding.btnRecord.setImageTintList(imageColor);

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
                    binding.btnDelete.setEnabled(false);
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

        new AlertDialog.Builder(binding.composition.getContext())
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

            List<String> soundUUIDs = binding.composition.deleteSoundViews();

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

        KlangfangSnackbar.longShow(binding.composition, t.getMessage());

        Log.e(TAG, t.getMessage(), t);


    }


    /**
     * Same as {@link EditorActivity#deleteSounds()}. This is called when sound recording is stopped
     */
    public void handleStop(boolean togglePlay, StopReason stopReason) {

        reset(togglePlay, stopReason);

        String message = stopReason.getReason();
        KlangfangSnackbar.longShow(binding.composition, message);

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

            binding.composition.setScrollPosition(0);

        }

        binding.composition.enable(true);

        binding.btnPlay.setEnabled(true);
        pressPlay = true;
        binding.btnRecord.setEnabled(true);

        switchState(togglePlay);

    }


    public void increaseScrollPosition() {

        binding.composition.increaseScrollPosition();

    }


    private void startMainActivity(String text) {


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(MESSAGE_TEXT, text);

        setResult(RESULT_OK, intent);
        finish();

        if (editorViewModel.getCompositionId() == null) {
            startActivity(intent);
        }

    }


    public void prepareCompositionRecoverService() {

        Long compositionId = editorViewModel.getCompositionId();

        if (Objects.nonNull(compositionId)) {

            Intent intent = new Intent(this, CompositionRecoverService.class);
            intent.putExtra("COMPOSITION_ID", compositionId);

            startService(intent);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        }

    }


    /**
     * Handles composition service connections
     */
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            KlangfangSnackbar.shortShow(binding.composition, "Service is disconnected");
            mBounded = false;
            compositionRecoverService = null;

        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            KlangfangSnackbar.shortShow(binding.composition, "Service is connected");
            mBounded = true;
            CompositionRecoverService.CompositionRecoverServiceBinder mLocalBinder = (CompositionRecoverService.CompositionRecoverServiceBinder) service;
            compositionRecoverService = mLocalBinder.getService();

        }

    };

}
