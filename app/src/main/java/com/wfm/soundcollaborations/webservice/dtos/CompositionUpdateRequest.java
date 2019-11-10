package com.wfm.soundcollaborations.webservice.dtos;

import java.util.ArrayList;
import java.util.List;

public class CompositionUpdateRequest {

    public List<SoundRequest> sounds;

    public CompositionUpdateRequest() {

        this.sounds = new ArrayList<>();

    }

    public CompositionUpdateRequest(List<SoundRequest> sounds) {

        this.sounds = sounds;

    }

}
