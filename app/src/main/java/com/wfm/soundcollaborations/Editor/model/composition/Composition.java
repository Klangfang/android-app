package com.wfm.soundcollaborations.Editor.model.composition;

import java.util.List;

public class Composition {

    public String title;

    public String creatorname;

    public List<Track> tracks;

    public String creationDate;

    public String status;

    public Integer numberOfParticipants;

    public Composition(String title, String creatorname, List<Track> tracks,
                       String creationDate, String status, Integer numberOfParticipants) {
        this.title = title;
        this.creatorname = creatorname;
        this.tracks = tracks;
        this.creationDate = creationDate;
        this.status = status;
        this.numberOfParticipants = numberOfParticipants;
    }
}
