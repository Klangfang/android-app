package com.wfm.soundcollaborations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.animation.TimeLimitAnimator;
import com.wfm.soundcollaborations.sound.SoundRecorder;

import java.util.UUID;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class SoundRecordActivity extends MainActivity {
    private final static String TAG = SoundRecordActivity.class.getSimpleName();

    private final long MAX_DURATION = 5000;

    private View timeLimitBar;
    private DisplayMetrics metrics;

    private SoundRecorder soundRecorder;
    private TimeLimitAnimator timeLimitAnimator;

    private final static String fileBaseURI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    private final static String fileExtension = ".3gp";
    private String currentFileURI;

    private ImageButton btnRecord;
    private View.OnTouchListener recordListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent m) {
            switch (m.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentFileURI = fileBaseURI + UUID.randomUUID().toString() + fileExtension;
                    Log.e(TAG, currentFileURI);

                    // Start recorder and animation
                    soundRecorder.start(currentFileURI, MAX_DURATION);
                    soundRecorder.stop();
                    timeLimitAnimator.start(MAX_DURATION);
                    break;

                case MotionEvent.ACTION_UP:
                    // Stop recorder and animation
                    soundRecorder.stop();
                    timeLimitAnimator.stop();
                    Intent intent = new Intent(SoundRecordActivity.this, SoundEditorActivity.class);
                    intent.putExtra("fileURI", currentFileURI);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.record, (ViewGroup) findViewById(R.id.content_layout));
        //getSupportActionBar().setTitle("Record");

        btnRecord = (ImageButton) findViewById(R.id.btn_record);
        timeLimitBar = findViewById(R.id.rl_time_limit);

        // Record button
        btnRecord.setOnTouchListener(recordListener);

        // Display metrics to get screen height
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // Record time limit bar
        timeLimitAnimator = new TimeLimitAnimator(timeLimitBar, metrics.heightPixels,
                getResources().getIntArray(R.array.record_limit_colors));

        // Sound recorder
        soundRecorder = new SoundRecorder(this);
    }

    @Override
    public void onResume(){
        super.onResume();

        timeLimitAnimator.stop();
    }
}
