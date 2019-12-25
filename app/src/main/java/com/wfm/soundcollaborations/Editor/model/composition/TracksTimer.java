package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.os.Handler;

import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


class TracksTimer {

    private static final String TAG = TracksTimer.class.getSimpleName();

    private static final int POSITION_ZERO = 0;

    private static final int COMPOSITION_MAX_DURATION_IN_MS = 1000 * 120;

    private final Context context;

    private int positionInMillis = POSITION_ZERO;
    private int circlesReached = POSITION_ZERO;
    private Timer mTimer;
    private Handler mHandler;
    private List<Track> mTracks;
    private CompositionView mCompositionView;
    private boolean cancelTimer = false;


    TracksTimer(Context context, List<Track> tracks, CompositionView compositionView) {

        this.context = context;
        mHandler = new Handler();
        mTracks = tracks;
        mCompositionView = compositionView;

    }


    void playOrPause(boolean pressPlay) {

        cancelTimer = !pressPlay;

        this.mCompositionView.enable(!pressPlay);

        if (pressPlay) {

            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(() -> {
                        if (cancelTimer) {
                            mTimer.cancel();
                            return;
                        }
                        if (positionInMillis >= COMPOSITION_MAX_DURATION_IN_MS) {
                            reset();
                            return;
                        }

                        playOrPauseInternal(true);

                        // increase scroll position
                        circlesReached += 1;
                        positionInMillis += 1;
                        if (circlesReached >= 50) {
                            mCompositionView.increaseScrollPosition();
                            circlesReached = 0;
                        }
                    });
                }
            }, 0, 1);


        } else {

            playOrPauseInternal(false);

        }

    }


    private void playOrPauseInternal(boolean pressPlay) {

        mTracks.forEach(t -> t.playOrPause(pressPlay, positionInMillis));

    }


    void seek(int positionInMillis) {

        mTracks.forEach(t -> t.seek(positionInMillis));

        this.positionInMillis = positionInMillis;

    }


    private void reset() {

        if (mTimer != null) {
            mTimer.cancel();
        }

        positionInMillis = POSITION_ZERO;

        if (mCompositionView != null) {

            mCompositionView.setScrollPosition(POSITION_ZERO);

            this.mCompositionView.setEnabled(true);

            EditorActivity editorActivity = (EditorActivity) this.context;

            editorActivity.handleStop(StopReason.COMPOSITION_END_REACHED);


        }
    }


    void updateTrack(int trackNumber, Track track) {

        mTracks.set(trackNumber, track);

    }

}
