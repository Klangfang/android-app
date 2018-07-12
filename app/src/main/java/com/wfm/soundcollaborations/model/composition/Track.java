package com.wfm.soundcollaborations.model.composition;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.wfm.soundcollaborations.model.audio.AudioPlayer;
import com.wfm.soundcollaborations.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.utils.AudioRecorderStatus;

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
    private AudioRecorder recorder;
    private int recorderTime;


    public Track(){ recorder = new AudioRecorder(); }

    public void addSound(Sound sound)
    {
        recorderTime = recorderTime + sound.getLength();
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
        // Den Player starten, wenn an der Stelle einen Sound vorhanden ist.
        // Der Player selbst weiss nicht, wo die Sounds sich befinden.
        if (isThereSound(positionInMillis)) {
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
        int endPosition;
        for(int i=0; i<sounds.size(); i++)
        {
            startPosition = sounds.get(i).getStartPosition();
            endPosition = startPosition+sounds.get(i).getLength();
            if(positionInMillis >= startPosition && positionInMillis < endPosition)
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
            soundsUris[i] = sound.getUri();
            i++;
        }
        return soundsUris;
    }

    // Startet den Recorder
    public void startTrackRecorder() {
        recorder.start();
    }

    // Stopt den Recorder
    public void stopTrackRecorder(int recorderTime) {
        recorder.stop(recorderTime);
    }

    // Liefert den Recorder-Status zurueck
    public AudioRecorderStatus getTrackRecorderStatus() {
        return recorder.getStatus();
    }

    // Liefert den erzeugten Sound-Dateipfad zurueck
    public String getRecordedFilePath() {
        return recorder.getRecordedFilePath();
    }

    // Liefert die maximale Amplitude im Recorder zurueck
    public int getMaxAmplitude() {
        return recorder.getMaxAmplitude();
    }

    public List<Sound> getSounds() {
        return sounds;
    }


}
