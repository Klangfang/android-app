package com.wfm.soundcollaborations.interaction.editor.views.composition;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.RemoteSound;
import com.wfm.soundcollaborations.interaction.editor.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.interaction.editor.views.composition.listeners.TrackWatchViewOnClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.wfm.soundcollaborations.interaction.editor.utils.DPUtils.TRACK_MAX_HEIGHT;
import static com.wfm.soundcollaborations.interaction.editor.utils.DPUtils.TRACK_MAX_LENGTH_IN_MS;
import static com.wfm.soundcollaborations.interaction.editor.views.composition.CompositionView.SCROLL_STEP;

class TrackViewContainer {

    private static final String TAG = TrackViewContainer.class.getSimpleName();

    private static final float WATCH_VIEW_PERCENTAGE = 0.17f;

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
            LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(TRACK_MAX_LENGTH_IN_MS, TRACK_MAX_HEIGHT);
            trackParams.setMargins(0, 10, 0, 10);
            trackView.setLayoutParams(trackParams);

            TrackWatchView trackWatchView = new TrackWatchView(context);
            trackWatchView.setOnClickListener(new TrackWatchViewOnClickListener(compositionView, index));
            LinearLayout.LayoutParams watchParams = new LinearLayout.LayoutParams(TRACK_MAX_HEIGHT, TRACK_MAX_HEIGHT);
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


    void addRemoteSoundView(Context context, RemoteSound sound) {

        SoundView soundView = new SoundView.Builder(context)
                .status(SoundViewStatus.REMOTE)
                .trackIndex(index)
                .startPosition(sound.startPosition)
                .duration(sound.duration)
                .url(sound.filePath)
                .build(this);

        trackView.addSoundView(soundView);
        soundViews.add(soundView);

    }


    void addLocalSoundView(Context context, Integer scrollPosition) {

        SoundView soundView = new SoundView.Builder(context)
                .status(SoundViewStatus.LOCAL_RECORDING)
                .trackIndex(index)
                .startPosition(scrollPosition)
                .build(this);

        trackView.addSoundView(soundView);
        soundViews.add(soundView);

    }


    void updateSoundView(int amplitude) throws Throwable {

        SoundView soundView = getLocalRecordingSoundView();

        soundView.addWave(amplitude);
        soundView.invalidate();
        Log.d(TAG, "Max Amplitude Recieved -> " + amplitude);

        ViewGroup.LayoutParams layoutParams = soundView.getLayoutParams();
        layoutParams.width += SCROLL_STEP;
        soundView.setLayoutParams(layoutParams);

        increaseWatchViewPercentage();

    }


    List<String> deleteSoundViews() {

        Predicate<? super SoundView> deletePredicate = SoundView::hasDeleteState;

        List<String> uuids = soundViews.stream()
                .filter(deletePredicate)
                .map(this::deleteFromTrack)
                .collect(Collectors.toList());

        soundViews.removeIf(deletePredicate);

        return uuids;

    }


    private String deleteFromTrack(SoundView soundView) {

        trackView.deleteSoundViews(soundView);

        return soundView.getUuid();

    }


    int getTrackWatchViewWidth() {

        return trackWatchView.getWidth();

    }


    void updateTrackWatches(int soundWidths) {

        decreaseTrackWatchPercentage(soundWidths);

    }


    /**
     * Completes recording sound view
     *
     * @return uuid of the sound view
     */
    String completeSoundView() throws Throwable {

        return getLocalRecordingSoundView().completeLocalSoundView();

    }


    private SoundView getLocalRecordingSoundView() throws Throwable {

        Predicate<? super SoundView> recordPredicate = SoundView::hasLocalRecordingState;
        return soundViews.stream()
                .filter(recordPredicate)
                .findAny()
                .orElseThrow(() -> new Throwable("Could not complete local recording sound view."));

    }


    private void increaseWatchViewPercentage() {

        trackWatchView.increasePercentage(WATCH_VIEW_PERCENTAGE);

    }


    private void decreaseTrackWatchPercentage(int widths) {

        float percentage = (float) (widths / SCROLL_STEP) * WATCH_VIEW_PERCENTAGE;
        trackWatchView.decreasePercentage(percentage);

    }


    void activate(boolean activate) {

        if (activate) {

            trackView.activate();
            trackWatchView.activate();

        } else {

            trackView.deactivate();
            trackWatchView.deactivate();

        }

    }


    void enable(boolean enable) {

        trackView.setEnabled(enable);
        trackWatchView.setEnabled(enable);

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
                .anyMatch(SoundView::isSelectedForDelete);

    }

}
