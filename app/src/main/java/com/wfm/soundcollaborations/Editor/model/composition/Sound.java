package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.util.Log;

import com.wfm.soundcollaborations.Editor.model.audio.AudioPlayer;
import com.wfm.soundcollaborations.Editor.views.composition.SoundView;

import java.util.Date;

/**
 * Created by mohammed on 10/27/17.
 */

public class Sound
{
    private static final String TAG = Sound.class.getSimpleName();

    private String uri;
    private String link;
    private int length;
    private int track;
    private int startPosition;
    private AudioPlayer player;
    private SoundView soundView;

    public Sound(String link, int length, int track, int startPosition, String uri)
    {
       initSound(link, length, track, startPosition, uri);
    }

    public Sound(String link, int length, int track, int startPosition, String uri, SoundView soundView)
    {
        initSound(link, length, track, startPosition, uri);
        this.soundView = soundView;
    }

    private void initSound(String link, int length, int track, int startPosition, String uri) {
        this.link = link;
        this.length = length;
        this.track = track;
        this.startPosition = startPosition;
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
        int endPosition = startPosition + length;
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

    public void pause(int trackNumber)
    {
        player.pause();

        Log.d(TAG, "Track " + trackNumber + " is Paused");
    }

    private boolean isPlaying()
    {
        return player.isPlaying();
    }

    public void seek(int positionInMillis)
    {
        int seekingPosition = calculateSeekingTimeForPlayer(positionInMillis);
        Log.d(TAG, "Track seeking Time is => "+seekingPosition);
        player.seek(seekingPosition);
    }


    private int calculateSeekingTimeForPlayer(int positionInMillis) {
        int maxPoint = positionInMillis;
        int soundsInside = 0;

            int endPosition = startPosition + length;
            if(startPosition <= positionInMillis && endPosition > positionInMillis) {
                maxPoint = startPosition;
            }

            if(endPosition <= positionInMillis) {
                soundsInside += length;
            }

        return positionInMillis - (maxPoint - soundsInside);
    }

    public int getLength()
    {
        return this.length;
    }

    public String getLink()
    {
        return this.link;
    }

    public int getTrack()
    {
        return this.track;
    }

    public int getStartPosition()
    {
        return this.startPosition;
    }

    public String getUri() throws NullPointerException
    {
        if(this.uri.isEmpty())
            throw new NullPointerException("No Uri found for the sound file!");
        return this.uri;
    }

    public SoundView getSoundView() {
        return soundView;
    }
}
