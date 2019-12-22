package com.wfm.soundcollaborations.Editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CompositionView extends LinearLayout
{
    private static final String TAG = CompositionView.class.getSimpleName();

    @BindView(R.id.hsv_holder)
    CompositionScrollView holderScrollView;
    @BindView(R.id.tvs)
    LinearLayout tracksViewsHolder;
    @BindView(R.id.twvs)
    LinearLayout trackWatchesViewsHolder;
    @BindView(R.id.ll_space)
    LinearLayout spaceLayout;

    List<TrackViewContainer> trackViewContainers = new ArrayList<>();

    private Paint playerLinePaint;
    private Path playerLineTriangle;
    private Paint playerLineTrianglePaint;

    private int activeTrackIndex;
    private int scrollPosition = 0;

    private OnScrollChanged mOnScrollChanged;

    public void updateSoundView(int amplitude) {

        trackViewContainers.stream()
                .filter(c -> c.getIndex() == activeTrackIndex)
                .forEach(c -> c.updateSoundView(amplitude));

        increaseScrollPosition(3);

    }

    public void updateTrackWatches(int soundWidths) {

        trackViewContainers.forEach(c -> c.updateTrackWatch(soundWidths));


    }

    public String finishRecording() throws Throwable {

        activate();

        return getActiveTrackViewContainer()
                .orElseThrow(() -> new Throwable("Could not finish recording"))
                .finishRecording();

    }


    private Optional<TrackViewContainer> getActiveTrackViewContainer() {

        Predicate<? super TrackViewContainer> activePredicate = c -> c.getIndex() == activeTrackIndex;

        return trackViewContainers.stream()
                .filter(activePredicate)
                .findAny();

    }


    public interface OnScrollChanged
    {
        void onNewScrollPosition(int position);
    }

    //TODO i think this is not used
    public CompositionView(Context context)
    {
        super(context);
        View.inflate(getContext(), R.layout.tracks_and_trackwatch_viewgroup, this);
        ButterKnife.bind(this);
        initVariables();
    }


    public CompositionView(Context context, AttributeSet attrs) {

        super(context, attrs);
        View.inflate(getContext(), R.layout.tracks_and_trackwatch_viewgroup, this);
        ButterKnife.bind(this);
        initVariables();

        // default first Track is activated
        activeTrackIndex = 0;

        for(int i = 0; i< 4; i++) {

            final boolean activate = (i == activeTrackIndex);

            TrackViewContainer trackViewContainer = new TrackViewContainer.Builder(activate)
                    .index(i)
                    .build(this);

            tracksViewsHolder.addView(trackViewContainer.getTrackView());
            trackWatchesViewsHolder.addView(trackViewContainer.getTrackWatchView());

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
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout()
            {
                spaceLayout.setPadding(getWidth()/2, 0, getWidth()/2, 0);
            }
        });

        holderScrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            scrollPosition = holderScrollView.getScrollX();
            mOnScrollChanged.onNewScrollPosition(holderScrollView.getScrollX());
        });

        holderScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }


    public void addSoundView(Context context, Sound sound) {

        trackViewContainers.stream()
                .filter(c -> c.getIndex() == sound.trackIndex)
                .forEach(c -> c.addSoundView(context, sound));

    }


    public int addSoundView(Context context) {

        deactivate();

        trackViewContainers.stream()
                .filter(c -> c.getIndex() == activeTrackIndex)
                .forEach(c -> c.addSoundView(context, scrollPosition));

        return scrollPosition;

    }


    public void refreshActiveTrackIndex(int trackIndex) {

        trackViewContainers.forEach(TrackViewContainer::deactivate);

        trackViewContainers.stream()
                .filter(c -> c.getIndex() == trackIndex)
                .forEach(TrackViewContainer::activate);

        activeTrackIndex = trackIndex;

    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
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

    public void increaseScrollPosition(int value)
    {
        this.scrollPosition += value;
        holderScrollView.scrollTo(this.scrollPosition, 0);
    }

    public void setScrollPosition(int value)
    {
        this.scrollPosition = value;
        holderScrollView.scrollTo(value, 0);
    }


    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        holderScrollView.setEnabled(enabled);
    }

    public void setOnScrollChanged(OnScrollChanged scrollChanged)
    {
        mOnScrollChanged = scrollChanged;
    }


    private void activate() {
        setEnabled(true);
    }


    private void deactivate() {

        setEnabled(false);

    }


    public List<String> deleteSoundViews() {

        return trackViewContainers.stream()
                .map(TrackViewContainer::deleteSoundViews)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


    }

}
