package com.wfm.soundcollaborations.Classes;

/**
 *  {@link CompositionOverview} represents a composition that the user can listen to.
 *
 *  It is the helper class to connect {@link com.wfm.soundcollaborations.fragments.ComposeFragment}
 *  and {@link com.wfm.soundcollaborations.adapter.CompositionAdapter}
 *
 *  It contains following data of the composition:
 *      - title (of the composition)
 *      - number of members (in the composition)
 *      - a sound URI (of the audio file that can be played)
 **/
public class CompositionOverview {
    // Member Variables of Composition Overview
    public final String mTitle;
    public final String mNumberOfMembers;
    public final String mSoundUri;

    /**
     * Creates new {@link CompositionOverview} objects.
     *
     * @param title is the title of the composition
     * @param numberOfMembers is the quantity of users that are currently participating
     * @param soundUri is the path of an audio file that can be played
     */
    public CompositionOverview(String title, String numberOfMembers, String soundUri) {
        mTitle = title;
        mNumberOfMembers = numberOfMembers;
        mSoundUri = soundUri;
    }
}
