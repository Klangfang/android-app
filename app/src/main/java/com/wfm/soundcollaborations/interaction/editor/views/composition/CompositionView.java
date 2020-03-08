package com.wfm.soundcollaborations.interaction.editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.databinding.TracksAndTrackwatchViewgroupBinding;
import com.wfm.soundcollaborations.interaction.editor.model.composition.sound.RemoteSound;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class CompositionView extends LinearLayout {

    private static final String TAG = CompositionView.class.getSimpleName();

    public static final int SCROLL_STEP = 3;

    private TracksAndTrackwatchViewgroupBinding binding;

    List<TrackViewContainer> trackViewContainers = new ArrayList<>();

    private Paint playerLinePaint;
    private Path playerLineTriangle;
    private Paint playerLineTrianglePaint;

    private int activeTrackIndex;
    private int scrollPosition = 0;

    private OnScrollChanged mOnScrollChanged;


    public interface OnScrollChanged
    {
        void onNewScrollPosition(int position);
    }

    //TODO i think this is not used
    public CompositionView(Context context) {
        super(context);

        binding = TracksAndTrackwatchViewgroupBinding.inflate(LayoutInflater.from(context), this, true);

        initVariables();

    }


    public CompositionView(Context context, AttributeSet attrs) {

        super(context, attrs);

        binding = TracksAndTrackwatchViewgroupBinding.inflate(LayoutInflater.from(context), this, true);

        initVariables();

        // default first Track is activated
        activeTrackIndex = 0;

        for(int i = 0; i< 4; i++) {

            final boolean activate = (i == activeTrackIndex);

            TrackViewContainer trackViewContainer = new TrackViewContainer.Builder(activate)
                    .index(i)
                    .build(this);

            binding.tvs.addView(trackViewContainer.getTrackView());
            binding.twvs.addView(trackViewContainer.getTrackWatchView());

            trackViewContainers.add(trackViewContainer);

        }

    }


    private void initVariables()
    {
        setWillNotDraw(false);
        playerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        playerLinePaint.setStyle(Paint.Style.STROKE);
        playerLinePaint.setColor(Color.rgb(0x56, 0x56, 0x56));
        playerLinePaint.setStrokeWidth(1);

        playerLineTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        playerLineTrianglePaint.setStyle(Paint.Style.FILL);
        playerLineTrianglePaint.setColor(Color.rgb(0x56, 0x56, 0x56));

        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() ->
                binding.llSpace.setPadding(getWidth() / 2, 0, getWidth() / 2, 0));

        binding.hsvHolder.getViewTreeObserver().addOnScrollChangedListener(() -> {
            scrollPosition = binding.hsvHolder.getScrollX();
            mOnScrollChanged.onNewScrollPosition(binding.hsvHolder.getScrollX());
        });

        binding.hsvHolder.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }


    private TrackViewContainer getActiveTrackViewContainer() throws Throwable {

        return getTrackViewContainer(activeTrackIndex);

    }


    private TrackViewContainer getTrackViewContainer(Integer trackIndex) throws Throwable {

        Predicate<? super TrackViewContainer> activePredicate = c -> c.getIndex() == trackIndex;

        return trackViewContainers.stream()
                .filter(activePredicate)
                .findAny()
                .orElseThrow(() -> new Throwable("Could not get track view container"));

    }

    public void addRemoteSoundView(Context context, RemoteSound sound) throws Throwable {

        getTrackViewContainer(sound.trackIndex).addRemoteSoundView(context, sound);

    }


    public void addLocalSoundView(Context context) throws Throwable {

        enable(false);

        getActiveTrackViewContainer().addLocalSoundView(context, scrollPosition);

    }


    public void updateSoundView(int amplitude) throws Throwable {

        getActiveTrackViewContainer().updateSoundView(amplitude);

        increaseScrollPosition();

    }


    public void updateTrackWatches(int trackIndex, int soundWidths) throws Throwable {

        getTrackViewContainer(trackIndex).updateTrackWatches(soundWidths);

    }


    /**
     * Completes local sound view and enables the composition view
     *
     * @return uuid of the sound view
     */
    public String completeLocalSoundView() throws Throwable {

        enable(true);

        return getActiveTrackViewContainer().completeSoundView();

    }


    public void enable(boolean enable) {

        setEnabled(enable);

        trackViewContainers.forEach(c -> c.enable(enable));

    }


    public void refreshActiveTrackIndex(int trackIndex) {

        trackViewContainers.forEach(c -> c.activate(false));

        trackViewContainers.stream()
                .filter(c -> c.getIndex() == trackIndex)
                .forEach(c -> c.activate(true));

        activeTrackIndex = trackIndex;

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);
        drawPlayerLine(canvas);

    }


    private void drawPlayerLine(Canvas canvas) {

        float x = getWidth() / 2;

        int trackWatchViewWidth = trackViewContainers.stream()
                .filter(c -> c.getIndex() == 0)
                .map(TrackViewContainer::getTrackWatchViewWidth)
                .findAny()
                .get();

        canvas.drawLine(x, 0, x, getHeight() - (trackWatchViewWidth / 2) - 20, playerLinePaint);

        playerLineTriangle = new Path();
        playerLineTriangle.moveTo(x+10, 0);
        playerLineTriangle.lineTo(x, 10);
        playerLineTriangle.lineTo(x-10, 0);
        playerLineTriangle.lineTo(x+10, 0);
        playerLineTriangle.close();

        canvas.drawPath(playerLineTriangle, playerLineTrianglePaint);

    }


    public int getActiveTrackIndex() {

        return activeTrackIndex;

    }


    public int getScrollPosition()
    {
        return this.scrollPosition;
    }

    public void increaseScrollPosition() {

        this.scrollPosition += SCROLL_STEP;
        binding.hsvHolder.scrollTo(this.scrollPosition, 0);

    }

    public void setScrollPosition(int value) {

        this.scrollPosition = value;
        binding.hsvHolder.scrollTo(value, 0);

    }


    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        binding.hsvHolder.setEnabled(enabled);

    }

    public void setOnScrollChanged(OnScrollChanged scrollChanged) {

        mOnScrollChanged = scrollChanged;

    }


    public List<String> deleteSoundViews() {

        return trackViewContainers.stream()
                .map(TrackViewContainer::deleteSoundViews)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

}
