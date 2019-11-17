package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class CompositionUpdateRequest {

    public List<SoundRequest> sounds;

    private CompositionUpdateRequest() {

        this.sounds = new ArrayList<>();

    }

    private CompositionUpdateRequest(List<SoundRequest> sounds) {

        this.sounds = sounds;

    }

    public static CompositionUpdateRequest build(List<Sound> recordedSounds) {

        List<SoundRequest> soundReqs = recordedSounds.stream()
                .map(SoundRequest::build)
                .collect(Collectors.toList());
        return new CompositionUpdateRequest(soundReqs);
    }

    public static CompositionUpdateRequest build() {

        return new CompositionUpdateRequest();
    }

}
