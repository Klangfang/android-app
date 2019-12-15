package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.AudioPlayer;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static com.wfm.soundcollaborations.Editor.utils.DPUtils.SOUND_SECOND_WIDTH;

/**
 * Created by mohammed on 10/27/17.
 * Edited by Talal
 */

public class Sound {

    private static final String TAG = Sound.class.getSimpleName();

    //BACKEND BEIM LADEN
    public Long id;

    public Integer trackIndex;

    //BACKEND BEIM LADEN
    public String title; // unique for saving files | has to be set dynamically

    public Integer startPosition;

    public Integer duration;

    private String creatorName;

    public String filePath;

    private AudioPlayer player;

    // only for new sounds is important TODO later separate SoundDownloaded and SoundRecorded class
    String uuid;

    public static class Builder {

        private String uuid;
        private Integer trackNumber;
        private Integer startPosition;
        private Integer duration;
        private String filePath;


        Builder uuid(String uuid) {

            this.uuid = uuid;
            return this;

        }


        Builder trackNumber(Integer trackNumber) {

            this.trackNumber = trackNumber;
            return this;

        }


        Builder startPosition(Integer startPosition) {

            this.startPosition = startPosition;
            return this;

        }


        public Builder duration(Integer duration) {

            this.duration = duration;
            return this;

        }


        public Builder filePath(String filePath) {

            this.filePath = filePath;
            return this;

        }


        public Sound build() {

            return new Sound(uuid, trackNumber, startPosition, duration, filePath);

        }

    }


    private Sound() {

    }


    private Sound(Long id, Integer trackIndex, Integer startPosition, Integer duration, String filePath) {

        this.id = id;
        this.trackIndex = trackIndex;
        this.startPosition = startPosition;
        this.duration = duration;
        this.filePath = filePath;
        this.creatorName = "talal"; // TODO it has to be set dynamically
        uuid = UUID.randomUUID().toString();

    }


    private Sound(String uuid, Integer trackIndex, Integer startPosition, Integer duration, String filePath) {

        this.uuid = uuid;
        this.trackIndex = trackIndex;
        this.startPosition = startPosition;
        this.duration = duration;
        this.filePath = filePath;
        this.creatorName = "talal"; // TODO it has to be set dynamically

    }


    boolean isRecorded() {

        return Objects.isNull(id);

    }


    void preparePlayer(Context context) {
        player = new AudioPlayer(context);
        player.addSounds(Arrays.asList(filePath));
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

    void seek(long positionInMillis) {
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

    public Integer getTrackIndex() {
        return trackIndex;
    }

    //public String getTitle() {
      //  return title;
    //}

    Integer getStartPosition() {
        return startPosition;
    }

    public Integer getDuration() {

        return duration;

    }


    Integer calculateWidth() {

        long duration = getDuration();
        int width = 0;
        width += (duration / 1000) * SOUND_SECOND_WIDTH;
        width += (duration % 1000) * SOUND_SECOND_WIDTH / 1000;
        return width;

    }

    public String getCreatorName() {
        return creatorName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUuid() {
        return uuid;
    }
}
