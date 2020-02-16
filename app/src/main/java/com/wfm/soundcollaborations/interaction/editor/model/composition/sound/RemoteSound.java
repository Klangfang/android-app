package com.wfm.soundcollaborations.interaction.editor.model.composition.sound;

import android.content.Context;

import com.wfm.soundcollaborations.interaction.editor.model.audio.SoundPlayer;

import java.util.Collections;


public class RemoteSound extends Sound {

    //BACKEND BEIM LADEN
    public Long id;

    //BACKEND BEIM LADEN
    public String title; // unique for saving files | has to be set dynamically

    public String filePath;


    public static class Builder {

        private long id;
        private int trackNumber;
        private int startPosition;
        private int duration;
        private String filePath;


        Builder id(long uuid) {

            this.id = uuid;
            return this;

        }


        public Builder trackNumber(int trackNumber) {

            this.trackNumber = trackNumber;
            return this;

        }


        public Builder startPosition(int startPosition) {

            this.startPosition = startPosition;
            return this;

        }


        public Builder duration(int duration) {

            this.duration = duration;
            return this;

        }


        public Builder filePath(String filePath) {

            this.filePath = filePath;
            return this;

        }


        public RemoteSound build() {

            return new RemoteSound(id, trackNumber, startPosition, duration, filePath);

        }

    }


    public RemoteSound() {
    }

    private RemoteSound(Long id, Integer trackIndex, Integer startPosition, Integer duration, String filePath) {

        this.id = id;
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

        return false;

    }

}
