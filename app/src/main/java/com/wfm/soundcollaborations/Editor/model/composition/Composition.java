package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.wfm.soundcollaborations.Editor.model.composition.sound.LocalSound;
import com.wfm.soundcollaborations.Editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.Editor.model.composition.sound.Sound;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.SoundResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.getPositionInMs;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.overlapConstraintsViolation;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.soundHasReachedMaxLength;
import static com.wfm.soundcollaborations.Editor.views.composition.CompositionView.SCROLL_STEP;

public class Composition {

    private static final String TAG = Composition.class.getSimpleName();

    private static final int POSITION_ZERO = 0;

    private final Context context;

    private final CompositionView compositionView;
    private final Long compositionId;
    private final String title;

    private int startPositionInDP;
    private int soundLengthInDP;

    private boolean recording;

    private CompositionStatus status = CompositionStatus.READY;

    private List<Track> tracks;

    private CompositionServiceClient csClient;

    private RecordTaskScheduler recordTaskScheduler;
    private PlayTaskScheduler playTaskScheduler;


    private Composition(Context context,
                        CompositionView compositionView,
                        Long compositionId,
                        String title,
                        List<Track> tracks) {

        this.context = context;
        this.compositionView = compositionView;
        this.compositionId = compositionId;
        this.title = title;
        this.tracks = tracks;

        csClient = new CompositionServiceClient();

        playTaskScheduler = new PlayTaskScheduler(context);
        recordTaskScheduler = new RecordTaskScheduler(context);

        subscribeToScrollEvent();

    }


    private void subscribeToScrollEvent() {

        compositionView.setOnScrollChanged(position -> {

            if (!isPlaying()) {
                int milliseconds = (int)(position * 16.6666);
                seek(milliseconds);
                Log.d(TAG, String.format("Milliseconds =>  %d position => %d", milliseconds, position));
            }

        });

    }


    private void seek(int positionInMillis) {

        tracks.forEach(t -> t.seek(positionInMillis));

        playTaskScheduler.seek(positionInMillis);

    }


    public StopReason simulateRecording() throws Throwable {

        if (isRecording()) {

            StopReason stopReason = checkStop();

            if (stopReason.equals(StopReason.NO_STOP)) {

                increaseSoundLength();

                int maxAmplitude = tracks.get(compositionView.getActiveTrackIndex()).getMaxAmplitude();
                compositionView.updateSoundView(maxAmplitude);

                recording = true;
            }

            return stopReason;
        }

        return StopReason.NO_STOP;

    }

    private void increaseSoundLength() {

        this.soundLengthInDP += SCROLL_STEP;

    }


    public boolean startRecording(Context context) throws Throwable {

        if (status.equals(CompositionStatus.READY)) {
            if (isNotRecording()) {

                startPositionInDP = compositionView.addSoundView(context);

                soundLengthInDP = 0;

                startTrackRecorder(getPositionInMs(compositionView.getScrollPosition()));

                recording = true;

            } else {

                //TODO remember the last sound recording duration, in order to change the global composition state to exhausted
                finishRecording();

            }
        }

        return isRecording();

    }


    private void finishRecording() throws Throwable {

        recording = false;

        int trackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(trackIndex);
        track.stopTrackRecorder(getPositionInMs(compositionView.getScrollPosition()));
        Integer duration = track.getDuration();
        String filePath = track.getFilePath();

        if (filePath != null) {

            String uuid = compositionView.finishRecording();
            LocalSound sound = new LocalSound.Builder()
                    .uuid(uuid)
                    .trackNumber(trackIndex)
                    .startPosition(getPositionInMs(startPositionInDP))
                    .duration(duration)
                    .filePath(filePath)
                    .build();
            track.prepareSound(sound, compositionView.getContext());

        }

        tracks.set(trackIndex, track);

    }


