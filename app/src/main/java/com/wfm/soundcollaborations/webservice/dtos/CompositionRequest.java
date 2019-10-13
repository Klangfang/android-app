package com.wfm.soundcollaborations.webservice.dtos;

import java.util.List;

public class CompositionRequest {

    public String title;

    public String creatorName;

    public List<SoundRequest> sounds;

    public CompositionRequest(String title, String creatorName, List<SoundRequest> sounds) {

        this.title = title;
        this.creatorName = creatorName;
        this.sounds = sounds;

    }
}
