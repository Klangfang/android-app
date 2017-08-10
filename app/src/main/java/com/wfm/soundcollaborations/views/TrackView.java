package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 19.10.16.
 */
public class TrackView extends LinearLayout{

    public TrackView(Context context) {
        super(context);
        createLayout();
    }

    public TrackView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createLayout();
    }

    private void createLayout(){
        View.inflate(getContext(), R.layout.track, this);
    }

}
