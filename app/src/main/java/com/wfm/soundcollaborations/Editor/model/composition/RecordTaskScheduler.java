package com.wfm.soundcollaborations.Editor.model.composition;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

class RecordTaskScheduler {

    private static final int DELAY = 0;
    private static final int PERIOD = 50;

    private Timer timer;
    private boolean recording;


    RecordTaskScheduler() {

        super();

    }


    void record(Runnable fallback) {

        if (!recording) {

            recording = true;

            Handler handler = new Handler();

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    handler.post(fallback);

                }
            }, DELAY, PERIOD);

        }

    }


    void stop() {

        if (recording) {

            recording = false;
            timer.cancel();

        }

    }


    boolean isRecording() {

        return recording;

    }

}
