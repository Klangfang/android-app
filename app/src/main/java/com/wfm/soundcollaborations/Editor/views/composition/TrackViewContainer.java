package com.wfm.soundcollaborations.Editor.views.composition;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.Editor.exceptions.SoundRecordingTimeException;
import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.Editor.utils.DPUtils;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.Editor.views.composition.listeners.TrackWatchViewOnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class TrackViewContainer {

    private static final String TAG = TrackViewContainer.class.getSimpleName();

    private static final float watchViewPercentage = 0.17f;

    private TrackView trackView;

    private TrackWatchView trackWatchView;

    private List<SoundView> soundViews = new ArrayList<>();

    private int index;


    public static class Builder {

        private final boolean activate;
        private int index;


        Builder index(int index) {

            this.index = index;
            return this;

        }


        Builder(boolean activate) {

            this.activate = activate;

        }


        public TrackViewContainer build(CompositionView compositionView) {

            Context context = compositionView.getContext();

            TrackView trackView = new TrackView(context);
            trackView.setOnClickListener(new TrackViewOnClickListener(compositionView, index));
            LinearLayout.LayoutParams trackParams  = new LinearLayout.LayoutParams(DPUtils.TRACK_WIDTH_IN_MS, DPUtils.TRACK_HEIGHT);
            trackParams.setMargins(0, 10, 0, 10);
            trackView.setLayoutParams(trackParams);

            TrackWatchView trackWatchView = new TrackWatchView(context);
            trackWatchView.setOnClickListener(new TrackWatchViewOnClickListener(compositionView, index));
            LinearLayout.LayoutParams watchParams  = new LinearLayout.LayoutParams(DPUtils.TRACK_HEIGHT, DPUtils.TRACK_HEIGHT);
            watchParams.setMargins(10, 10, 0, 10);
            trackWatchView.setLayoutParams(watchParams);

            if (activate) {
                trackView.activate();
                trackWatchView.activate();
            }

            return new TrackViewContainer(trackView, trackWatchView, index);

        }

    }


    private TrackViewContainer(TrackView trackView, TrackWatchView trackWatchView, int index) {

        this.trackView = trackView;
        this.trackWatchView = trackWatchView;
        this.index = index;

    }


    void addSoundView(Context context, Sound sound) {

        SoundView soundView = new SoundView.Builder(context)
                .status(SoundViewStatus.DOWNLOAD)
                .trackIndex(index)
                .startPosition(sound.startPosition)
                .duration(sound.duration)
                .url(sound.filePath)
                .build(this);

        trackView.addSoundView(soundView);

    }


    void addSoundView(Context context, Integer scrollPosition) {

        SoundView soundView = new SoundView.Builder(context)
                .status(SoundViewStatus.RECORD)
                .trackIndex(index)
                .startPosition(scrollPosition)
                .build(this);

        trackView.addSoundView(soundView);
        soundViews.add(soundView);

    }


    void updateSoundView(int amplitude) {

        try {

            SoundView soundView = soundViews.stream()
                    .filter(s -> s.getSoundViewStatus().equals(SoundViewStatus.RECORD))
                    .findAny()
                    .get();

            soundView.addWave(amplitude);
            soundView.invalidate();
            Log.d(TAG, "Max Amplitude Recieved -> " + amplitude);

            ViewGroup.LayoutParams layoutParams = soundView.getLayoutParams();
            layoutParams.width = layoutParams.width + 3;
            soundView.setLayoutParams(layoutParams);

            increaseWatchViewPercentage(watchViewPercentage);

        } catch (SoundRecordingTimeException e) {
            //TODO LOG or popup
            e.printStackTrace();
        }

    }


    List<String> deleteSoundViews() {

        Predicate<? super SoundView> deletePredicate = SoundView::hasDeleteState;

        List<String> uuids = soundViews.stream()
                .filter(deletePredicate)
                .map(this::deleteFromTrackAndMap)
                .collect(Collectors.toList());

        soundViews.removeIf(deletePredicate);

        return uuids;

    }


    private String deleteFromTrackAndMap(SoundView soundView) {

        trackView.deleteSoundViews(soundView);

        return soundView.getUuid();

    }


    int getTrackWatchViewWidth() {

        return trackWatchView.getWidth();

    }


    void updateTrackWatch(int soundWidths) {

        //TODO wait until sdk 6 Integer.divideUnsigned(soundWidths / 3)
        decreaseTrackWatchPercentage((float) (soundWidths / 3) * 0.17f);

    }


    String finishRecording() throws Throwable {

        SoundView recordedSound = getRecordedSound()
                .orElseThrow(() -> new RuntimeException("Could not finish recording."));
        return recordedSound.finishRecording();

    }


    private Optional<SoundView> getRecordedSound() {

        Predicate<? super SoundView> recordPredicate = SoundView::hasRecordState;
        return soundViews.stream()
                .filter(recordPredicate)
                .findAny();
    }


    private void increaseWatchViewPercentage(float percentage)
            throws SoundRecordingTimeException {

        if (isTrackWatchPercentageFull()) {

            throw new SoundRecordingTimeException();

        }

        trackWatchView.increasePercentage(percentage);

    }


    private void decreaseTrackWatchPercentage(float percentage) {

        trackWatchView.decreasePercentage(percentage);

    }


    private boolean isTrackWatchPercentageFull() {

        float actualPercentage = trackWatchView.getPercentage();
        return actualPercentage >= 100;

    }


    void activate() {

        trackView.activate();
        trackWatchView.activate();

    }


    void deactivate() {

        trackView.deactivate();
        trackWatchView.deactivate();

    }


    TrackView getTrackView() {
        return trackView;
    }


    TrackWatchView getTrackWatchView() {
        return trackWatchView;
    }


    int getIndex() {
        return index;
    }


    boolean hasDeleteSoundViews() {

        return soundViews.stream()
                .anyMatch(s -> s.getSoundViewStatus().equals(SoundViewStatus.SELECT_FOR_DELETE));

    }

}
