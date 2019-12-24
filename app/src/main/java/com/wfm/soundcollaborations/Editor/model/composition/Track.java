package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.DPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class Track {

    private int trackNumber;
    private List<Sound> sounds = new ArrayList<>();
    private AudioRecorder recorder;
    private int recorderTime;


    Track(int trackNumber) {

        this.trackNumber = trackNumber;
        recorder = new AudioRecorder();

    }


    List<Sound> getRecordedSounds() {

        return sounds.stream().filter(Sound::isRecorded).collect(Collectors.toList());

    }


    private void addRecordedSound(Sound sound) {

        recorderTime = recorderTime + sound.getDuration();
        sounds.add(sound);

    }


    void addSound(Sound sound, Context context) {

        sound.preparePlayer(context);
        sounds.add(sound);


    }

    public void addSounds(List<Sound> sounds) {
        this.sounds.addAll(sounds);
    }


    void prepare(Context context) throws NullPointerException {

        sounds.forEach(s -> s.preparePlayer(context));

    }


    void playOrPause(boolean pressPlay, int positionInMillis) {

        sounds.forEach(s -> s.playOrPause(pressPlay, positionInMillis));

    }


    void seek(int positionInMillis) {

        sounds.forEach(s -> s.seek(positionInMillis));

    }

    // after adding a new sound to the list of sounds, we sort our list of sounds again and create a new player with this.
    void prepareSound(Sound sound, Context context)
    {
        addRecordedSound(sound);

        sound.preparePlayer(context);

        //Sorting sounds:
        Collections.sort(sounds, (Sound s1, Sound s2) -> Long.valueOf(s1.getStartPosition()).compareTo(Long.valueOf(s2.getStartPosition())));
    }


    // Startet den Recorder
    void startTrackRecorder() throws RecordTimeOutExceededException {
        recorder.start();
    }

    // Stopt den Recorder
    void stopTrackRecorder() {
        recorder.stop();
    }

    // Liefert den Recorder-Status zurueck
    AudioRecorderStatus getTrackRecorderStatus() {
        return recorder.getStatus();
    }

    // Liefert den erzeugten Sound-Dateipfad zurueck
    String getFilePath() {
        return recorder.getRecordedFilePath();
    }

    // Liefert die maximale Amplitude im Recorder zurueck
    int getMaxAmplitude() {
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


    int deleteSounds(List<String> soundUUIDs) {

        //TODO why unterschiedliche uuids ?!!! Problem mit trackwatchview
        int soundsWidths = sounds.stream()
                .filter(sound -> soundUUIDs.contains(sound.uuid))
                .mapToInt(sound -> DPUtils.getValueInDP(sound.duration))
                .sum();

        sounds.removeIf(sound -> soundUUIDs.contains(sound.uuid));

        return soundsWidths;

    }

    boolean isPlaying() {

        return sounds.stream()
                .anyMatch(Sound::isPlaying);

    }

}
