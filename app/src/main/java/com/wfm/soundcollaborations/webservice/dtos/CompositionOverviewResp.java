package com.wfm.soundcollaborations.webservice.dtos;

/**
 *  Represents a composition that the user can listen to
 **/
public class CompositionOverviewResp {

    public Long id;
    public String title;
    public Integer numberOfMembers;
    public String snippetUrl;

    public CompositionOverviewResp(Long id, String title, Integer numberOfMembers, String snippetUrl) {
        this.id = id;
        this.title = title;
        this.numberOfMembers = numberOfMembers;
        this.snippetUrl = snippetUrl;
    }

}
