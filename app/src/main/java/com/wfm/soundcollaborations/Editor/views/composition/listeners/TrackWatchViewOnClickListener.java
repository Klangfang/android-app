package com.wfm.soundcollaborations.Editor.views.composition.listeners;

import android.view.View;

import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;

/**
 * Created by mohammed on 10/22/17.
 */

public class TrackWatchViewOnClickListener implements View.OnClickListener
{

    private CompositionView compositionView;
    private int trackIndex;

    public TrackWatchViewOnClickListener(CompositionView compositionView, int trackIndex)
    {
        this.compositionView = compositionView;
        this.trackIndex = trackIndex;
    }

    @Override
    public void onClick(View view)
    {
        compositionView.activateTrack(trackIndex);
    }
}
