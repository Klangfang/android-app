package com.wfm.soundcollaborations.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Markus Eberts on 22.10.16.
 */
@DatabaseTable(tableName = "friend")
public class FriendEntity {

    @DatabaseField(id = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String contactName;


    public FriendEntity(){}


    public FriendEntity(long id, String name){
        this.id = id;
        this.name = name;
    }


    public FriendEntity(long id, String name, String contactName){
        this.id = id;
        this.name = name;
        this.contactName = contactName;
    }


    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getContactName() {
        return contactName;
    }


    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        FriendEntity other = (FriendEntity) obj;
        return id.equals(other.id);
    }
}
