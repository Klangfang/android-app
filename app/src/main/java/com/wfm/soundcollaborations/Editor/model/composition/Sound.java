package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.AudioPlayer;

import java.time.LocalDate;

/**
 * Created by mohammed on 10/27/17.
 * Edited by Talal
 */

public class Sound {

    private static final String TAG = Sound.class.getSimpleName();

    private Integer trackNumber;

    private String title;

    private Integer startPosition;
    private Integer duration;

    private String creatorName;

    private String filePath;
    
    private AudioPlayer player;

    // TODO think about where to move
    private final String COMPOSITION_FILES_URL = "https://klangfang-service.herokuapp.com/compositions/";


    public Sound(Integer trackNumber, String title, Integer startPosition, Integer duration, String filePath) {
        this.trackNumber = trackNumber;
        this.title = title;
        this.startPosition = startPosition;
        this.duration = duration;
        this.filePath = filePath;
    }

    public Sound(Integer trackNumber, Integer startPosition, Integer duration, String filePath) {
        this.trackNumber = trackNumber;
        this.startPosition = startPosition;
        this.duration = duration;
        this.filePath = filePath;
    }

    public Sound(Integer trackNumber, String title, Integer startPosition, Integer duration,
                 String creatorName, String filePath) {
        this.trackNumber = trackNumber;
        this.title = title;
        this.startPosition = startPosition;
        this.duration = duration;
        this.creatorName = creatorName;
        this.filePath = COMPOSITION_FILES_URL + filePath + "?compositionId=1";
    }

    public void prepare(Context context) {
        player = new AudioPlayer(context);
        player.addSounds(new String[]{filePath});
    }

    public void play(int trackNumber, int positionInMillis) {
        // Den Player starten, wenn an der Stelle einen Sound vorhanden ist.
        // Der Player selbst weiss nicht, wo die Sounds sich befinden.
        long endPosition = startPosition + duration;
        if (startPosition <= positionInMillis && endPosition > positionInMillis) {
            if (!isPlaying()) {
                player.play();

                Log.d(TAG, "Track "+trackNumber+" is Playing in "+positionInMillis);
            }
        } else {
            player.pause();

            Log.d(TAG, "Track " + trackNumber + " is Paused in " + positionInMillis);
        }
    }

    public void pause(int trackNumber) {
        player.pause();

        Log.d(TAG, "Track " + trackNumber + " is Paused");
    }

    private boolean isPlaying()
    {
        return player.isPlaying();
    }

    public void seek(long positionInMillis) {
        long seekingPosition = calculateSeekingTimeForPlayer(positionInMillis);
        Log.d(TAG, "Track seeking Time is => "+seekingPosition);
        player.seek(seekingPosition);
    }


    private long calculateSeekingTimeForPlayer(long positionInMillis) {
        long maxPoint = positionInMillis;
        long soundsInside = 0;

        long endPosition = startPosition + duration;
            if(startPosition <= positionInMillis && endPosition > positionInMillis) {
                maxPoint = startPosition;
            }

            if(endPosition <= positionInMillis) {
                soundsInside += duration;
            }

        return positionInMillis - (maxPoint - soundsInside);
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getFilePath() {
        return filePath;
    }
}
