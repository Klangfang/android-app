package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.helper.Utility;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Markus Eberts on 03.01.17.
 */
public class SoundVisualizationView extends View{
    private final static String TAG = SoundVisualizationView .class.getSimpleName();
    private static final int MAX_AMPLITUDE = 32767;

    private List<Integer> amplitudes;

    private Paint amplitudePaint;
    private Paint progressPaint;

    private int lightColor;
    private int darkColor;

    private int index = 0;
    private boolean animate = false;

    public SoundVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.amplitudePaint = new Paint();
        this.progressPaint = new Paint();
        this.progressPaint.setColor(Color.BLACK);
        this.progressPaint.setStrokeWidth(5);
        this.amplitudes = new ArrayList<>();

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.SoundVisualization, 0, 0);

        try {
            lightColor  = typedArray.getColor(R.styleable.SoundVisualization_light_color, Color.WHITE);
            darkColor  = typedArray.getColor(R.styleable.SoundVisualization_dark_color, Color.BLACK);
        } finally {
            typedArray.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();

        float s = ((float)w)/amplitudes.size();
        amplitudePaint.setStrokeWidth(s);

        float startX = s / 2;
        int max = 0;

        if (!amplitudes.isEmpty()) {
            max = Collections.max(amplitudes);
        }

        max = MAX_AMPLITUDE;
        canvas.drawColor(lightColor);
        int lastIndex = amplitudes.size();

        if (animate) {
            lastIndex = index;
        }

        for (int i = 0; i < lastIndex; i++) {
            int amplitude = amplitudes.get(i);

            amplitudePaint.setColor(Color.rgb((int) Utility.scale(amplitude, 0, max, Color.red(lightColor), Color.red(darkColor)),
                    (int) Utility.scale(amplitude, 0, max, Color.green(lightColor), Color.green(darkColor)),
                    (int) Utility.scale(amplitude, 0, max, Color.blue(lightColor), Color.blue(darkColor))));

            canvas.drawLine(startX, 0, startX, canvas.getHeight(), amplitudePaint);

            if (animate && i == lastIndex-1 && lastIndex != amplitudes.size()){
                canvas.drawLine(startX + s, 0, startX + s, canvas.getHeight(), progressPaint);
            }

            startX += s;
        }
    }

    public void addBytesArray(byte[] bytes)
    {
        ArrayList<Integer> data = new ArrayList<>();
        byte tmp[] = new byte[4];
        int tmpInteger;
        for(int i=0; i<bytes.length; i=i+4)
        {
            tmp[0] = bytes[i];
            tmp[1] = bytes[i+1];
            tmp[2] = bytes[i+2];
            tmp[3] = bytes[i+3];
            tmpInteger = new BigInteger(tmp).intValue();
            data.add(tmpInteger);
        }
        this.amplitudes.addAll(data);
        invalidate();
    }

    public void addAmplitude(int amplitude)
    {
        this.amplitudes.add(amplitude);
        invalidate();
    }

    public void setAmplitudes(List<Integer> amplitudes){
        this.amplitudes = amplitudes;
        Log.e(TAG, "Number of amplitude values: " + amplitudes.size());
        animate = false;
        invalidate();
    }

    public void animateAmplitudes(){
        animate = true;
        index = 0;
        invalidate();
    }

    public void animateIndex(float percent){
        index = (int) (percent * amplitudes.size());

        //Log.e(TAG, "Draw index: " + index);
        invalidate();
    }
}
