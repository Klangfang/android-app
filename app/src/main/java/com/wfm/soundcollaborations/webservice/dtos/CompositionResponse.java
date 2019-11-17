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

}