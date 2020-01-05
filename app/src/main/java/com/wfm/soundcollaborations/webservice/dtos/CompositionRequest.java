package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.editor.model.composition.sound.LocalSound;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CompositionRequest {

    public String title;

    private String creatorName;

    public List<SoundRequest> sounds;

    private CompositionRequest(String title, String creatorName, List<SoundRequest> sounds) {

        this.title = title;
        this.creatorName = creatorName;
        this.sounds = sounds;

    }

    public static CompositionRequest build(String title, String creatorName, Stream<LocalSound> recordedSounds) {

        List<SoundRequest> soundReqs = recordedSounds.map(SoundRequest::build)
                .collect(Collectors.toList());
        return new CompositionRequest(title, creatorName, soundReqs);

    }
}
