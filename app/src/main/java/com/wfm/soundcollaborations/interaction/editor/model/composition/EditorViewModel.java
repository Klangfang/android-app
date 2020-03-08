package com.wfm.soundcollaborations.interaction.editor.model.composition;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.wfm.soundcollaborations.CompositionRepository;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.Sound;
import com.wfm.soundcollaborations.interaction.editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.SoundResponse;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.inject.Inject;

import static com.wfm.soundcollaborations.interaction.editor.utils.DPUtils.getPositionInMs;
import static com.wfm.soundcollaborations.interaction.editor.utils.DPUtils.overlapConstraintsViolation;
import static com.wfm.soundcollaborations.interaction.editor.utils.DPUtils.soundHasReachedMaxLength;

public class EditorViewModel extends ViewModel {

    private static final String TAG = EditorViewModel.class.getSimpleName();

    private static final String CREATOR_NAME_HAS_TO_BE_DEFINED = "KLANGFANG";

    private Composition composition;

    private final CompositionRepository compositionRepository;
    private Consumer<String> callback;

    private int startPositionInDP = 0;

    private RecordTaskScheduler recordTaskScheduler;
    private PlayTaskScheduler playTaskScheduler;


    @Inject
    public EditorViewModel(CompositionRepository compositionRepository) {

        this.compositionRepository = compositionRepository;

        playTaskScheduler = new PlayTaskScheduler();
        recordTaskScheduler = new RecordTaskScheduler();

    }


    @Override
    protected void onCleared() {

        super.onCleared();

        preDestroy();

        //requestCancel();//TODO

    }


    public void createNewComposition(String compositionTitle) {

        composition = new Composition.CompositionConfigurer()
                .title(compositionTitle)
                .build();

    }


    public List<RemoteSound> loadComposition(Context applicationContext, String compositionResponseJson) {

        List<RemoteSound> remoteSounds = new ArrayList<>();

        if (StringUtils.isNotBlank(compositionResponseJson)) {

            CompositionResponse compositionResponse = JsonUtil.fromJson(compositionResponseJson, CompositionResponse.class);
            if (compositionResponse != null) {

                composition = new Composition.CompositionConfigurer()
                        .compositionId(compositionResponse.id)
                        .title(compositionResponse.title)
                        .collaboration()
                        .build();

                for (SoundResponse sndResp : compositionResponse.sounds) {

                    Integer trackNumber = sndResp.trackNumber;
                    Track track = composition.getTrack(trackNumber);

                    RemoteSound sound = new RemoteSound.Builder()
                            .trackNumber(sndResp.trackNumber)
                            .startPosition(sndResp.startPosition)
                            .duration(sndResp.duration)
                            .filePath(sndResp.url)
                            .build();

                    track.addRemoteSound(applicationContext, sound);
                    composition.updateTrack(trackNumber, track);

                    remoteSounds.add(sound);
                }

            }

        }

        return remoteSounds;

    }


    private void preDestroy() {

        stopRecording();
        stopPlaying();

    }


    public void requestCancel() {

        preDestroy();

        composition.cancel();

        if (composition.isCollaboration()) {

            compositionRepository.cancel(composition.getCompositionId(), callback);

        }

    }


    public void requestCreate() {

        preDestroy();

        composition.cancel();

        compositionRepository.create(composition.getTitle(), CREATOR_NAME_HAS_TO_BE_DEFINED, composition.getLocalSounds(), callback);

    }


    public void requestJoin() {

        preDestroy();

        composition.cancel();

        compositionRepository.join(composition.getCompositionId(), composition.getLocalSounds(), callback);

    }


    public boolean isCompositionNotCanceled() {

        return composition.isNotCanceled();

    }


    public Map<Integer, Integer> deleteSounds(List<String> soundUUIDs) {

        Map<Integer, Integer> valuesToRecover = new HashMap<>();

        composition.getTracks().forEach(t -> {

            int soundWidths = t.increaseTime(soundUUIDs);

            valuesToRecover.put(t.getTrackNumber(), soundWidths);

            t.deleteSounds(soundUUIDs);

        });

        return valuesToRecover;

    }


