package com.wfm.soundcollaborations.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.RecordActivity;
import com.wfm.soundcollaborations.activities.SaveSoundActivity;
import com.wfm.soundcollaborations.adapter.SoundListAdapter;
import com.wfm.soundcollaborations.animation.PulseAnimation;
import com.wfm.soundcollaborations.Editor.model.audio.AudioRecorder;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammed on 10/5/17.
 */

public class MySoundsFragment extends Fragment
{
    private final static String TAG = MySoundsFragment.class.getSimpleName();

    @BindView(R.id.lv_sounds)
    ListView lvSounds;
    @BindView(R.id.btn_record)
    FloatingActionButton btnRecord;

    private AudioRecorder recorder;
    private final static String SOUND_FILE_BASE_URI_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
    private final static String SOUND_FILE_EXTENSION = ".3gp";
    private String filePath;

    private PulseAnimation mPulseAnimation;
    private SoundListAdapter soundListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        recorder = new AudioRecorder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mysounds, container, false);
        ButterKnife.bind(this, root);
        initRecordBtn();
        return root;
    }

    /**
     * initialize Events for Record Button
     */
    private void initRecordBtn()
    {
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent recordActivity = new Intent(getContext(), RecordActivity.class);
                startActivity(recordActivity);
            }
        });
        /*btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "Sound Recording Service Started");
                        filePath = SOUND_FILE_BASE_URI_DIR + getString(R.string.sound_file_prefix) + "_" +
                                DateUtils.getCurrentDate("yyyyMMdd_HHmmss")+ SOUND_FILE_EXTENSION;
                        recorder.start(filePath);
                        mPulseAnimation = new PulseAnimation(view);
                        mPulseAnimation.start();
                        break;

                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "Sound Recording Service Stopped");
                        try
                        {
                            mPulseAnimation.stop();
                            recorder.stop();
                            showSaveSoundActivity();
                        }
                        catch (Exception ex)
                        {
                            Log.d(TAG, "There was an error while recording! clear file!");
                            FileUtils.deleteFile(filePath);
                            recorder.reset();
                            Toast.makeText(getContext(), getString(R.string.MESSAGE_PRESS_AND_HOLD_TO_RECORD),
                                    Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                return true;
            }
        });*/
    }

    // TODO Refactor this function!
    @Override
    public void onStart()
    {
        super.onStart();
        // load recorded files
    }



    /**
     * if the recording was successful then move to save sound activity
     */
    private void showSaveSoundActivity()
    {
        Intent intent = new Intent(getContext(), SaveSoundActivity.class);
        intent.putExtra(SaveSoundActivity.EXTRA_SOUND_FILE_URI, filePath);
        startActivity(intent);
    }
}