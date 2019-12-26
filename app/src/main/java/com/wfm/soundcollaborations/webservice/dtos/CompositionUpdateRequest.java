package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CompositionUpdateRequest {

    public List<SoundRequest> sounds;

    private CompositionUpdateRequest() {

        this.sounds = new ArrayList<>();

    }

    private CompositionUpdateRequest(List<SoundRequest> sounds) {

        this.sounds = sounds;

    }

    public static CompositionUpdateRequest build(Stream<LocalSound> recordedSounds) {

        List<SoundRequest> soundReqs = recordedSounds.map(SoundRequest::build)
                .collect(Collectors.toList());
        return new CompositionUpdateRequest(soundReqs);
    }

    public static CompositionUpdateRequest build() {

        return new CompositionUpdateRequest();
    }

}
