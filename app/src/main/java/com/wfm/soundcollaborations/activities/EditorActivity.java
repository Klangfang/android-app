package com.wfm.soundcollaborations.activities;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ohoussein.playpause.PlayPauseView;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.exceptions.NoActiveTrackException;
import com.wfm.soundcollaborations.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.exceptions.SoundWillOverlapException;
import com.wfm.soundcollaborations.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.model.composition.CompositionBuilder;
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

    private boolean isPlaying = false;

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
    public void record(View view)
    {

        final SoundView soundView;
        final AudioRecorder audioRecorder = new AudioRecorder();
        final Timer recordTimer = new Timer();
        handler = new Handler();
        try
        {
            audioRecorder.create();
            soundView = builder.record(this);
            audioRecorder.start();
            recordTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            // do your work right here
                            layoutParams = (RelativeLayout.LayoutParams) soundView.getLayoutParams();
                            width = width + 3;
                            layoutParams.width = width;
                            soundView.setLayoutParams(layoutParams);
                            int max = audioRecorder.getMaxAmplitude();
                            soundView.addWave(max);
                            Log.d(TAG, "Max Amplitude Recieved -> "+max);
                            soundView.invalidate();
                            builder.getCompositionView().increaseScrollPosition(3);
                            builder.getCompositionView().increaseViewWatchPercentage(soundView.getTrack(), 0.17f);
                            try
                            {
                                builder.isThereAnyOverlapping(layoutParams.leftMargin + layoutParams.width, soundView.getTrack());
                            }
                            catch (Exception ex)
                            {
                                Log.d(TAG, "Timer Out Of Range!");
                                recordTimer.cancel();
                                audioRecorder.stop();
                            }

                        }
                    });
                }
            }, 0, 50);

        }
        catch (NoActiveTrackException ex)
        {
            Toast.makeText(this, "Please select Track!", Toast.LENGTH_LONG).show();
        }
        catch (SoundWillOverlapException ex2)
        {
            Toast.makeText(this, "Recording will overlap with other sounds!", Toast.LENGTH_LONG).show();
        }
        catch (SoundWillBeOutOfCompositionException ex)
        {
            Toast.makeText(this, "Recording will be out of composition!", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }

        view.setEnabled(false);

    }

    @OnClick(R.id.btn_play)
    public void play(View view)
    {
        if(! isPlaying)
        {
            builder.play();
            isPlaying = true;
            ((PlayPauseView) view).toggle();
        }
        else
        {
            builder.pause();
            isPlaying = false;
            ((PlayPauseView) view).toggle();
        }

    }




}
