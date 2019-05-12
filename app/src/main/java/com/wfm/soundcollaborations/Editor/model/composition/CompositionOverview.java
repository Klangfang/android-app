package com.wfm.soundcollaborations.Editor.model.composition;

public class CompositionOverview {

    public String title;
    public Integer numberOfMembers;
    public String snippet;
    public String pickUrl;

    public CompositionOverview(String title, int numberOfMembers, String snippet, String pickUrl) {
        this.title = title;
        this.numberOfMembers = numberOfMembers;
        this.snippet = snippet;
        this.pickUrl = pickUrl;
    }
}