    private StopReason checkStop() throws Throwable {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(activeTrackIndex);

        if (track.getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {

            finishRecording();

            status = CompositionStatus.EXHAUSTED;

            return StopReason.MAXIMUM_RECORDING_TIME_REACHED;

        }

        if (soundHasReachedMaxLength(compositionView.getScrollPosition())) {

            finishRecording();

            return StopReason.COMPOSITION_END_REACHED;

        }

        // check overlap constraints
        int cp = compositionView.getScrollPosition();
        Function<Sound, Integer> sp = Sound::getStartPosition;
        Function<Sound, Integer> d = Sound::getDuration;
        Predicate<? super Sound> predicate = s -> overlapConstraintsViolation(cp, sp.apply(s), d.apply(s));
        boolean overlapConstraintsViolation = track.getSounds().parallelStream()
                .anyMatch(predicate);
        if (overlapConstraintsViolation) {

            finishRecording();

            return StopReason.SOUND_RECORD_OVERLAP;
        }

        return StopReason.NO_STOP;

    }


    private void startTrackRecorder(int startTime) {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(activeTrackIndex);
        track.startTrackRecorder(startTime);
        tracks.set(activeTrackIndex, track);

    }


    public void deleteSounds() throws Throwable {

        List<String> soundUuids = compositionView.deleteSoundViews();

        for (Track track : tracks) {

            int soundWidths = track.increaseTime(soundUuids);
            compositionView.updateTrackWatches(track.getTrackNumber(), soundWidths);

            track.deleteSounds(soundUuids);


        }

    }


    public void enable(StopReason stopReason) {

        if (stopReason.equals(StopReason.COMPOSITION_END_REACHED)) {
            compositionView.setScrollPosition(POSITION_ZERO);
        }

        stopRecord();

        compositionView.enable(true);

    }


    public void increaseScrollPosition() {

        compositionView.increaseScrollPosition();

    }


    public void record() {

        recordTaskScheduler.record();

    }


    private void stopRecord() {

        recordTaskScheduler.stop();

    }

    public void preDestroy() {

        tracks.forEach(Track::preDestroy);

    }


    public static class CompositionConfigurer {

        private Context context;
        private CompositionView compositionView;

        private Long compositionId;
        private String title;

        private List<Track> tracks = new ArrayList<>();

        public CompositionConfigurer compositionView(CompositionView compositionView) {

            this.compositionView = compositionView;

            return this;

        }

        public CompositionConfigurer(Context context) {

            this.context = context;

            for (int i = 0; i < 4; i++) {

                Track track = new Track(i);
                tracks.add(track);

            }

        }


        public CompositionConfigurer title(String title) {

            this.title = title;
            return this;

        }


        public Composition build(CompositionResponse compositionResponse) throws Throwable {

            for (SoundResponse sndResp : compositionResponse.sounds) {

                Integer trackNumber = sndResp.trackNumber;
                Track track = tracks.get(trackNumber);

                RemoteSound sound = new RemoteSound.Builder()
                        .trackNumber(sndResp.trackNumber)
                        .startPosition(sndResp.startPosition)
                        .duration(sndResp.duration)
                        .filePath(sndResp.url)
                        .build();

                compositionView.addSoundView(compositionView.getContext(), sound);

                track.addSound(sound, compositionView.getContext());
                track.prepare(compositionView.getContext());
                tracks.set(trackNumber, track);

                compositionId = compositionResponse.id;
                title = compositionResponse.title;

            }

            return build();

        }


        public Composition build() {

            return new Composition(context, compositionView, compositionId, title, tracks);

        }

    }


    private Context getViewContext() {

        return compositionView.getContext();

    }


    private Stream<LocalSound> getRecordedSounds() {

        return tracks.stream()
                .map(Track::getLocalSounds)
                .flatMap(Function.identity());

    }


    public void playOrPause(boolean pressPlay) {

        compositionView.enable(!pressPlay);

        playTaskScheduler.playOrPause(pressPlay, tracks);

    }


    // --- CompositionService Call ----

    public void create() {

        csClient.create(title,
                "KLANGFANG",
                getRecordedSounds(),
                listener -> showInfoAndStartNewTask());


    }


    public void join() {

        csClient.join(compositionId,
                getRecordedSounds(),
                listener -> showInfoAndStartNewTask());

    }


    private void showInfoAndStartNewTask() {

        Intent intent = new Intent(getViewContext(), MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        getViewContext().startActivity(intent);
        Toast.makeText(getViewContext(), "CompositionRequest is released!", Toast.LENGTH_LONG).show();

    }


    public void cancel() {

        if (Objects.nonNull(compositionId)) {
            csClient.cancel(compositionId, listener -> showInfoAndStartNewTask());
        }

    }

    private boolean isNotRecording() {

        return !isRecording();

    }


    private boolean isRecording() {

        return recording;
    }


    private boolean isPlaying() {

        return tracks.stream()
                .anyMatch(Track::isPlaying);

    }

}
