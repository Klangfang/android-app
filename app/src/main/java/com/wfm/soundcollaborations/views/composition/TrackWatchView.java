package com.wfm.soundcollaborations.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by mohammed on 10/21/17.
 */

public class TrackWatchView extends View
{
    private Paint circlePaint;
    private Paint innerCirclePaint;
    private Paint borderStrokePaint;
    private int padding = 5;
    private float percentage = 0;


    public TrackWatchView(Context context)
    {
        super(context);
        init();
    }

    public TrackWatchView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(Color.BLACK);
        borderStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderStrokePaint.setColor(Color.BLACK);
        borderStrokePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawFullCircle(canvas);
        drawPercentage(canvas, percentage);
        drawFullCircleBorder(canvas);
        drawInnerCircle(canvas);
    }

    private void drawFullCircle(Canvas canvas)
    {
        circlePaint.setColor(Color.rgb(0x5a, 0x5a, 0x5a));
        canvas.drawCircle(getWidth()/2, getWidth()/2, getWidth()/2-padding, circlePaint);
    }

    private void drawPercentage(Canvas canvas, float percentage)
    {
        circlePaint.setColor(Color.rgb(0xF9, 0xD7, 0x37));
        RectF oval1 = new RectF(padding, padding, getWidth()-padding, getWidth()-padding);
        canvas.drawArc(oval1, -90, (360  * (percentage/100)), true, circlePaint);
    }

    private void drawFullCircleBorder(Canvas canvas)
    {
        int borderStrokeWidth = 5;
        borderStrokePaint.setStrokeWidth(borderStrokeWidth);
        canvas.drawCircle(getWidth()/2, getWidth()/2, getWidth()/2-padding, borderStrokePaint);
    }

    private void drawInnerCircle(Canvas canvas)
    {
        canvas.drawCircle(getWidth()/2, getWidth()/2, getWidth()/6-padding, innerCirclePaint);
    }

    public void increasePercentage(float percentage)
    {
        this.percentage = this.percentage + percentage;
        if(percentage >= 100)
            this.percentage = 100;
        invalidate();
    }

    public void activate()
    {
        innerCirclePaint.setColor(Color.WHITE);
        borderStrokePaint.setColor(Color.WHITE);
        invalidate();
    }

    public void deactivate()
    {
        innerCirclePaint.setColor(Color.BLACK);
        borderStrokePaint.setColor(Color.BLACK);
        invalidate();
    }
}
