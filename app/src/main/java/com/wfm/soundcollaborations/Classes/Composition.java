package com.wfm.soundcollaborations.Classes;

/**
 *  {@link Composition} represents a CompositionRequest that the user can listen to.
 *  It contains the meta data, such as title, number of participants and a audio preview file.
 **/
public class Composition {

    public String pickUrl;
    private String mCompositionTitle;
    private String mLocations;
    private String mNumberOfMembers;

    public Composition(String pickUrl, String compositionTitle, String locations, String numberOfMembers) {
        this.pickUrl = pickUrl;
        mCompositionTitle = compositionTitle;
        mLocations = locations;
        mNumberOfMembers = numberOfMembers;
    }

    public String getCompositionTitle() {return mCompositionTitle;}
    public String getCompositionLocations() {return mLocations;}
    public String getNumberOfMembers() {return mNumberOfMembers;}

}
