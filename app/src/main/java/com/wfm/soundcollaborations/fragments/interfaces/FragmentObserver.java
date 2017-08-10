package com.wfm.soundcollaborations.fragments.interfaces;

import android.os.Bundle;

/**
 * Created by Markus Eberts on 13.10.16.
 */
public interface FragmentObserver {

    enum Event {
        RECORDED_SOUND,
        SOUND_MANAGEMENT,
        SELECTED_ITEM,
    }

    void fragmentUpdate(Event event, Bundle args);

}