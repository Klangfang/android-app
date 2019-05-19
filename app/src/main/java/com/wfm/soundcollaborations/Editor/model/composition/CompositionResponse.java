package com.wfm.soundcollaborations.Editor.model.composition;

public class CompositionResponse {

    public final Composition composition;

    public final String pickUrl;

    public CompositionResponse(Composition composition, String pickUrl) {
        this.composition = composition;
        this.pickUrl = pickUrl;
    }
}
