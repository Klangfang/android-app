package com.wfm.soundcollaborations.Classes;

/**
 *  {@link CompositionOverview} represents a composition that the user can listen to.
 *
 *  It is the helper class to connect {@link com.wfm.soundcollaborations.fragments.ComposeFragment}
 *  and {@link com.wfm.soundcollaborations.adapter.CompositionAdapter}
 *
 *  It contains following meta data of the composition:
 *  - title (of the composition)
 *  - number of members (in the composition)
 *  - an audio preview file //TODO @willi: Add Audio Preview
 **/
public class CompositionOverview {
    // Placeholder
    private String mTitle;
    private String mNumberOfMembers;

    // Helper class to replace placeholder with real content
    // Title and numberOfMembers are loaded from ComposeFragment.java
    public CompositionOverview(String title, String numberOfMembers) {
        mTitle = title;
        mNumberOfMembers = numberOfMembers;
    }

    // Return new replaced content
    // Function is called in CompositionAdapter.java
    public String getCompositionTitle() {
        return mTitle; // Real title
    }
    public String getNumberOfMembers() {
        return mNumberOfMembers; // Real Number
    }
}
