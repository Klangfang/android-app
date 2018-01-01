package com.wfm.soundcollaborations.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.views.composition.listeners.TrackViewOnClickListener;
import com.wfm.soundcollaborations.views.composition.listeners.TrackWatchViewOnClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammed on 10/21/17.
 */

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

    ArrayList<TrackView> tracksViews = new ArrayList<>();
    ArrayList<TrackWatchView> tracksWatchViews = new ArrayList<>();

    private Paint playerLinePaint;
    private Path playerLineTriangle;
    private Paint playerLineTrianglePaint;

    private int activeTrack = -1;
    private int scrollPosition = 0;

    private OnScrollChanged mOnScrollChanged;

    public interface OnScrollChanged
    {
        void onNewScrollPosition(int position);
    }

    public CompositionView(Context context)
    {
        super(context);
        View.inflate(getContext(), R.layout.view_composition, this);
        ButterKnife.bind(this);
        initVariables();
    }

    public CompositionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        View.inflate(getContext(), R.layout.view_composition, this);
        ButterKnife.bind(this);
        initVariables();
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

        holderScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener()
        {
            @Override
            public void onScrollChanged()
            {
                scrollPosition = holderScrollView.getScrollX();
                mOnScrollChanged.onNewScrollPosition(holderScrollView.getScrollX());
            }
        });

        holderScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void addTrackView(TrackWatchView trackWatchView, TrackView trackView)
    {
        // add watch view
        trackWatchesViewsHolder.addView(trackWatchView);
        tracksWatchViews.add(trackWatchView);
        // add track view
        tracksViewsHolder.addView(trackView);
        tracksViews.add(trackView);
    }


    public void activateTrack(int index)
    {
        for(int i=0; i<tracksViews.size(); i++)
        {
            tracksViews.get(i).deactivate();
            tracksWatchViews.get(i).deactivate();
        }

        tracksViews.get(index).activate();
        tracksWatchViews.get(index).activate();
        this.activeTrack = index;
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        drawPlayerLine(canvas);
    }

    private void drawPlayerLine(Canvas canvas)
    {
        float x = getWidth() / 2;
        canvas.drawLine(x, 0, x, getHeight()- (tracksWatchViews.get(0).getWidth() / 2) - 20, playerLinePaint);

        playerLineTriangle = new Path();
        playerLineTriangle.moveTo(x+10, 0);
        playerLineTriangle.lineTo(x, 10);
        playerLineTriangle.lineTo(x-10, 0);
        playerLineTriangle.lineTo(x+10, 0);
        playerLineTriangle.close();

        canvas.drawPath(playerLineTriangle, playerLineTrianglePaint);
    }

    public int getActiveTrack()
    {
        return this.activeTrack;
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

    public void increaseViewWatchPercentage(int trackNumber, float percentage)
    {
        this.tracksWatchViews.get(trackNumber).increasePercentage(percentage);
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
}
