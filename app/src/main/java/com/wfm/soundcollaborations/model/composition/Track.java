package com.wfm.soundcollaborations.model.composition;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wfm.soundcollaborations.model.audio.AudioPlayer;
import com.wfm.soundcollaborations.model.audio.AudioRecorder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private AudioRecorder audioRecorder = new AudioRecorder();


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
        // den Player starten, wenn an der Stelle einen Sound vorhanden ist.
        // Der Player selbst weiss nicht, wo die Sounds sich befinden.
        if (isThereSound(positionInMillis)) {
            playSound(trackNumber, positionInMillis);
        } else {
            pause(trackNumber, positionInMillis);
        }
    }

    // Play sound
    private void playSound(int trackNumber, int positionInMillis)
    {
        if (!isPlaying()) {
            player.play();
            Log.d(TAG, "Track "+trackNumber+" is Playing in "+positionInMillis);
        }
    }

    private void pause(int trackNumber, int positionInMillis)
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

    // after adding a new sound to the list of sounds, we sort our list of sounds again and create a new player with this.
    public void prepareSound(Sound sound)
    {
        addSound(sound);

        //Sorting sounds:
        Collections.sort(sounds, (Sound s1, Sound s2) -> Integer.valueOf(s1.getStartPosition()).compareTo(Integer.valueOf(s2.getStartPosition())));

        // add new sounds to the player
        player.addSounds(getSoundUris());
    }

    private String[] getSoundUris() {
        String[] soundsUris = new String[sounds.size()];

        int i = 0;
        for (Sound sound : sounds) {
            soundsUris[i] = (sound.getUri());
            i++;
        }
        return soundsUris;
    }

    public AudioRecorder getAudioRecorder() {
        return audioRecorder;
    }
}
