package com.wfm.soundcollaborations.Editor.model.composition.sound;

import android.content.Context;

import com.wfm.soundcollaborations.Editor.model.audio.SoundPlayer;

import java.util.Collections;


public class LocalSound extends Sound {

    public String filePath;


    public static class Builder {

        private String uuid;
        private Integer trackNumber;
        private Integer startPosition;
        private Integer duration;
        private String filePath;


        public Builder uuid(String uuid) {

            this.uuid = uuid;
            return this;

        }


        public Builder trackNumber(Integer trackNumber) {

            this.trackNumber = trackNumber;
            return this;

        }


        public Builder startPosition(Integer startPosition) {

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


        public LocalSound build() {

            return new LocalSound(uuid, trackNumber, startPosition, duration, filePath);

        }

    }


    public LocalSound() {
    }

    private LocalSound(String uuid, Integer trackIndex, Integer startPosition, Integer duration, String filePath) {

        this.uuid = uuid;
        this.trackIndex = trackIndex;
        this.startPosition = startPosition;
        this.duration = duration;
        this.filePath = filePath;
        this.creatorName = "talal"; // TODO it has to be set dynamically

    }


    @Override
    public void preparePlayer(Context context) {

        player = SoundPlayer.build(context);
        player.addSounds(Collections.singletonList(filePath));

    }


    @Override
    public void releasePlayer() {

        player.release();

    }


    @Override
    public boolean isLocalSound() {

        return true;

    }


    public String getUuid() {

        return uuid;

    }

}
