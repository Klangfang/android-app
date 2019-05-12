package com.wfm.soundcollaborations.Editor.model.composition;

import java.util.List;

public class Composition {

    public String title;

    public String creatorName;

    public List<Sound> sounds;

    public String creationDate;

    public String status;

    public Integer numberOfMembers;

    public Composition(String title, String creatorName, List<Sound> sounds,
                       String creationDate, String status, Integer numberOfMembers) {
        this.title = title;
        this.creatorName = creatorName;
        this.sounds = sounds;
        this.creationDate = creationDate;
        this.status = status;
        this.numberOfMembers = numberOfMembers;
    }
}
