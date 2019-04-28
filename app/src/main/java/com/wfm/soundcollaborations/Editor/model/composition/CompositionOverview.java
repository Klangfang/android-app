package com.wfm.soundcollaborations.Editor.model.composition;

public class CompositionOverview {

    public String title;
    public int numberOfParticipation;
    public String soundFilePath;
    public String pickUrl;

    public CompositionOverview(String title, int numberOfParticipation, String soundFilePath, String pickUrl) {
        this.title = title;
        this.numberOfParticipation = numberOfParticipation;
        this.soundFilePath = soundFilePath;
        this.pickUrl = pickUrl;
    }
}
