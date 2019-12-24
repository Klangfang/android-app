package com.wfm.soundcollaborations.Editor.model.audio;

import android.content.Context;

import java.util.List;


public class SoundPlayer {

    private Context context;
    private ExoPlayerFactory exoPlayerFactory;


    public SoundPlayer(Context context) {

        this.context = context;
        buildExoPlayer();

    }

    private void buildExoPlayer() {

        exoPlayerFactory = new ExoPlayerFactory();
        exoPlayerFactory.createExoPlayer(context);

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

    public void reset() {

        exoPlayerFactory.reset();

    }

    public void release() {

        exoPlayerFactory.release();

    }

}
