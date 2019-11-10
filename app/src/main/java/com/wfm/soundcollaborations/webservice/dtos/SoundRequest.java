package com.wfm.soundcollaborations.webservice.dtos;

public class SoundRequest {

    public Integer trackNumber;

    public Integer startPosition;

    public Integer duration;

    public String creatorName = "TALAL";

    public SoundRequest(Integer trackNumber, Integer startPosition, Integer duration) {

        this.trackNumber = trackNumber;
        this.startPosition = startPosition;
        this.duration = duration;

    }

}
