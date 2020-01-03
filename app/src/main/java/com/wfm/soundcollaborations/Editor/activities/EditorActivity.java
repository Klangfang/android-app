package com.wfm.soundcollaborations.Editor.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.IBinder;
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
import com.wfm.soundcollaborations.Editor.model.composition.CompositionService;
import com.wfm.soundcollaborations.Editor.model.composition.StopReason;
import com.wfm.soundcollaborations.Editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.KlangfangSnackbar;
import com.wfm.soundcollaborations.fragments.ComposeFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();

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
    boolean mBounded;
    CompositionService compositionService;

    BroadcastReceiver editorBroadCastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        init();

        createReceiver();

        registerReceiver(editorBroadCastReceiver, new IntentFilter(CompositionService.NOTIFICATION));

        createCompositionService();

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

        compositionService.requestCancel();

        unbindService(mServiceConnection);

        unregisterReceiver(editorBroadCastReceiver);

    }


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
        deletedBtn.setOnClickListener(delBtnview -> deleteConfirmation(delBtnview.getContext()));

    }


    private void createCompositionService() {

        Intent intent = getIntent();
        String compositionResponse = intent.getStringExtra(ComposeFragment.PICK_RESPONSE);
        if (StringUtils.isNotBlank(compositionResponse)) {

            create = false;

            Intent intent1 = new Intent(this, CompositionService.class);
            intent1.putExtra("CompositionResponse", compositionResponse);

            startService(intent1);
            bindService(intent1, mServiceConnection, BIND_AUTO_CREATE);

        } else {

            String compositionTitle = intent.getStringExtra(String.valueOf(R.id.composition_title_textfield));

            Intent intent1 = new Intent(this, CompositionService.class);
            intent1.putExtra(String.valueOf(R.id.composition_title_textfield), compositionTitle);

            startService(intent1);
            bindService(intent1, mServiceConnection, BIND_AUTO_CREATE);

            create = true;

        }

        compositionView.setOnScrollChanged(position -> {

            int milliseconds = (int) (position * 16.6666);
            compositionService.seek(milliseconds);
            Log.d(TAG, String.format("Milliseconds =>  %d position => %d", milliseconds, position));

        });

    }


    private void startOrStopRecording() {

        if (compositionService.isRecording()) {

            stopRecording();

        } else {

            startRecording();

        }

    }


    private void startRecording() {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        int scrollPositionInDP = compositionView.getScrollPosition();

        try {

            if (compositionService.canRecord(activeTrackIndex, scrollPositionInDP)) {

                compositionView.addLocalSoundView(this);

                compositionService.startRecording(activeTrackIndex, scrollPositionInDP, this::simulateRecording);

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

        compositionService.stopRecording();

    }


    /**
     * Work to do when sound recording is completed
     */
    void completeLocalSound() {

        try {

            String uuid = compositionView.completeLocalSoundView();

            int activeTrackIndex = compositionView.getActiveTrackIndex();
            int scrollPositionInDP = compositionView.getScrollPosition();

            compositionService.completeLocalSound(activeTrackIndex, scrollPositionInDP, uuid);

            handleStop(false, StopReason.FINISH_RECORDING);

        } catch (Throwable t) {
            handleErrorOnRecording(t);
        }

    }


    /**
     * Simulates recording session
     */
    public void simulateRecording() {

        StopReason stopReason = StopReason.UNKNOWN;

        try {

            int activeTrackIndex = compositionView.getActiveTrackIndex();
            int scrollPosition = compositionView.getScrollPosition();

            stopReason = compositionService.simulateRecording(activeTrackIndex, scrollPosition);

        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

        if (!stopReason.equals(StopReason.NO_STOP)) {

            handleStop(false, stopReason);

        }

    }


    /**
     * Adds remote sounds of loaded composition
     */
    public void addRemoteSoundView(RemoteSound remoteSound) {

        try {

            compositionView.addRemoteSoundView(this, remoteSound);

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
     */
    public void updateTrackWatches(int trackNumber, int soundWidths) {

        try {

            compositionView.updateTrackWatches(trackNumber, soundWidths);

        } catch (Throwable t) {

            handleErrorOnRecording(t);
        }

    }


    @OnClick(R.id.btn_play)
    public void play(View view) {

        switchState(true);
        compositionService.playOrPause(pressPlay, this::increaseScrollPosition, this::handleStop);
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

            if (compositionService.isRecording()) {

                KlangfangSnackbar.shortShow(compositionView, "Please cancel recording before continuing.");

                doNothing = true;

            } else {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.release_composition) {

                    if (create) {

                        compositionService.requestCreate();

                    } else {

                        compositionService.requestJoin();

                    }

                } else if (itemId == CANCEL_BTN_ID) {

                    cancelConfirmation(compositionView.getContext());

                    doNothing = true;

                }
            }
        } catch (Throwable t) {

            handleErrorOnRecording(t);

        }

        return doNothing;

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
    private void deleteConfirmation(Context context) {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Möchten Sie diesen Sound löschen?").setPositiveButton("Ja", dialogClickListener)
                .setNegativeButton("Nein", dialogClickListener).show();

    }


    private void cancelConfirmation(Context context) {

        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Ihre Änderungen werden verworfen. Möchten Sie wirklich fortfahren?")
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

            compositionService.deleteSounds(soundUUIDs);

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


    /**
     * Creates a new activity broadcast message receiver
     */
    private void createReceiver() {

        if (editorBroadCastReceiver == null) {

            editorBroadCastReceiver = new EditorBroadcastReceiver();

        }

    }


    /**
     * Handles composition service connections
     */
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

            KlangfangSnackbar.shortShow(compositionView, "Service is disconnected");
            mBounded = false;
            compositionService = null;

        }


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            KlangfangSnackbar.shortShow(compositionView, "Service is connected");
            mBounded = true;
            CompositionService.CompositionServiceBinder mLocalBinder = (CompositionService.CompositionServiceBinder) service;
            compositionService = mLocalBinder.getService();

        }

    };

}
