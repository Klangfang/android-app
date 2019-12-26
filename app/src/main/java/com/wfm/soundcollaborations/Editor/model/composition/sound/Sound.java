package com.wfm.soundcollaborations.Editor.model.composition.sound;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.SoundPlayer;


public abstract class Sound {

    private static final String TAG = Sound.class.getSimpleName();

    private static final boolean PLAY = true;
    private static final boolean PAUSE = false;

    // only for new sounds is important TODO later separate RemoteSound and LocalSound class
    String uuid;

    public Integer trackIndex;

    public Integer startPosition;

    public Integer duration;

    String creatorName;

    SoundPlayer player;
    private boolean playing;


    public void playOrPause(boolean pressPlay, int positionInMS) {

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


    public void seek(long positionInMillis) {

        long seekingPosition = calculateSeekingTimeForPlayer(positionInMillis);
        player.seek(seekingPosition);

        Log.d(TAG, String.format("Track seeking time is => %d", seekingPosition));

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


    public abstract void preparePlayer(Context context);


    public abstract boolean isLocalSound();


    public Integer getTrackIndex() {

        return trackIndex;

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


    public boolean isPlaying() {

        return playing;

    }

    public String getUuid() {

        return uuid;

    }
}
