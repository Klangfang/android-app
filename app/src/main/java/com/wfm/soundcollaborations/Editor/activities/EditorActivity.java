package com.wfm.soundcollaborations.Editor.activities;

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
import com.wfm.soundcollaborations.Editor.model.composition.Track;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.Editor.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.Editor.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.JSONUtils;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        } catch (NoActiveTrackException ex) {
            Toast.makeText(this, "Please select Track!", Toast.LENGTH_LONG).show();
        } catch (SoundWillOverlapException ex2) {
            Toast.makeText(this, "Recording will overlap with other sounds!", Toast.LENGTH_LONG).show();
        } catch (SoundWillBeOutOfCompositionException ex) {
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();

        restartTimer();

        Track activeTrack = builder.getActiveTrack();
        boolean isNewRecording = startRecord(activeTrack);

        if (isNewRecording) {
            recordTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // do your work right here

                            try {

                                checkRecordLimit();

                                if (recording) { // Die Aufnahme laueft weiter, wenn man nicht pausieren moechte.
                                    // Aufnahme Animation
                                    layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
                                    width = width + 3;
                                    soundLength = width;
                                    layoutParams.width = width;
                                    soundView.setLayoutParams(layoutParams);
                                    int max = activeTrack.getMaxAmplitude();
                                    soundView.addWave(max);
                                    Log.d(TAG, "Max Amplitude Recieved -> " + max);
                                    soundView.invalidate();
                                    builder.getCompositionView().increaseScrollPosition(3);
                                    builder.getCompositionView().increaseViewWatchPercentage(soundView.getTrack(), 0.17f);
                                }

                            } catch (SoundWillBeOutOfCompositionException e) {

                            } catch (Exception e) {
                                //Toast.makeText(this, "30 second of recording is reached!", Toast.LENGTH_LONG).show();
                                stopRecording();
                            }
                        }
                    });
                }
            }, 0, 50);
        }
    }

    private boolean startRecord(Track activeTrack) {
        // Beim Zeitlimit oder bei einer Ueberlappung keine Aufnahme starten.
        if (isLimitReached() || isOverlapping()) {
            return false;
        }

        // Clicking record while recording
        if (recording) {
            stopRecording();
            prepareRecordedSounds();
            return false;
        }

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

        recordedSoundPath = activeTrack.getRecordedFilePath();

        return true;
    }

    private boolean isLimitReached() {
        int pos = this.compositionView.getScrollPosition();
        if((pos + builder.getSoundSecondWidth()) > builder.getTrackWidth()) {
            Toast.makeText(this, "Recording will be out of composition!", Toast.LENGTH_LONG).show();
            return true;
        }
        if (builder.isRecorderStoped()) {
            Toast.makeText(this, "30 second of recording is reached!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean isOverlapping() {

        // cancel recorders on overlapping and return true;
        if ((builder != null && layoutParams != null && soundView != null) &&
                builder.isThereAnyOverlapping(layoutParams.leftMargin + layoutParams.width)) {

            Toast.makeText(this, "Can't record while overlapping with other sounds!", Toast.LENGTH_LONG).show();

            return true;
        }

        return false;
    }

    private void updateStatusOnPlay() {

        recordBtn.setEnabled(!builder.getPlayStatus());
    }

    private void checkRecordLimit() {
        if (isLimitReached() || isOverlapping()) {
            if (recording) {
                stopRecording();
                prepareRecordedSounds();
            }
        }

        strobo = !strobo;
        recordBtn.setBackgroundColor(strobo ? getResources().getColor(R.color.red) : getResources().getColor(R.color.grey_light));
    }

    private void stopRecording() {

        // Stop recorder
        builder.stopTrackRecorder(soundLength);

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

    private void prepareRecordedSounds() {
        if (recordedSoundPath != null && soundLength!=null && startPositionInWidth != null) {
            builder.addRecordedSound(recordedSoundPath, soundLength, soundView.getTrack(), startPositionInWidth);
        }
    }

}
