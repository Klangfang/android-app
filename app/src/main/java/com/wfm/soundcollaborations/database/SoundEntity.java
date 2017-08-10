package com.wfm.soundcollaborations.database;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Markus Eberts on 13.10.16.
 */
@DatabaseTable(tableName = "sound")
public class SoundEntity {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String fileURI;

    @DatabaseField
    private Date creationDate;

    @DatabaseField
    private double latitude;

    @DatabaseField
    private double longitude;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<Integer> amplitudes;

    public SoundEntity(){ }

    public SoundEntity(String name, String fileURI) {
        this.name = name;
        this.fileURI = fileURI;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getFileURI() {
        return fileURI;
    }


    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }


    public Date getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public double getLatitude() {
        return latitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public double getLongitude() {
        return longitude;
    }


    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setGeolocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<Integer> getAmplitudes() {
        return amplitudes;
    }

    public void setAmplitudes(ArrayList<Integer> amplitudes) {
        this.amplitudes = amplitudes;
    }
}
