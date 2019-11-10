package com.wfm.soundcollaborations.Classes;


public class CompositionOverview {
    // Member Variables of CompositionRequest Overview
    public final String mTitle;
    public final int mNumberOfMembers;
    public final String mSoundUri;

    /**
     * Creates new {@link CompositionOverview} objects.
     *
     * @param title is the title of the composition
     * @param numberOfMembers is the quantity of users that are currently participating
     * @param soundUri is the path of an audio file that can be played
     */
    public CompositionOverview(String title, int numberOfMembers, String soundUri) {
        mTitle = title;
        mNumberOfMembers = numberOfMembers;
        mSoundUri = soundUri;
    }
}
