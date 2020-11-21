package com.wfm.soundcollaborations.webservice.dtos;

import java.util.List;

public class OverviewResponse {

    public List<CompositionOverviewResp> overviews;

    public String nextPage;

    public OverviewResponse() {

    }

    public OverviewResponse(List<CompositionOverviewResp> overviews) {
        this.overviews = overviews;
    }

}
