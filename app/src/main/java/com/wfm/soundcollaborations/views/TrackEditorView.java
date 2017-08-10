package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 19.10.16.
 */
public class TrackEditorView extends LinearLayout{

    public TrackEditorView(Context context) {
        super(context);
        createLayout();
    }

    public TrackEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createLayout();
    }

    private void createLayout(){
        View.inflate(getContext(), R.layout.track_editor, this);
    }

}
