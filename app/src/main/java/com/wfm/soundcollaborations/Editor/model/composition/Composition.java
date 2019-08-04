package com.wfm.soundcollaborations.Editor.model.composition;

import java.util.List;

public class Composition {

    public Long id;

    public String title;

    public String creatorName;

    public List<Sound> sounds;

    //BACKEND BEIM LADEN
    public String creationDate;

    //BACKEND BEIM LADEN
    public String status;

    //BACKEND BEIM LADEN
    public Integer numberOfMembers;

    //BACKEND BEIM LADEN
    public Integer duration;

    //BACKEND BEIM LADEN
    public String snippet;

}
