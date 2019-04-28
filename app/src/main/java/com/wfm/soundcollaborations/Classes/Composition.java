package com.wfm.soundcollaborations.Classes;

/**
 *  {@link Composition} represents a Composition that the user can listen to.
 *  It contains the meta data, such as title, number of participants and a audio preview file.
 **/
public class Composition {

    private String mCompositionTitle;
    private String mLocations;
    private String mNumberOfMembers;

    public Composition(String compositionTitle, String locations, String numberOfMembers) {
        mCompositionTitle = compositionTitle;
        mLocations = locations;
        mNumberOfMembers = numberOfMembers;
    }

    public String getCompositionTitle() {return mCompositionTitle;}
    public String getCompositionLocations() {return mLocations;}
    public String getNumberOfMembers() {return mNumberOfMembers;}

}
