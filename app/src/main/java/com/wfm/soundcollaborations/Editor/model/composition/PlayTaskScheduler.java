package com.wfm.soundcollaborations.Editor.model.composition;

import android.os.Handler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class PlayTaskScheduler {

    private static final int COMPOSITION_MAX_DURATION_IN_MS = 1000 * 120;
    private static final int PLAYING_PERIOD_IN_MS = 1;
    private static final long NO_DELAY = 0;
    private static final int POSITION_ZERO = 0;

    private Timer timer;

    private int positionInMillis = POSITION_ZERO;
    private int circlesReached = POSITION_ZERO;

    private boolean playing;


    PlayTaskScheduler() {

        super();

    }


    void playOrPause(boolean pressPlay, List<Track> tracks, Runnable fallback1, Runnable fallback2) {

        Handler handler = new Handler();

        playing = pressPlay;

        if (pressPlay) {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(() -> {

                        if (!playing) {

                            cancel();
                            return;

                        }

                        if (positionInMillis >= COMPOSITION_MAX_DURATION_IN_MS) {

                            cancel();
                            positionInMillis = POSITION_ZERO;
                            fallback2.run();
                            return;

                        }

                        tracks.forEach(t -> t.playOrPause(true, positionInMillis));

                        // increase scroll position
                        circlesReached += PLAYING_PERIOD_IN_MS;
                        positionInMillis += PLAYING_PERIOD_IN_MS;
                        if (circlesReached >= 50) {
                            fallback1.run();
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


    void stop() {

        if (playing) {

            playing = false;
            timer.cancel();

        }

    }


    boolean isPlaying() {

        return playing;

    }

}
