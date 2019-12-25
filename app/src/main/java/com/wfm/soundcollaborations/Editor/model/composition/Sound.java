package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.SoundPlayer;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;


public class Sound {

    private static final String TAG = Sound.class.getSimpleName();

    private static final boolean PLAY = true;
    private static final boolean PAUSE = false;

    //BACKEND BEIM LADEN
    public Long id;

    public Integer trackIndex;

    //BACKEND BEIM LADEN
    public String title; // unique for saving files | has to be set dynamically

    public Integer startPosition;

    public Integer duration;

    private String creatorName;

    public String filePath;

    private SoundPlayer player;

    // only for new sounds is important TODO later separate SoundDownloaded and SoundRecorded class
    String uuid;

    private boolean playing;

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


        Builder filePath(String filePath) {

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

        return Objects.nonNull(uuid);

    }


    void preparePlayer(Context context) {
        player = new SoundPlayer(context);
        player.addSounds(Collections.singletonList(filePath));
    }


    void playOrPause(boolean pressPlay, int positionInMS) {

        // Start the player, if the current position has sounds.
        long endPosition = startPosition + duration;
        boolean currentPositionHasSound = startPosition <= positionInMS && endPosition > positionInMS;

        if (pressPlay) {

            if (!playing && currentPositionHasSound) {

                player.playOrPause(PLAY);
                playing = PLAY;

            }

        } else {

            player.playOrPause(PAUSE);
            playing = PAUSE;

        }

        String status = playing ? "Playing" : "Paused";

        Log.d(TAG, String.format("Track %d is %s in  %s", trackIndex, status, positionInMS));

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

    public String getCreatorName() {
        return creatorName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getUuid() {
        return uuid;
    }


    boolean isPlaying() {

        return playing;

    }
}
