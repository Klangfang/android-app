package com.wfm.soundcollaborations.Editor.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundRecordingTimeException;
import com.wfm.soundcollaborations.fragments.ComposeFragment;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.PickResponse;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.Editor.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;
import com.wfm.soundcollaborations.common.NetworkActivity;
import com.wfm.soundcollaborations.webservice.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Platzhalter für UI und Zusammenspiel mit der Compositionlogik.
 */
public class EditorActivity extends AppCompatActivity {

    private static final String TAG = EditorActivity.class.getSimpleName();
    @BindView(R.id.composition)
    CompositionView compositionView;

    private CompositionBuilder builder;
    private Handler handler;
    private RelativeLayout.LayoutParams layoutParams;
    private int width = 0;

    //private boolean isPlaying = false;
    private boolean recording;
    private boolean strobo;

    private SoundView soundView;

    private Timer recordTimer = new Timer();

    @BindView(R.id.btn_delete)
    Button deletedBtn;

    @BindView(R.id.btn_record)
    Button recordBtn;

    @BindView(R.id.btn_play)
    PlayPauseView playBtn;

    private Integer startPositionInWidth=null;
    private Integer soundLength=null;

    /**
     * This constant creates a placeholder for the user's consent of the record audio permission.
     * It will be used when handling callback from the runtime permission (onRequestPermissionsResult)
     */
    private final int RECORD_AUDIO_PERMISSIONS_DECISIONS = 1;


    private PickResponse response;
    private CompositionServiceClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        // create soundViews to be added to the corresponding sounds
        // let SoundDownloader update these views using listener
        // when a view finished downloading it add itself to the track
        // when all sounds are loaded the CompositionOverview will be ready to play the sounds
        builder = new CompositionBuilder(compositionView, 4);
        Intent intent = getIntent();
        String compositionJson = intent.getStringExtra(ComposeFragment.PICK_RESPONSE);
        // create new composition has no json response
        if (StringUtils.isNoneBlank(compositionJson)) {
            response = JsonUtil.fromJson(compositionJson, PickResponse.class);
            if (response != null) {
                builder.addSounds(response.composition);
            }
        }

        deletedBtn.setOnClickListener(delBtnview -> deleteConfirmation(delBtnview.getContext()));

        client = new CompositionServiceClient(compositionView.getContext());
    }


    @Override
    protected void onDestroy() {

        if (response != null && response.composition != null) {
            client.release(response.composition.id);
        }
        super.onDestroy();
    }


    private void deleteConfirmation(Context context) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    builder.deleteSounds();
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

    private void selectSound(SoundView soundView) {
       builder.selectSound(soundView);
       deletedBtn.setEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.btn_play)
    public void play(View view)
    {
        builder.play();
        updateStatusOnPlay();
        ((PlayPauseView) view).toggle();
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
            soundView = builder.getRecordSoundView(this);
            layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();

            // Stop recording
            if (recording) {
                resetEditorValues();
                builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
            } else {
                // Beim Zeitlimit oder bei einer Ueberlappung keine Aufnahme starten.
                builder.checkLimits(soundView, soundLength, startPositionInWidth);

                // Create sound view listener
                soundView.setOnLongClickListener(clickView -> {
                    float xPosition = clickView.getX();
                    Log.v("long clicked", "pos: " + xPosition);
                    if (builder.isSelectedSound((SoundView) clickView)) {
                        ((SoundView) clickView).setDefaultSoundColor();
                        deselectSound((SoundView) clickView);
                    } else {
                        ((SoundView) clickView).setSelectedSoundColor();
                        selectSound((SoundView) clickView);
                    }
                    clickView.invalidate();
                    return true;
                });

                restartTimer();

                boolean isNewRecording = startRecord();

                if (isNewRecording) {

                    recordTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // do your work right here

                                    try {

                                        builder.checkLimits(soundView, soundLength, startPositionInWidth);

                                        if (recording) { // Die Aufnahme laueft weiter, wenn man nicht pausieren moechte.
                                            // Aufnahme Animation
                                            layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
                                            width = width + 3;
                                            soundLength = width;
                                            layoutParams.width = width;
                                            soundView.setLayoutParams(layoutParams);
                                            int max = builder.getActiveTrack().getMaxAmplitude();
                                            soundView.addWave(max);
                                            Log.d(TAG, "Max Amplitude Recieved -> " + max);
                                            soundView.invalidate();
                                            builder.getCompositionView().increaseScrollPosition(3);
                                            builder.getCompositionView().increaseViewWatchPercentage(soundView.getTrackNumber(), 0.17f);
                                        }

                                    } catch (SoundWillBeOutOfCompositionException e) {
                                        recording = false;
                                        resetEditorValues();
                                        builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
                                    } catch (SoundWillOverlapException e) {
                                        recording = false;
                                        resetEditorValues();
                                        builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
                                    } catch (SoundRecordingTimeException ex) {
                                        recording = false;
                                        resetEditorValues();
                                        builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
                                    } catch (Exception e) {
                                        recording = false;
                                        resetEditorValues();
                                        builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
                                    }
                                }
                            });
                        }
                    }, 0, 50);
                }
            }
        } catch (NoActiveTrackException ex) {
            Toast.makeText(this, "Please select Track!", Toast.LENGTH_LONG).show();
        } catch (RecordTimeOutExceededException ex) {
            resetEditorValues();
            builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
        } catch (SoundWillOverlapException ex) {

        } catch (SoundWillBeOutOfCompositionException ex) {
        } catch (SoundRecordingTimeException ex) {
            resetEditorValues();
            builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void deselectSound(SoundView soundView) {
        deletedBtn.setEnabled(!builder.deselectSound(soundView));
    }

    private boolean startRecord() throws RecordTimeOutExceededException {

        // Clicking record while not recording
        RelativeLayout.LayoutParams soundParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
        startPositionInWidth = soundParams.leftMargin;
        layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();

        recording = true;

        // disable play button
        playBtn.setEnabled(false);

        // deaktiviere Cursor
        compositionView.deactivate();

        handler = new Handler();


        // Start recorder
        builder.getActiveTrack().startTrackRecorder();

        return true;
    }

    private void updateStatusOnPlay() {

        recordBtn.setEnabled(!builder.getPlayStatus());
    }

    private void resetEditorValues() {

        // restart timer
        restartTimer();

        // Aufgenommenen Sound zum Track hinzufuegen

        // Reactivate scrolling
        //compositionView.activate();

        // set recording flag
        recording = false;

        // enable play button again
        playBtn.setEnabled(true);

        layoutParams = null;

    }

    private void restartTimer() {
        compositionView.activate();
        recordTimer.cancel();
        recordTimer = new Timer();
        width = 0;
    }

}
