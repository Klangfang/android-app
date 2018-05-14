package com.wfm.soundcollaborations.model.composition;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.util.Log;

import com.wfm.soundcollaborations.model.Constants;
import com.wfm.soundcollaborations.views.composition.CompositionView;

/**
 * Created by mohammed on 11/18/17.
 */

public class TracksTimer
{
    private static final String TAG = TracksTimer.class.getSimpleName();

    private int positionInMillis = 0;
    private int circlesReached = 0;
    private Timer mTimer;
    private Handler mHandler;
    private ArrayList<Track> mTracks;
    private CompositionView mCompositionView;
    private boolean cancelTimer = false;

    public TracksTimer(ArrayList<Track> tracks, CompositionView compositionView)
    {
        mHandler = new Handler();
        mTracks = tracks;
        mCompositionView = compositionView;
    }

    public void play()
    {
        cancelTimer = false;
        this.mCompositionView.setEnabled(false);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run()
            {
                mHandler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        if(cancelTimer)
                        {
                            mTimer.cancel();
                            return;
                        }
                        if(positionInMillis >= Constants.COMPOSITION_LENGTH_IN_MILLI_SECONDS)
                        {
                            reset();
                        }

                        for(int i=0; i<mTracks.size(); i++)
                        {
                            mTracks.get(i).play(i, positionInMillis);
                        }

                        // increase scroll position
                        circlesReached += 1;
                        positionInMillis += 1;
                        if(circlesReached >= 50)
                        {
                            mCompositionView.increaseScrollPosition(3);
                            circlesReached = 0;
                        }
                    }
                });
            }
        }, 0, 1);
    }

    public void pause()
    {
        cancelTimer = true;
        this.mCompositionView.setEnabled(true);
        for(int i=0; i<mTracks.size(); i++)
        {
            mTracks.get(i).pause(i);
        }
    }

    public void seek(int positionInMillis)
    {
        for(int i=0; i<mTracks.size(); i++)
        {
            mTracks.get(i).seek(positionInMillis);
        }
        this.positionInMillis = positionInMillis;
    }

    public void reset()
    {
        mTimer.cancel();
        positionInMillis = 0;
        mCompositionView.setScrollPosition(0);
        this.mCompositionView.setEnabled(true);
    }


}
