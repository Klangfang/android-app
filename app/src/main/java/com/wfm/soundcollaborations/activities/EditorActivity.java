package com.wfm.soundcollaborations.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.model.composition.CompositionBuilder;
import com.wfm.soundcollaborations.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.utils.JSONUtils;
import com.wfm.soundcollaborations.views.composition.CompositionView;
import com.wfm.soundcollaborations.views.composition.SoundView;

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
    private AudioRecorder audioRecorder = new AudioRecorder();
    private Timer recordTimer = new Timer();

    @BindView(R.id.btn_record)
    Button recordBtn;

    @BindView(R.id.btn_play)
    PlayPauseView playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);
        String jsonData = "{"
                + " 'uuid': '3423423-432434-43243241-33-22222',"
                + " 'sounds': ["
                + "   {'length': 28260, 'track': 1, 'start_position': 0, 'link': "
                + "'https://www.firexweb.com/sounds/sound1.3gp'},"

                + "   {'length': 29760, 'track': 2, 'start_position': 0, 'link': "
                + "'https://www.firexweb.com/sounds/sound3.3gp'},"

                + "   {'length': 30580, 'track': 3, 'start_position': 0, 'link': "
                + "'https://www.firexweb.com/sounds/sound5.3gp'},"

                + "   {'length': 29100, 'track': 4, 'start_position': 20000, 'link': "
                + "'https://www.firexweb.com/sounds/sound8.3gp'},"

                + "   {'length': 4920, 'track': 3, 'start_position': 40000, 'link': "
                + "'https://www.firexweb.com/sounds/sound7.3gp'},"

                + "   {'length': 30580, 'track': 1, 'start_position': 50000, 'link': "
                + "'https://www.firexweb.com/sounds/sound2.3gp'},"

                + "   {'length': 30680, 'track': 2, 'start_position': 80000, 'link': "
                + "'https://www.firexweb.com/sounds/sound4.3gp'}"

                + " ]"
                + "}";
        // create soundViews to be added to the corresponding tracks
        // let SoundDownloader update these views using listener
        // when a view finished downloading it add itself to the track
        // when all sounds are loaded the Composition will be ready to play the sounds
        builder = new CompositionBuilder(compositionView, 4);
        builder.addSounds(JSONUtils.getSounds(jsonData));
    }

    @OnClick(R.id.btn_record)
    public void record(final View view)
    {
        initRecorders();

        // Beim Zeitlimit oder bei einer Ueberlappung keine Aufnahme starten.
        if (audioRecorder.getStatus().equals(AudioRecorderStatus.STOPED) || updateRecordersOnOverlapping()) {
            return;
        }
        // Clicking record while recording
        if (recording) {
            compositionView.activate();
            updateRecordButton();
            return;
        }

        // Clicking record while not recording
        compositionView.deactivate();
        handler = new Handler();
        audioRecorder.create();
        audioRecorder.start();
        recordTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // do your work right here

                    try {
                        updateRecordAvailability();
                        checkLimits();
                        if (recording) { // Die Aufnahme laueft, wenn man nicht pausieren moechte.
                            layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
                            width = width + 3;
                            layoutParams.width = width;
                            soundView.setLayoutParams(layoutParams);
                            int max = audioRecorder.getMaxAmplitude();
                            soundView.addWave(max);
                            Log.d(TAG, "Max Amplitude Recieved -> " + max);
                            soundView.invalidate();
                            builder.getCompositionView().increaseScrollPosition(3);
                            builder.getCompositionView().increaseViewWatchPercentage(soundView.getTrack(), 0.17f);
                        }

                    } catch (SoundWillBeOutOfCompositionException e) {
                    } catch (RecordTimeOutExceededException e) {
                    } catch (Exception e) {
                        //Toast.makeText(this, "30 second of recording is reached!", Toast.LENGTH_LONG).show();
                        initRecorders();
                    }
                }
            });
                }
            }, 0, 50);
    }

    private void checkLimits() throws SoundWillBeOutOfCompositionException, RecordTimeOutExceededException {
        int pos = this.compositionView.getScrollPosition();
        if((pos + builder.getSoundSecondWidth()) > builder.getTrackWidth()) {
            Toast.makeText(this, "Recording will be out of composition!", Toast.LENGTH_LONG).show();
            initRecorders();
            throw new SoundWillBeOutOfCompositionException();
        }
        if (audioRecorder.getStatus().equals(AudioRecorderStatus.STOPED)) {
            Toast.makeText(this, "30 second of recording is reached!", Toast.LENGTH_LONG).show();
            initRecorders();
            throw new RecordTimeOutExceededException();
        }
    }

    @OnClick(R.id.btn_play)
    public void play(View view)
    {
        builder.play();
        updateStatusOnPlay();
        ((PlayPauseView) view).toggle();
    }

    private boolean updateRecordersOnOverlapping() {

        // cancel recorders on overlapping and return true;
        if ((builder != null && layoutParams != null && soundView != null) &&
                builder.isThereAnyOverlapping(layoutParams.leftMargin + layoutParams.width, soundView.getTrack())) {
            initRecorders();

            Toast.makeText(this, "Can't record while overlapping with other sounds!", Toast.LENGTH_LONG).show();

            return true;
        }

        return false;
    }

    private void updateStatusOnPlay() {

        recordBtn.setEnabled(!builder.getPlayStatus());
    }

    private void updateRecordAvailability() {

        boolean playStatus = builder.getPlayStatus();
        boolean overlapping = updateRecordersOnOverlapping();

        // Recording ability is when the player is either recording or playing and there is no overlapping
        recording = (!playStatus && !overlapping);
        strobo = !strobo;
        recordBtn.setBackgroundColor(strobo ? getResources().getColor(R.color.red) : getResources().getColor(R.color.grey_light));

        // Play button is activated when it is not recording
        playBtn.setEnabled(!recording);
    }

    private void updateRecordButton() {

        updateRecordAvailability();

        // Record button is activated when it is not recording and the player is not playing and there is no overlapping
        //recordBtn.setEnabled(false);
        initRecorders();
        recording = !recording; // switch recording status

        // Play button is activated when it is not recording
        playBtn.setEnabled(!recording);

       //TODO recordBtn.setEnabled(!recording);

    }

    private void initRecorders() {
        compositionView.activate();
        recordTimer.cancel();
        audioRecorder.stop();
        audioRecorder = new AudioRecorder();
        recordTimer = new Timer();
        width = 0;
        try {
            soundView = builder.record(this);
        } catch (NoActiveTrackException ex) {
            Toast.makeText(this, "Please select Track!", Toast.LENGTH_LONG).show();
            initRecorders();
        } catch (SoundWillOverlapException ex2) {
            Toast.makeText(this, "Recording will overlap with other sounds!", Toast.LENGTH_LONG).show();
            initRecorders();
        } catch (SoundWillBeOutOfCompositionException ex) {
            //Toast.makeText(this, "Recording will be out of composition!", Toast.LENGTH_LONG).show();
            // initRecorders();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
            initRecorders();
        }
        layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
    }
}
