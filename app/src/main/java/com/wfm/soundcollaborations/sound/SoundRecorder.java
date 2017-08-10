package com.wfm.soundcollaborations.sound;

import android.app.Activity;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by markus on 09.10.16.
 */
public class SoundRecorder implements MediaRecorder.OnInfoListener {
    private final static String TAG = SoundRecorder.class.getSimpleName();

    private MediaRecorder recorder;
    private Activity context;
    private Handler handler;
    private Timer timer;
    private boolean recordStarted;

    private List<Integer> amplitudes = new ArrayList<>();

    private RecordListener listener;
    public interface RecordListener{
        void recordUpdate(int e, Object data);
    }

    public SoundRecorder(Activity context){
        this.context = context;
        this.handler = new Handler();
    }

    public void start(String filename, long maxDuration) {
        if (recorder != null){ return; }

        amplitudes.clear();
        recordStarted = false;

        recorder = new MediaRecorder();
        recorder.setOnInfoListener(this);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(filename);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setMaxDuration((int) maxDuration);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "Recorder prepare failed");
            stop();
            return;
        }

        recorder.start();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (recorder == null){
                            return;
                        }

                        int a = recorder.getMaxAmplitude();

                        if (!recordStarted && a > 0){
                            recordStarted = true;
                        }

                        if (recordStarted) {
                            amplitudes.add(a);
                            listener.recordUpdate(2, amplitudes);
                            Log.v(TAG, "Amplitude (record): " + a);
                        } else {
                            Log.v(TAG, "Amplitude: " + a);
                        }
                    }
                });
            }
        }, 0, 20);
    }


    public void stop() {
        if (recorder == null){ return; }
        Log.e(TAG, "Stop record");

        timer.cancel();
        timer = null;

        int m = 1;

        try {
            recorder.stop();
        } catch (RuntimeException e){
            m = 4;
        } finally {
            recorder.release();
            recorder = null;
        }

        listener.recordUpdate(m, null);
    }

    public void setListener(RecordListener listener){
        this.listener = listener;
    }

    public List<Integer> getAmplitudes(){
        return amplitudes;
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int i, int i1) {
        if (i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            Log.e(TAG, "Max duration");
            stop();
        }
    }
}
