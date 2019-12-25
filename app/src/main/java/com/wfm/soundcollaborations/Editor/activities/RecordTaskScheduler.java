package com.wfm.soundcollaborations.Editor.activities;

import android.content.Context;
import android.os.Handler;

import com.wfm.soundcollaborations.Editor.model.composition.StopReason;

import java.util.Timer;
import java.util.TimerTask;

class RecordTaskScheduler extends Timer {

    private static final int DELAY = 0;
    private static final int PERIOD = 50;


    void scheduleRecord(Context context) {

        EditorActivity editorActivity = (EditorActivity) context;

        Handler handler = new Handler();

        scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> {

                    if (!editorActivity.simulateRecording().equals(StopReason.NO_STOP)) {

                        this.cancel();

                    }

                });
            }
        }, DELAY, PERIOD);

    }

}
