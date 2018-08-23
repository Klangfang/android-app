package com.wfm.soundcollaborations.Editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by mohammed on 10/27/17.
 */

public class SoundView extends View
{
    private static final String TAG = SoundView.class.getSimpleName();

    private Paint linePaint;
    private ArrayList<Integer> waves;
    private int track = -1;

    private Path clipPath;
    private RectF rectangle;
    private Paint rectPaint;
    int radius = 50;
    private Paint viewPaint;


    public SoundView(Context context)
    {
        super(context);
        init();
    }

    public SoundView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        // clipping
        rectangle = new RectF();
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setStrokeCap(Paint.Cap.ROUND);
        rectPaint.setColor(Color.rgb(0x1f, 0x5a, 0x37));
        setBackground(new ColorDrawable(Color.TRANSPARENT));
        clipPath = new Path();
        clipPath.addRoundRect(rectangle, radius, radius, Path.Direction.CW);
        // waves
        waves = new ArrayList<>();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);


        viewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        viewPaint.setStyle(Paint.Style.FILL);
        viewPaint.setColor(Color.rgb(0x1f, 0x5a, 0x37));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        rectangle.set(0, 0, getLayoutParams().width, getLayoutParams().height);
        canvas.drawRoundRect(rectangle, radius, radius, rectPaint);
        drawWaves(canvas);
        canvas.clipPath(clipPath);

    }

    public void addWave(int frame)
    {
        this.waves.add(frame);
        invalidate();
    }

    private void drawWaves(Canvas canvas)
    {
        // draw lines
        float width = 3;
        linePaint.setStrokeWidth(width);
        int currentLineX = 0;
        for(int i=0; i<waves.size(); i++)
        {
            linePaint.setColor(Color.argb(getWaveAlpha(waves.get(i)), 0, 0, 0));
            canvas.drawLine(currentLineX, 0, currentLineX, getHeight(), linePaint);
            currentLineX += width;
        }
    }

    private int getWaveAlpha(int frame)
    {
        return (frame * 255 / 32768) * 4 > 255 ? 255 : (frame * 255 / 32768) * 4;
    }

    public void reset()
    {
        this.waves.clear();
        waves = null;
        getLayoutParams().width = 0;
        init();
        invalidate();
    }

    public void setTrack(int track)
    {
        this.track = track;
    }

    public void increaseWidth(int width)
    {
        getLayoutParams().width = getLayoutParams().width + width;
        invalidate();
    }

    public int getTrack()
    {
        return this.track;
    }

    public void setYellowBackground()
    {
        rectPaint.setColor(Color.rgb(0xF9, 0xD7, 0x37));
        invalidate();
    }

    public void setBlackBackground() {
        rectPaint.setColor(Color.rgb(15, 15, 15));
        invalidate();
    }
}
