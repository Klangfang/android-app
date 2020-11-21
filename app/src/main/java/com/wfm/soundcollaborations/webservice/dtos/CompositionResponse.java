package com.wfm.soundcollaborations.webservice.dtos;


import java.util.List;

public class CompositionResponse {

    public Long id;

    public String title;

    public String creatorName;

    public String creationTime;

    public String status;

    public Integer numberOfMembers;

    public Integer duration;

    public String snippet;

    public List<SoundResponse> sounds;

    public CompositionResponse() {
    }

    public CompositionResponse(Long id, String title, String creatorName, String creationTime, String status, Integer numberOfMembers, Integer duration, String snippet, List<SoundResponse> sounds) {
        this.id = id;
        this.title = title;
        this.creatorName = creatorName;
        this.creationTime = creationTime;
        this.status = status;
        this.numberOfMembers = numberOfMembers;
        this.duration = duration;
        this.snippet = snippet;
        this.sounds = sounds;
    }

}