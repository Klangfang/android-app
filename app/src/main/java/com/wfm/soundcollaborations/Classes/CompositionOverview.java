package com.wfm.soundcollaborations.Classes;

/**
 *  {@link CompositionOverview} represents a Composition that the user can listen to.
 *  It contains the meta data, such as title, number of members and an audio preview file.
 **/
public class CompositionOverview {

    private String mCompositionTitle; //TODO change to mTitle
    private String mLocations;
    private String mNumberOfMembers;

    public CompositionOverview(String compositionTitle, String locations, String numberOfMembers) {
        mCompositionTitle = compositionTitle;
        mLocations = locations;
        mNumberOfMembers = numberOfMembers;
    }

    public String getCompositionTitle() {return mCompositionTitle;}
    public String getCompositionLocations() {return mLocations;}
    public String getNumberOfMembers() {return mNumberOfMembers;}
}
