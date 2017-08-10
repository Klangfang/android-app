package com.wfm.soundcollaborations.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Markus Eberts on 13.10.16.
 */
@DatabaseTable(tableName = "tag")
public class TagEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField(foreign = true, foreignAutoRefresh=true)
    private SoundEntity sound;

    public TagEntity(){ }


    public TagEntity(String name, SoundEntity sound) {
        this.name = name;
        this.sound = sound;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public SoundEntity getSound() {
        return sound;
    }


    public void setSound(SoundEntity sound) {
        this.sound = sound;
    }
}
