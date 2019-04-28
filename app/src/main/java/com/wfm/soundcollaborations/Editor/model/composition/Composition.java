package com.wfm.soundcollaborations.Editor.model.composition;

import java.util.List;

public class Composition {

    public Long id;

    public String title;

    public String creatorname;

    public List<Track> tracks;

    public String creationDate;

    public String status;

    public Integer numberOfParticipants;

    public Composition(Long id, String title, String creatorname, List<Track> tracks,
                       String creationDate, String status, Integer numberOfParticipants) {
        this.id = id;
        this.title = title;
        this.creatorname = creatorname;
        this.tracks = tracks;
        this.creationDate = creationDate;
        this.status = status;
        this.numberOfParticipants = numberOfParticipants;
    }
}
