package com.wfm.soundcollaborations.webservice.dtos;

import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.Editor.utils.ByteArrayUtils;

public final class SoundRequest {

    public Integer trackNumber;

    public Integer startPosition;

    public Integer duration;

    public String creatorName = "TALAL";

    public byte[] soundBytes;

    private SoundRequest(Integer trackNumber,
                         Integer startPosition,
                         Integer duration,
                         byte[] soundBytes) {

        this.trackNumber = trackNumber;
        this.startPosition = startPosition;
        this.duration = duration;
        this.soundBytes = soundBytes;

    }

    public static SoundRequest build(Sound s) {

        return new SoundRequest(s.trackNumber, s.startPosition, s.duration, ByteArrayUtils.toByteArray(s.filePath));

    }

}
