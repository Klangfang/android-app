package com.wfm.soundcollaborations.Editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
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
import java.util.Map;
import java.util.stream.Collectors;

public class TrackViewContainer {

    private static final String TAG = TrackViewContainer.class.getSimpleName();

    private TrackView trackView;

    private TrackWatchView trackWatchView;

    private List<SoundView> soundViews = new ArrayList<>();

    private int index;


    public void activate() {

        trackView.activate();
        trackWatchView.activate();

    }


    public int getTrackWatchViewWidth() {

        return trackWatchView.getWidth();

    }

    public void updateTrackWatch(Map<String, Integer> soundWidths) {

        //TODO check this warning
        soundViews.stream()
                .filter(s -> soundWidths.keySet().contains(s.getUuid()))
                .forEach(s -> decreaseTrackWatchPercentage((soundWidths.get(s.getUuid()) / 3) * 0.17f));

    }


    public static class Builder {

        private final boolean activate;
        private int index;


        public Builder index(int index) {

            this.index = index;
            return this;

        }


        public Builder(boolean activate) {

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


    public void increaseViewWatchPercentage(Context context, float percentage)
            throws SoundRecordingTimeException {

        if (isTrackWatchPercentageFull()) {
            //TODO is context required here?!
            throw new SoundRecordingTimeException(context);
        }

        trackWatchView.increasePercentage(percentage);

    }


    public void decreaseTrackWatchPercentage(float percentage) {

        trackWatchView.decreasePercentage(percentage);

    }


    private boolean isTrackWatchPercentageFull() {
        float actualPercentage = trackWatchView.getPercentage();
        return actualPercentage >= 100;
    }


    // TODO make it better with one for each
    protected List<String> deleteSoundViews() {

        soundViews.removeIf(s -> s.hasDefaultState());

        return soundViews.stream()
                .map(SoundView::getUuid)
                .collect(Collectors.toList());

    }


    protected void addSoundView(Context context, Sound sound) {

        trackView.addSoundView(new SoundView.Builder(context)
                .trackIndex(index)
                .startPosition(sound.startPosition)
                .duration(sound.duration)
                .url(sound.filePath)
                .build(SoundViewType.DOWNLOAD));

    }


    protected void addSoundView(Context context, Integer scrollPosition) {

        SoundView soundView = new SoundView.Builder(context)
                .trackIndex(index)
                .startPosition(scrollPosition)
                .build(SoundViewType.RECORD);

        trackView.addSoundView(soundView);
        soundViews.add(soundView);

    }


    protected void updateSoundView(Context context, int amplitude) {

        try {

            //TODO maybe last element
            SoundView soundView = soundViews.stream()
                    .filter(s -> s.getSoundViewType().equals(SoundViewType.RECORD))
                    .findAny()
                    .get();
            soundView.addWave(amplitude);
            soundView.invalidate();
            Log.d(TAG, "Max Amplitude Recieved -> " + amplitude);

            ViewGroup.LayoutParams layoutParams = soundView.getLayoutParams();
            layoutParams.width = layoutParams.width + 3;
            soundView.setLayoutParams(layoutParams);


            increaseViewWatchPercentage(context, 0.17f);
        } catch (SoundRecordingTimeException e) {
            //TODO LOG or popup
            e.printStackTrace();
        }

    }


    protected void drawLine(Canvas canvas) {

        trackView.drawLine(canvas);

    }


    protected void deactivate() {

        trackView.deactivate();
        trackWatchView.deactivate();

    }


    protected void deleteSoundView(SoundView soundView) {

        trackView.removeView(soundView);

    }


    protected TrackView getTrackView() {
        return trackView;
    }


    protected TrackWatchView getTrackWatchView() {
        return trackWatchView;
    }


    protected List<SoundView> getSoundViews() {
        return soundViews;
    }


    protected int getIndex() {
        return index;
    }

}
