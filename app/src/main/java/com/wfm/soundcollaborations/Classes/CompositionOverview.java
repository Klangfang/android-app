package com.wfm.soundcollaborations.Classes;

import android.widget.Button;

/**
 *  {@link CompositionOverview} represents a Composition that the user can listen to.
 *  It contains the meta data, such as title, number of members and an audio preview file.
 **/
public class CompositionOverview {

    private String mCompositionTitle;
    private String mLocations;
    private String mNumberOfMembers;
    //private Button mPlayButton;

    public CompositionOverview(String compositionTitle, String locations, String numberOfMembers/*,Button playButton*/) {
        mCompositionTitle = compositionTitle;
        mLocations = locations;
        mNumberOfMembers = numberOfMembers;
        //mPlayButton = playButton;
    }

    public String getCompositionTitle() {return mCompositionTitle;}
    public String getCompositionLocations() {return mLocations;}
    public String getNumberOfMembers() {return mNumberOfMembers;}
    /*
    public void playCompositionOverview() {
        //TODO add button to method
    }*/

}