    private void stopPlaying() {

        if (isPlaying()) { //TODO check in acitivy

            composition.getTracks().forEach(Track::preDestroy);

            playTaskScheduler.stop();

        }

    }


    public void startRecording(int activeTrackIndex, int startPositionInDP, Runnable callback) {


        this.startPositionInDP = startPositionInDP;

        //start track recorder
        Track track = composition.getTrack(activeTrackIndex);
        track.startTrackRecorder(getPositionInMs(startPositionInDP));
        composition.updateTrack(activeTrackIndex, track);

        recordTaskScheduler.record(callback);

    }


    public void completeLocalSound(Context applicationContext, int activeTrackIndex, int scrollPositionInDP, String uuid) {

        Track track = composition.getTrack(activeTrackIndex);
        track.stopTrackRecorder(getPositionInMs(scrollPositionInDP));
        Integer duration = track.getDuration();
        String filePath = track.getFilePath();

        if (filePath != null) {

            LocalSound sound = new LocalSound.Builder()
                    .uuid(uuid)
                    .trackNumber(activeTrackIndex)
                    .startPosition(getPositionInMs(startPositionInDP))
                    .duration(duration)
                    .filePath(filePath)
                    .build();
            track.addLocalSound(applicationContext, sound);

        }

        composition.updateTrack(activeTrackIndex, track);

    }


    public boolean canRecord(int activeTrackIndex, int scrollPositionInDP) {

        return composition.isTrackNotExhausted(activeTrackIndex) && checkStop(activeTrackIndex, scrollPositionInDP).equals(StopReason.NO_STOP);

    }


    public void stopRecording() {

        if (isRecording()) {

            recordTaskScheduler.stop();

        }

    }


    public StopReason checkStop(int activeTrackIndex, int scrollPositionInDP) {

        if (isRecording()) {

            Track track = composition.getTrack(activeTrackIndex);

            if (track.getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {

                stopRecording();

                composition.exhaustTrack(track.getTrackNumber());

                return StopReason.MAXIMUM_RECORDING_TIME_REACHED;

            }

            if (soundHasReachedMaxLength(scrollPositionInDP)) {

                stopRecording();

                return StopReason.COMPOSITION_END_REACHED;

            }

            // check overlap constraints
            int cp = scrollPositionInDP;
            Function<Sound, Integer> sp = Sound::getStartPosition;
            Function<Sound, Integer> d = Sound::getDuration;
            Predicate<? super Sound> predicate = s -> overlapConstraintsViolation(cp, sp.apply(s), d.apply(s));
            boolean overlapConstraintsViolation = track.getSounds().parallelStream()
                    .anyMatch(predicate);
            if (overlapConstraintsViolation) {

                stopRecording();

                return StopReason.SOUND_RECORD_OVERLAP;

            }

        }

        return StopReason.NO_STOP;

    }


    public void seek(int positionInMillis) {

        if (!composition.isPlaying()) {

            composition.getTracks().forEach(t -> t.seek(positionInMillis));

            playTaskScheduler.seek(positionInMillis);

        }

    }


    private boolean isPlaying() {

        return playTaskScheduler.isPlaying();

    }


    public boolean isRecording() {

        return recordTaskScheduler.isRecording();

    }


    public void playOrPause(boolean pressPlay, Runnable fallback1, Runnable fallback2) {

        playTaskScheduler.playOrPause(pressPlay, composition.getTracks(), fallback1, fallback2);

    }


    public int getMaxAmplitude(int activeTrackIndex) {

        return composition.getMaxAmplitude(activeTrackIndex);

    }


    public void setCallBack(Consumer<String> callback) {

        this.callback = callback;

    }


    public String getCompositionTitle() {

        return composition.getTitle();

    }


    public Long getCompositionId() {

        return composition.getCompositionId();

    }

}
