package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.os.Handler;

import com.wfm.soundcollaborations.Editor.activities.EditorActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class PlayTaskScheduler {

    private static final int COMPOSITION_MAX_DURATION_IN_MS = 1000 * 120;
    private static final int PLAYING_PERIOD_IN_MS = 1;
    private static final long NO_DELAY = 0;
    private static final int POSITION_ZERO = 0;

    private final Context context;

    private Timer timer;

    private int positionInMillis = POSITION_ZERO;
    private int circlesReached = POSITION_ZERO;

    private boolean pause = false;


    PlayTaskScheduler(Context context) {

        super();

        this.context = context;

    }


    void playOrPause(boolean pressPlay, List<Track> tracks) {

        EditorActivity editorActivity = (EditorActivity) context;

        Handler handler = new Handler();

        pause = !pressPlay;

        if (pressPlay) {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {

                        if (pause) {

                            cancel();
                            return;

                        }

                        if (positionInMillis >= COMPOSITION_MAX_DURATION_IN_MS) {

                            cancel();
                            reset(context);
                            return;

                        }

                        tracks.forEach(t -> t.playOrPause(true, positionInMillis));

                        // increase scroll position
                        circlesReached += PLAYING_PERIOD_IN_MS;
                        positionInMillis += PLAYING_PERIOD_IN_MS;
                        if (circlesReached >= 50) {
                            editorActivity.increaseScrollPosition();
                            circlesReached = 0;
                        }
                    });
                }
            }, NO_DELAY, PLAYING_PERIOD_IN_MS);


        } else {

            tracks.forEach(t -> t.playOrPause(false, positionInMillis));

        }

    }


    void seek(int positionInMillis) {

        this.positionInMillis = positionInMillis;

    }


    private void reset(Context context) {

        positionInMillis = POSITION_ZERO;

        EditorActivity editorActivity = (EditorActivity) context;
        editorActivity.handleStop(true, StopReason.COMPOSITION_END_REACHED);

    }

}
