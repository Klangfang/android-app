package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wfm.soundcollaborations.editor.model.composition.sound.Sound;

import java.util.ArrayList;


public class SoundView extends View {

    private static final String TAG = SoundView.class.getSimpleName();
    private static final int MAX_AMPLITUDE = 32767;
    private Paint linePaint;
    private ArrayList<Integer> amplitudes;
    private ArrayList<Integer> waves;

    private Sound mSound;

    public SoundView(Context context, Sound sound)
    {
        super(context);
        this.mSound = sound;
        init();
    }

    public SoundView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        amplitudes = new ArrayList<>();
        waves = new ArrayList<>();
        linePaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(this.amplitudes.size() != 0)
        {
            drawAmplitudes(canvas);
        }
        else
        {
            drawWaves(canvas);
        }

    }

    public void add(int amplitude)
    {
        this.amplitudes.add(amplitude);
        Log.d(TAG, "Amplitude Received => "+amplitude);
        invalidate();
    }

    private void drawAmplitudes(Canvas canvas)
    {
        // draw lines
        float width = (float)Math.ceil(canvas.getWidth()/150);
        linePaint.setStrokeWidth(width);
        int currentLineX = 0;
        for(int i=0; i<amplitudes.size(); i++)
        {
            linePaint.setColor(Color.argb(getAlpha(amplitudes.get(i)), 0, 0, 0));
            canvas.drawLine(currentLineX, 0, currentLineX, getHeight(), linePaint);
            currentLineX += width;
        }
    }

    private int getAlpha(int amplitude)
    {
        int alpha = (int)( (amplitude / (float) MAX_AMPLITUDE)* 255);
        if(alpha > 255)
            return 255;
        return alpha;
    }

    public void addWave(int frame)
    {
        this.waves.add(frame);
    }

    private void drawWaves(Canvas canvas)
    {
        // draw lines
        float width = (float)Math.ceil(canvas.getWidth()/150);
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
        if(frame > 255)
            return 255;
        return frame;
    }

    public void reset()
    {
        this.amplitudes.clear();
        this.waves.clear();
        invalidate();
    }

    public Sound getSound()
    {
        return this.mSound;
    }


}
