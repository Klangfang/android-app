package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.AudioPlayer;

/**
 * Created by mohammed on 10/27/17.
 */

public class Sound
{
    private static final String TAG = Sound.class.getSimpleName();

    private String uri;
    private String link;
    private int lengthInMs;
    private int track;
    private int startPositionInMs;
    private AudioPlayer player;

    public Sound(String link, int lengthInMs, int track, int startPositionInMs, String uri)
    {
        this.link = link;
        this.lengthInMs = lengthInMs;
        this.track = track;
        this.startPositionInMs = startPositionInMs;
        this.uri = uri;
    }

    public void prepare(Context context) throws NullPointerException
    {
        player = new AudioPlayer(context);
        player.addSounds(new String[]{uri});
    }

    public void play(int trackNumber, int positionInMillis)
    {
        // Den Player starten, wenn an der Stelle einen Sound vorhanden ist.
        // Der Player selbst weiss nicht, wo die Sounds sich befinden.
        long endPosition = startPositionInMs + lengthInMs;
        if (startPositionInMs <= positionInMillis && endPosition > positionInMillis) {
            if (!isPlaying()) {
                player.play();

                Log.d(TAG, "Track "+trackNumber+" is Playing in "+positionInMillis);
            }
        } else {
            player.pause();

            Log.d(TAG, "Track " + trackNumber + " is Paused in " + positionInMillis);
        }
    }

    public void pause(int trackNumber)
    {
        player.pause();

        Log.d(TAG, "Track " + trackNumber + " is Paused");
    }

    private boolean isPlaying()
    {
        return player.isPlaying();
    }

    public void seek(long positionInMillis)
    {
        long seekingPosition = calculateSeekingTimeForPlayer(positionInMillis);
        Log.d(TAG, "Track seeking Time is => "+seekingPosition);
        player.seek(seekingPosition);
    }


    private long calculateSeekingTimeForPlayer(long positionInMillis) {
        long maxPoint = positionInMillis;
        long soundsInside = 0;

        long endPosition = startPositionInMs + lengthInMs;
            if(startPositionInMs <= positionInMillis && endPosition > positionInMillis) {
                maxPoint = startPositionInMs;
            }

            if(endPosition <= positionInMillis) {
                soundsInside += lengthInMs;
            }

        return positionInMillis - (maxPoint - soundsInside);
    }

    public int getLengthInMs()
    {
        return this.lengthInMs;
    }

    public String getLink()
    {
        return this.link;
    }

    public int getTrack()
    {
        return this.track;
    }

    public long getStartPositionInMs()
    {
        return this.startPositionInMs;
    }

    public String getUri() throws NullPointerException
    {
        if(this.uri.isEmpty())
            throw new NullPointerException("No Uri found for the sound file!");
        return this.uri;
    }

}
