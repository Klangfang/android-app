package com.wfm.soundcollaborations.Editor.model.composition;

/**
 *  {@link com.wfm.soundcollaborations.Classes.CompositionOverview} represents a composition that the user can listen to.
 *
 *  It is the helper class to connect {@link com.wfm.soundcollaborations.fragments.ComposeFragment}
 *  and {@link com.wfm.soundcollaborations.adapter.CompositionOverviewAdapter}
 *
 *  It contains following data of the composition:
 *      - title (of the composition)
 *      - number of members (in the composition)
 *      - a sound URI (of the audio file that can be played)
 **/
public class CompositionOverview {

    public String title;
    public Integer numberOfMembers;
    public String snippetUrl;
    public String pickUrl;

}
