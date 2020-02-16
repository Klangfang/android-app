package com.wfm.soundcollaborations.interaction.editor.model.audio;

import android.content.Context;

import java.util.List;


public final class SoundPlayer {

    private ExoPlayerFactory exoPlayerFactory;


    private SoundPlayer(Context context) {

        this.exoPlayerFactory = ExoPlayerFactory.build(context);

    }


    public static SoundPlayer build(Context context) {

        return new SoundPlayer(context);

    }


    public void addSounds(List<String> uris) {

        exoPlayerFactory.prepare(uris);

    }


    public void playOrPause(boolean play) {

        exoPlayerFactory.playOrPause(play);

    }


    public void seek(long positionsMs) {

        exoPlayerFactory.seek(positionsMs);

    }


    public void release() {

        exoPlayerFactory.release();

    }

}
