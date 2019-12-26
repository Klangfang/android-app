package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.os.Handler;

import com.wfm.soundcollaborations.Editor.activities.EditorActivity;

import java.util.Timer;
import java.util.TimerTask;

class RecordTaskScheduler {

    private static final int DELAY = 0;
    private static final int PERIOD = 50;

    private final Context context;

    private Timer timer;


    RecordTaskScheduler(Context context) {

        super();

        this.context = context;

    }


    void record() {

        EditorActivity editorActivity = (EditorActivity) context;

        Handler handler = new Handler();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                handler.post(editorActivity::simulateRecording);

            }
        }, DELAY, PERIOD);

    }


    void stop() {

        timer.cancel();

    }

}
