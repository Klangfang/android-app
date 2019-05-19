package com.wfm.soundcollaborations.Editor.model.composition;

import java.util.Set;

public class OverviewResponse {

    public final Set<CompositionOverview> overviews;

    public final String nextPage;

    public OverviewResponse(Set<CompositionOverview> overviews, String nextPage) {
        this.overviews = overviews;
        this.nextPage = nextPage;
    }
}
