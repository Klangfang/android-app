package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.Editor.model.composition.Sound;

import java.util.List;
import java.util.stream.Collectors;

public final class CompositionRequest {

    public String title;

    public String creatorName;

    public List<SoundRequest> sounds;

    private CompositionRequest(String title, String creatorName, List<SoundRequest> sounds) {

        this.title = title;
        this.creatorName = creatorName;
        this.sounds = sounds;

    }

    public static CompositionRequest build(String title, String creatorName, List<Sound> recordedSounds) {

        List<SoundRequest> soundReqs = recordedSounds.stream()
                .map(SoundRequest::build)
                .collect(Collectors.toList());
        return new CompositionRequest(title, creatorName, soundReqs);

    }
}
