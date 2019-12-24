package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.utils.AudioRecorderStatus;
import com.wfm.soundcollaborations.Editor.utils.DPUtils;
import com.wfm.soundcollaborations.Editor.views.composition.CompositionView;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;
import com.wfm.soundcollaborations.webservice.dtos.SoundResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.SOUND_SECOND_WIDTH;
import static com.wfm.soundcollaborations.Editor.utils.DPUtils.TRACK_WIDTH_IN_MS;
import static com.wfm.soundcollaborations.Editor.views.composition.CompositionView.SCROLL_STEP;

public class Composition {

    private static final String TAG = Composition.class.getSimpleName();

    private final CompositionView compositionView;
    private final Long compositionId;
    private final String title;
    private final Context context;

    private int startPositionInDP;
    private int soundLengthInDP;

    private boolean recording;

    private List<Track> tracks;

    private TracksTimer mTracksTimer;


    private CompositionServiceClient csClient;


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

        createTracksController();
        subscribeToScrollEvent();

    }


    // for playing and pausing sounds
    private void createTracksController() {

        mTracksTimer = new TracksTimer(context, tracks, compositionView);

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

        mTracksTimer.seek(positionInMillis);

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

        if (isNotRecording()) {

            startPositionInDP = compositionView.addSoundView(context);

            soundLengthInDP = 0;

            startTrackRecorder();

            recording = true;

        } else {

            finishRecording();

        }

        return isRecording();

    }


    private void finishRecording() throws Throwable {

        recording = false;

        int trackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(trackIndex);
        tracks.remove(trackIndex); // TODO without remove
        track.stopTrackRecorder();
        Integer duration = track.getDuration();
        String filePath = track.getFilePath();

        if (filePath != null) {

            String uuid = compositionView.finishRecording();
            Sound sound = new Sound.Builder()
                    .uuid(uuid)
                    .trackNumber(trackIndex)
                    .startPosition(DPUtils.getPositionInMs(startPositionInDP))
                    .duration(duration)
                    .filePath(filePath)
                    .build();
            track.prepareSound(sound, compositionView.getContext());
            mTracksTimer.updateTrack(trackIndex, track); //TODO noch brauchbar??!

        }

        tracks.add(trackIndex, track);

    }


    private StopReason checkStop() {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(activeTrackIndex);
        if (track.getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {

            return StopReason.MAXIMUM_RECORDING_TIME_REACHED;

        }

        // Check Sound out of composition
        int cursorPositionInDP = this.compositionView.getScrollPosition();
        if((cursorPositionInDP + SOUND_SECOND_WIDTH) > TRACK_WIDTH_IN_MS) {

            return StopReason.COMPOSITION_END_REACHED;

        }

        // check sound overlapping
        long startPositionInDP, lengthInDP;
        int trackNumber = compositionView.getActiveTrackIndex();
        for (Sound sound : tracks.get(trackNumber).getSounds()) {
            startPositionInDP = DPUtils.getValueInDP(sound.getStartPosition());
            lengthInDP = DPUtils.getValueInDP(sound.getDuration());
            long endPositionInDP = startPositionInDP + lengthInDP;
            int distanceToStartPos = cursorPositionInDP + 20 ;
            int distanceToEndPos = cursorPositionInDP + 20 ;
            if (distanceToStartPos > startPositionInDP && distanceToEndPos < endPositionInDP) {

                return StopReason.SOUND_RECORD_OVERLAP;

            }
        }

        return StopReason.NO_STOP;

    }


    private void startTrackRecorder() throws RecordTimeOutExceededException {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(activeTrackIndex);
        track.startTrackRecorder();
        tracks.add(activeTrackIndex, track);

    }


    public void deleteSounds() {

        tracks.forEach(track -> {
            int soundWidths = track.deleteSounds(compositionView.deleteSoundViews());
            compositionView.updateTrackWatches(soundWidths);
        });

    }

    public void enable() {

        compositionView.enable(true);

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

                Sound sound = new Sound.Builder()
                        .trackNumber(sndResp.trackNumber)
                        .startPosition(sndResp.startPosition)
                        .duration(sndResp.duration)
                        .filePath(sndResp.url)
                        .build();

                compositionView.addSoundView(compositionView.getContext(), sound);

                track.addSound(sound, compositionView.getContext());
                track.prepare(compositionView.getContext());
                tracks.add(trackNumber, track);

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


    private Stream<Sound> getRecordedSounds() {

        return tracks.stream()
                .map(Track::getRecordedSounds)
                .flatMap(Collection::stream);

    }


    public void playOrPause(boolean pressPlay) {

        mTracksTimer.playOrPause(pressPlay);

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
