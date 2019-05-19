package com.wfm.soundcollaborations.Editor.model.composition;

public class CompositionOverview {

    public String title;
    public Integer numberOfMembers;
    public String snippetUrl;
    public String pickUrl;

    public CompositionOverview(String title, int numberOfMembers, String snippetUrl, String pickUrl) {
        this.title = title;
        this.numberOfMembers = numberOfMembers;
        this.snippetUrl = snippetUrl;
        this.pickUrl = pickUrl;
    }
}
