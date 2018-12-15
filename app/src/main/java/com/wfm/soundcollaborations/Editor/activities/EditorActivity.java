package com.wfm.soundcollaborations.Editor.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundRecordingTimeException;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.Editor.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.Editor.utils.JSONUtils;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;

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

    private String recordedSoundPath=null;
    private Integer startPositionInWidth=null;
    private Integer soundLength=null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        String jsonData = "{"
                + " 'uuid': '3423423-432434-43243241-33-22222',"
                + " 'sounds': ["
               // + "   {'length': 28260, 'track': 1, 'start_position': 0, 'link': "
               // + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/we_wish_you_a_merry_xmas.ogg'},"

               // + "   {'length': 29760, 'track': 2, 'start_position': 20000, 'link': "
               // + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/we_three_kings.ogg'},"

                //+ "   {'length': 30580, 'track': 3, 'start_position': 30000, 'link': "
                //+ "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/deck_the_halls.ogg'},"

                //+ "   {'length': 29100, 'track': 4, 'start_position': 20000, 'link': "
               // + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/jingle_bells.ogg'},"

                //+ "   {'length': 4920, 'track': 3, 'start_position': 40000, 'link': "
                //+ "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_heart_of_a_galaxy.ogg'},"

                + "   {'length': 30580, 'track': 1, 'start_position': 10000, 'link': "
                + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/solar_eclipse.ogg'},"

                + "   {'length': 30680, 'track': 2, 'start_position': 20000, 'link': "
                + "'https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg'}"

                + " ]"
                + "}";
        // create soundViews to be added to the corresponding tracks
        // let SoundDownloader update these views using listener
        // when a view finished downloading it add itself to the track
        // when all sounds are loaded the Composition will be ready to play the sounds
        builder = new CompositionBuilder(compositionView, 4);
        builder.addSounds(JSONUtils.getSounds(jsonData));

        deletedBtn.setOnClickListener(delBtnview -> deleteConfirmation(delBtnview.getContext()));
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

    @OnClick(R.id.btn_record)
    public void record(final View view)
    {

        try {
            soundView = builder.getRecordSoundView(this);
            layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();


            // Stop recording
            if (recording) {
                resetEditorValues();
                builder.prepareRecordedSound(soundView, soundLength, startPositionInWidth);
            }

            else {
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
                                            builder.getCompositionView().increaseViewWatchPercentage(soundView.getTrack(), 0.17f);
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
        } catch(SoundRecordingTimeException ex){
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
