package com.wfm.soundcollaborations.activities;

import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.model.audio.AudioPlayer;
import com.wfm.soundcollaborations.model.Constants;
import com.wfm.soundcollaborations.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.views.SoundView;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordActivity extends AppCompatActivity
{
    private static final String TAG = RecordActivity.class.getSimpleName();

    @BindView(R.id.toolbar_record)
    Toolbar recordToolbar;
    @BindView(R.id.sv_visualizer)
    SoundView soundVisualizerView;
    @BindView(R.id.tv_record_time)
    TextView recordTimeTextView;
    @BindView(R.id.btn_record)
    Button recordBtn;
    @BindView(R.id.btn_play)
    Button playBtn;

    private AudioRecorder mAudioRecorder;
    private boolean isRecording = false;
    private AudioPlayer mTrackPlayer;
    private boolean isPlaying = false;



    private Handler handler;
    private Timer recordTimeTimer;
    private Timer visualizeAmplitudesTimer;
    private int currentRecordedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        ButterKnife.bind(this);

        initToolbar();
        initRecordBtn();
        initPlayBtn();

        mAudioRecorder = new AudioRecorder();
        handler = new Handler();

    }

    private void initToolbar()
    {
        setSupportActionBar(recordToolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initRecordBtn()
    {
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(! isRecording)
                {
                    resetPlayBtn();
                    mAudioRecorder.create();
                    mAudioRecorder.start();
                    initRecordTimeTimer();
                    initVisualizeAmplitudesTimer();
                    ((Button)(view)).setText("Finish");
                    isRecording = true;
                }
                else
                {
                    mAudioRecorder.stop();
                    recordTimeTimer.cancel();
                    visualizeAmplitudesTimer.cancel();
                    resetValues();
                    ((Button)(view)).setText("Record");
                }
            }
        });
    }

    private void initPlayBtn()
    {
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view)
            {
                try
                {
                    /*
                    soundVisualizerView.reset();
                    SoundSampler soundSampler = SoundSampler.create(mAudioRecorder.getRecordedFilePath());
                    Log.d(TAG, "Number of Frames are => "+ soundSampler.getNumFrames());
                    int[] grains = soundSampler.getFrameGains();
                    for(int i=0; i<grains.length; i++)
                    {
                        Log.d(TAG, "Value of "+i+" = "+ grains[i]);
                        soundVisualizerView.addWave(grains[i] * 2);
                    }
                    /*mTrackPlayer = new AudioPlayer(getBaseContext(), Uri.parse(mAudioRecorder.getRecordedFilePath()));

                    mTrackPlayer.play();
                    view.setEnabled(false);
                    mTrackPlayer.getMediaPlayer().setOnCompletionListener(
                            new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    view.setEnabled(true);
                                }
                            });*/
                }
                catch (Exception ex)
                {
                    Toast.makeText(getBaseContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void resetPlayBtn()
    {
        playBtn.setEnabled(true);
        mTrackPlayer = null;
        soundVisualizerView.reset();
    }

    private void initRecordTimeTimer()
    {
        recordTimeTimer = new Timer();
        recordTimeTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(currentRecordedTime > Constants.MAX_RECORD_TIME)
                            maxTimeForRecordingReached();
                        else
                            updateTime(currentRecordedTime);
                        currentRecordedTime += 1;
                    }
                });
            }
        }, 0, 1000);
    }

    private void initVisualizeAmplitudesTimer()
    {
        soundVisualizerView.reset();

        visualizeAmplitudesTimer = new Timer();
        visualizeAmplitudesTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(mAudioRecorder != null)
                            soundVisualizerView.add(mAudioRecorder.getMaxAmplitude() * 4);
                    }
                });
            }
        }, 0, 170);
    }

    private void updateTime(int recordTime)
    {
        int value = Constants.MAX_RECORD_TIME - recordTime;
        if(value >= 10)
            recordTimeTextView.setText("00:00:"+value);
        else
            recordTimeTextView.setText("00:00:0"+value);
    }

    private void maxTimeForRecordingReached()
    {
        mAudioRecorder.stop();
        recordTimeTimer.cancel();
        visualizeAmplitudesTimer.cancel();
        recordBtn.setText("Record");
        Toast.makeText(this, "You have reached maximum time for recording!", Toast.LENGTH_SHORT).show();
        resetValues();
    }

    private void resetValues()
    {
        isRecording = false;
        currentRecordedTime = 0;
        updateTime(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
