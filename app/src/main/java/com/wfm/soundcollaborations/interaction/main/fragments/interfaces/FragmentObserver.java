package com.wfm.soundcollaborations.interaction.main.fragments.interfaces;

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