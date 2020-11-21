package com.wfm.soundcollaborations.webservice.dtos;

public class SoundResponse {

    public Long id;

    public Integer trackNumber;

    public String url;

    public Integer startPosition;

    public Integer duration;

    public String creatorName;

    public SoundResponse() {

    }

    public SoundResponse(Long id, Integer trackNumber, String url, Integer startPosition, Integer duration, String creatorName) {
        this.id = id;
        this.trackNumber = trackNumber;
        this.url = url;
        this.startPosition = startPosition;
        this.duration = duration;
        this.creatorName = creatorName;
    }

}
