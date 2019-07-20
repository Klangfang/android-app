package com.wfm.soundcollaborations.activities;

import android.os.Bundle;
import android.view.ViewGroup;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.views.SoundSelectorView;

/**
 * Created by Markus Eberts on 15.10.16.
 */
public class SongEditorActivity extends MainActivity {

    SoundSelectorView selectorUL;
    SoundSelectorView selectorUR;
    SoundSelectorView selectorLL;
    SoundSelectorView selectorLR;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.song_editor, (ViewGroup) findViewById(R.id.fl_content));

        selectorUL = (SoundSelectorView) findViewById(R.id.selector_ul);
        selectorUR = (SoundSelectorView) findViewById(R.id.selector_ur);
        selectorLL = (SoundSelectorView) findViewById(R.id.selector_ll);
        selectorLR = (SoundSelectorView) findViewById(R.id.selector_lr);
    }
}
