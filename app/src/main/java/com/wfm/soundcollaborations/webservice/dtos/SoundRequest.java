package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.interaction.editor.utils.ByteArrayUtils;

public final class SoundRequest {

    private Integer trackNumber;

    private Integer startPosition;

    public Integer duration;

    public String creatorName = "TALAL";

    private byte[] soundBytes;

    private SoundRequest(Integer trackNumber,
                         Integer startPosition,
                         Integer duration,
                         byte[] soundBytes) {

        this.trackNumber = trackNumber;
        this.startPosition = startPosition;
        this.duration = duration;
        this.soundBytes = soundBytes;

    }

    public static SoundRequest build(LocalSound s) {

        return new SoundRequest(s.trackIndex, s.startPosition, s.duration, ByteArrayUtils.toByteArray(s.filePath));

    }

}
