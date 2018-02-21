package com.wfm.soundcollaborations.views.composition.listeners;

import android.view.View;
import android.widget.Button;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.views.composition.CompositionView;

import butterknife.BindView;

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
