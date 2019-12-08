package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Track {

    private List<Sound> sounds = new ArrayList<>();
    private AudioRecorder recorder;
    private int recorderTime;


    public Track(){ recorder = new AudioRecorder(); }


    public List<Sound> getRecordedSounds() {

        return sounds.stream().filter(Sound::isRecored).collect(Collectors.toList());

    }


    public void addRecordedSound(Sound sound) {

        recorderTime = recorderTime + sound.getDuration();
        sounds.add(sound);

    }

    public void addSound(Sound sound, Context context) {

        sound.preparePlayer(context);
        sounds.add(sound);


    }

    public void addSounds(List<Sound> sounds) {
        this.sounds.addAll(sounds);
    }

    public void prepare(Context context) throws NullPointerException {
        for (Sound sound : sounds) {
            sound.preparePlayer(context);
        }

    }

    public void play(int trackNumber, int positionInMillis) {
        for (Sound sound : sounds) {
            sound.play(trackNumber, positionInMillis);
        }
    }

    public void pause(int trackNumber) {
        for (Sound sound : sounds) {
            sound.pause(trackNumber);
        }
    }

    public void seek(int positionInMillis) {
        for (Sound sound : sounds) {
            sound.seek(positionInMillis);
        }
    }

    // after adding a new sound to the list of sounds, we sort our list of sounds again and create a new player with this.
    public void prepareSound(Sound sound, Context context)
    {
        addRecordedSound(sound);

        sound.preparePlayer(context);

        //Sorting sounds:
        Collections.sort(sounds, (Sound s1, Sound s2) -> Long.valueOf(s1.getStartPosition()).compareTo(Long.valueOf(s2.getStartPosition())));
    }

    // Startet den Recorder
    public void startTrackRecorder() throws RecordTimeOutExceededException {
        recorder.start();
    }

    // Stopt den Recorder
    public void stopTrackRecorder() {
        recorder.stop();
    }

    // Liefert den Recorder-Status zurueck
    public AudioRecorderStatus getTrackRecorderStatus() {
        return recorder.getStatus();
    }

    // Liefert den erzeugten Sound-Dateipfad zurueck
    public String getFilePath() {
        return recorder.getRecordedFilePath();
    }

    // Liefert die maximale Amplitude im Recorder zurueck
    public int getMaxAmplitude() {
        return recorder.getMaxAmplitude();
    }

    public List<Sound> getSounds() {
        return sounds;
    }

    public void deleteSound(Sound soundToDelete) {
        recorder.increaseTime(soundToDelete.getDuration());
        sounds.remove(soundToDelete);
    }

    public Integer getDuration() {
        return recorder.getDuration();
    }


    public Map<String, Integer> deleteSounds(List<String> soundUUIDs) {

        Map<String, Integer> soundsWidth = sounds.stream()
                .filter(sound -> soundUUIDs.contains(sound.uuid))
                .collect(Collectors.toMap(Sound::getUuid, Sound::calculateWidth));

        sounds.removeIf(sound -> soundUUIDs.contains(sound.uuid));

        return soundsWidth;

    }
}
