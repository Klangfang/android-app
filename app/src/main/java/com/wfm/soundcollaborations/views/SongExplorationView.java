package com.wfm.soundcollaborations.views;

import android.content.Context;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.View;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 12.11.16.
 */
public class SongExplorationView extends CardView {

    public SongExplorationView(Context context) {
        super(context);
        createLayout();
    }

    public SongExplorationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createLayout();
    }

    private void createLayout(){
        View.inflate(getContext(), R.layout.song_exploration, this);
    }
}
