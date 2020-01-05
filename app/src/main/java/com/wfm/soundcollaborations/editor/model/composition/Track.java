package com.wfm.soundcollaborations.editor.model.composition;

import android.content.Context;

import com.wfm.soundcollaborations.editor.model.audio.AudioRecorder;
import com.wfm.soundcollaborations.editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.editor.model.composition.sound.Sound;
import com.wfm.soundcollaborations.editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.editor.utils.DPUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class Track {

    private int trackNumber;
    private List<Sound> sounds = new ArrayList<>();
    private AudioRecorder recorder;
    private int recorderTime;


    Track(int trackNumber) {

        this.trackNumber = trackNumber;
        recorder = new AudioRecorder();

    }


    void addLocalSound(Context context, LocalSound sound) {

        recorderTime = recorderTime + sound.getDuration();
        sound.preparePlayer(context);
        sounds.add(sound);

        //Sorting sounds:
        Collections.sort(sounds, (Sound s1, Sound s2) -> Long.valueOf(s1.getStartPosition()).compareTo(Long.valueOf(s2.getStartPosition())));

    }


    void addRemoteSound(Context context, RemoteSound sound) {

        sound.preparePlayer(context);
        sounds.add(sound);

        //Sorting sounds:
        Collections.sort(sounds, (Sound s1, Sound s2) -> Long.valueOf(s1.getStartPosition()).compareTo(Long.valueOf(s2.getStartPosition())));

    }


    void playOrPause(boolean pressPlay, int positionInMillis) {

        sounds.forEach(s -> s.playOrPause(pressPlay, positionInMillis));

    }


    void seek(int positionInMillis) {

        sounds.forEach(s -> s.seek(positionInMillis));

    }


    // Startet den Recorder
    void startTrackRecorder(int startTime) {
        recorder.start(startTime);
    }

    // Stopt den Recorder
    void stopTrackRecorder(int endTime) {
        recorder.stop(endTime);
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

    public Integer getDuration() {
        return recorder.getDuration();
    }


    int increaseTime(List<String> soundUUIDs) {

        int allSoundsDuration = sounds.stream()
                .filter(s -> s.isLocalSound() && soundUUIDs.contains(s.getUuid()))
                .mapToInt(s -> s.duration)
                .sum();

        recorder.increaseTime(allSoundsDuration);

        return DPUtils.getValueInDP(allSoundsDuration);

    }


    void deleteSounds(List<String> soundUUIDs) {

        //TODO everything in one loop?
        Predicate<? super Sound> soundToDelete = s -> s.isLocalSound() && soundUUIDs.contains(s.getUuid());
        sounds.stream()
                .filter(soundToDelete)
                .forEach(Sound::releasePlayer);
        sounds.removeIf(soundToDelete);

    }


    boolean isPlaying() {

        return sounds.stream()
                .anyMatch(Sound::isPlaying);

    }


    int getTrackNumber() {

        return trackNumber;

    }


    Stream<LocalSound> getLocalSounds() {

        return sounds.stream()
                .filter(Sound::isLocalSound)
                .map(LocalSound.class::cast);

    }


    void preDestroy() {

        sounds.forEach(Sound::releasePlayer);

    }
}
