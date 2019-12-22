package com.wfm.soundcollaborations.Editor.model.composition;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.wfm.soundcollaborations.Editor.exceptions.RecordTimeOutExceededException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundRecordingTimeException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillBeOutOfCompositionException;
import com.wfm.soundcollaborations.Editor.exceptions.SoundWillOverlapException;
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

public class Composition {

    private static final String TAG = CompositionBuilder.class.getSimpleName();


    private final CompositionView compositionView;
    private final Long compositionId;
    private final String title;

    private List<Track> tracks;

    private TracksTimer mTracksTimer;


    private CompositionServiceClient csClient;


    private Composition(CompositionView compositionView,
                        Long compositionId,
                        String title,
                        List<Track> tracks) {

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

        mTracksTimer = new TracksTimer(tracks, compositionView);

    }


    private void subscribeToScrollEvent() {

        compositionView.setOnScrollChanged(position -> {

            if (mTracksTimer.isNotPlaying()) {
                int milliseconds = (int)(position * 16.6666);
                seek(milliseconds);
                Log.d(TAG, "Milliseconds => "+milliseconds+" position => "+ position);
            }

        });

    }


    private void seek(int positionInMillis)
    {
        mTracksTimer.seek(positionInMillis);
    }

    public void updateSoundView() {

        //width = width + 3;
        //soundLength = width;
        //layoutParams.width = width;
        //soundView.setLayoutParams(layoutParams);

        int maxAmplitude = tracks.get(compositionView.getActiveTrackIndex()).getMaxAmplitude();
        compositionView.updateSoundView(maxAmplitude);

    }


    //TODO why soundLegnthInWidth is not used?
    public void finishRecording(Integer soundLengthInWidth, Integer startPositionInWidth)
            throws Throwable {

        int trackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(trackIndex);
        tracks.remove(trackIndex); // TODO without remove
        track.stopTrackRecorder();
        Integer duration = track.getDuration();
        String filePath = track.getFilePath();

        if (filePath != null && soundLengthInWidth != null && startPositionInWidth != null) {

            String uuid = compositionView.finishRecording();
            Sound sound = new Sound.Builder()
                    .uuid(uuid)
                    .trackNumber(trackIndex)
                    .startPosition(DPUtils.getPositionInMs(startPositionInWidth))
                    .duration(duration)
                    .filePath(filePath)
                    .build();
            track.prepareSound(sound, compositionView.getContext());
            mTracksTimer.updateTrack(trackIndex, track); //TODO noch brauchbar??!

        }

        tracks.add(trackIndex, track);

    }


    public void checkLimits(Integer soundLengthInWidth, Integer startPositionInWidth)
            throws Throwable {

        int activeTrackIndex = compositionView.getActiveTrackIndex();
        Track track = tracks.get(activeTrackIndex);
        if (track.getTrackRecorderStatus().equals(AudioRecorderStatus.STOPPED)) {
            finishRecording(soundLengthInWidth, startPositionInWidth);
            throw new SoundRecordingTimeException();
        }

        // Check Sound out of composition
        int cursorPositionInDP = this.compositionView.getScrollPosition();
        if((cursorPositionInDP + SOUND_SECOND_WIDTH) > TRACK_WIDTH_IN_MS) {
            // Stop recorder
            finishRecording(soundLengthInWidth, startPositionInWidth);
            throw new SoundWillBeOutOfCompositionException();
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
                finishRecording(soundLengthInWidth, startPositionInWidth);
                throw new SoundWillOverlapException();
            }
        }

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


    public static class CompositionConfigurer {

        private final CompositionView compositionView;

        private Long compositionId;
        private String title;

        private List<Track> tracks = new ArrayList<>();

        public CompositionConfigurer(CompositionView compositionView) {

            this.compositionView = compositionView;

            for(int i=0; i<4; i++) {

                Track track = new Track();
                tracks.add(track);

            }

        }


        public CompositionConfigurer title(String title) {

            this.title = title;
            return this;

        }



        public Composition build(CompositionResponse compositionResponse) {

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

            return new Composition(compositionView, compositionId, title, tracks);

        }

    }


    private Context getViewContext() {

        return compositionView.getContext();

    }


    public int createSoundView(Context context) throws RecordTimeOutExceededException {

        int scrollPosition = compositionView.addSoundView(context);

        startTrackRecorder();

        return scrollPosition;

    }


    private Stream<Sound> getRecordedSounds() {

        return tracks.stream()
                .map(Track::getRecordedSounds)
                .flatMap(Collection::stream);

    }


    public void play() {

        mTracksTimer.playOrPause();

    }


    public boolean isNotPlaying() {

        return mTracksTimer.isNotPlaying();

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


}
