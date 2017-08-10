package com.wfm.soundcollaborations.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Markus Eberts on 27.10.16.
 */
@DatabaseTable(tableName = "randomSong")
public class RandomSongEntity {

    @DatabaseField(id = true)
    private long id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    public RandomSongEntity(){}

    public RandomSongEntity(String title, String description){
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
