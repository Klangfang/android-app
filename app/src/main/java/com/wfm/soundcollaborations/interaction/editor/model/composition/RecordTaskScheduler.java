package com.wfm.soundcollaborations.interaction.editor.model.composition;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

class RecordTaskScheduler {

    private static final int DELAY = 0;
    private static final int PERIOD = 50;

    private Runnable callback;
    private Timer timer;
    private Handler handler;
    private boolean recording;


    RecordTaskScheduler() {

        super();

    }


    void record(Runnable callback) {

        if (!recording) {

            recording = true;

            this.callback = callback;

            handler = new Handler();
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    handler.post(callback);

                }
            }, DELAY, PERIOD);

        }

    }


    void stop() {

        if (recording) {

            recording = false;
            timer.cancel();
            handler.removeCallbacks(callback);

        }

    }


    boolean isRecording() {

        return recording;

    }

}
