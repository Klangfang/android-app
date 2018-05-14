package com.wfm.soundcollaborations.model.composition;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wfm.soundcollaborations.model.audio.AudioPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by mohammed on 10/27/17.
 */

public class Track
{
    private static final String TAG = Track.class.getSimpleName();

    private List<Sound> sounds = new ArrayList<>();
    private AudioPlayer player;


    public Track(){}

    public void addSound(Sound sound)
    {
        sounds.add(sound);
    }

    public void prepare(Context context) throws NullPointerException
    {
        player = new AudioPlayer(context);
        String soundsUris[] = new String[this.sounds.size()];
        for(int i=0; i<sounds.size(); i++)
        {
            soundsUris[i] = this.sounds.get(i).getUri();
        }
        player.addSounds(soundsUris);
    }

    public void play(int trackNumber, int positionInMillis)
    {
        if (!isPlaying() && isThereSound(positionInMillis)) {
            player.play();
            Log.d(TAG, "Track "+trackNumber+" is Playing in "+positionInMillis);
            return;
        }
        if (isPlaying() && !isThereSound(positionInMillis)){
            pause(trackNumber, positionInMillis);
            return;
        }
    }

    public void pause(int trackNumber, int positionInMillis)
    {
        player.pause();
        if (positionInMillis!=-1) {
            Log.d(TAG, "Track " + trackNumber + " is Paused in " + positionInMillis);
        } else {
            Log.d(TAG, "Track " + trackNumber + " is Paused");
        }

    }

    public void pause(int trackNumber)
    {
       pause(trackNumber, -1);
    }

    public boolean isPlaying()
    {
        return player.isPlaying();
    }

    public void seek(int positionInMillis)
    {
        int seekingPosition = calculateSeekingTimeForPlayer(positionInMillis);
        Log.d(TAG, "Track seeking Time is => "+seekingPosition);
        player.seek(seekingPosition);
    }

    private int calculateSeekingTimeForPlayer(int positionInMillis)
    {
        int maxPoint = positionInMillis;
        int soundsInside = 0;
        for(int i=0; i<sounds.size(); i++)
        {
            if( sounds.get(i).getStartPosition() <= positionInMillis &&
                    sounds.get(i).getStartPosition() + sounds.get(i).getLength() > positionInMillis)
            {
                maxPoint = sounds.get(i).getStartPosition();
            }

            if(sounds.get(i).getStartPosition() + sounds.get(i).getLength() <= positionInMillis)
            {
                soundsInside += sounds.get(i).getLength();
            }
        }
        return positionInMillis - (maxPoint - soundsInside);
    }

    public boolean isThereSound(int positionInMillis)
    {
        int startPosition;
        for(int i=0; i<sounds.size(); i++)
        {
            startPosition = sounds.get(i).getStartPosition();
            if(positionInMillis >= startPosition && positionInMillis < (startPosition+sounds.get(i).getLength()))
                return true;
        }
        return false;
    }
}
