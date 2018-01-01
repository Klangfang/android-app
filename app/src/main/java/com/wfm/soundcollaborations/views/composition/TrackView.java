package com.wfm.soundcollaborations.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wfm.soundcollaborations.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mohammed on 10/21/17.
 */

public class TrackView extends LinearLayout
{
    private static final String TAG = TrackView.class.getSimpleName();
    private Paint linePaint;
    @BindView(R.id.ll_sounds_holder)
    RelativeLayout soundsHolderLayout;

    public TrackView(Context context)
    {
        super(context);
        View.inflate(context, R.layout.view_track, this);
        ButterKnife.bind(this);
        init();
    }

    public TrackView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        View.inflate(context, R.layout.view_track, this);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        setOrientation(LinearLayout.HORIZONTAL);
        setWillNotDraw(false);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.rgb(0x5a, 0x5a, 0x5a));
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        drawLine(canvas);
    }

    public void drawLine(Canvas canvas)
    {
        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, linePaint);
    }

    public void activate()
    {
        linePaint.setColor(Color.WHITE);
        invalidate();
    }

    public void deactivate()
    {
        linePaint.setColor(Color.rgb(0x5a, 0x5a, 0x5a));
        invalidate();
    }

    public void addSoundView(SoundView soundView)
    {
        soundsHolderLayout.addView(soundView);
    }
}
