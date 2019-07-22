package com.wfm.soundcollaborations.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.helper.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class TimeLimitView extends View {
    private final static String TAG = TimeLimitView.class.getSimpleName();
    private static final int MAX_AMPLITUDE = 32767;

    private List<Integer> amplitudes;
    private List<Integer> amplitudeColors;
    private ValueAnimator currentAnimator;
    private int lastValue = 0;

    private RectF rectF;
    private RectF amplitudeRectF;
    private Paint fillPaint;
    private Paint amplitudePaint;
    private int color = Color.BLUE;
    private int startRadius = 0;
    private int startPosX = 0;
    private int startPosY = 0;
    private int endPosX = 0;
    private int endPosY = 0;
    private int startId;
    private View startView;

    private int lightColor;
    private int darkColor;

    private TimeLimitListener listener;
    public interface TimeLimitListener{
        void finished();
    }

    public TimeLimitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.TimeLimit, 0, 0);

        try {
            startRadius = typedArray.getInt(R.styleable.TimeLimit_start_radius, startRadius);
            color = typedArray.getColor(R.styleable.TimeLimit_circle_color, color);
            startId = typedArray.getResourceId(R.styleable.TimeLimit_start, startId);
        } finally {
            typedArray.recycle();
        }

        lightColor = ContextCompat.getColor(getContext(), R.color.yellow_light);
        darkColor = ContextCompat.getColor(getContext(), R.color.yellow_dark);

        amplitudes = new ArrayList<>();
        amplitudeColors = new ArrayList<>();
        rectF = new RectF();
        amplitudeRectF = new RectF();
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);

        amplitudePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        amplitudePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

        updateCircle();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // canvas.drawOval(rectF, fillPaint);

        float s = 0;

        if (!amplitudes.isEmpty()) {
            s = ((float) lastValue - (startView.getWidth() / 1.5f)) / amplitudes.size();
        }

        amplitudePaint.setStrokeWidth(s);

        float startX = lastValue - s / 2;
        int max = 0;

        if (!amplitudes.isEmpty()) {
            max = Collections.max(amplitudes);
        }

        max = MAX_AMPLITUDE;
        int amplitudeColorsSize = amplitudeColors.size();
        int c = 0;
        int amplitude = 0;

        for (int i = 0; i < amplitudes.size(); i++) {
            amplitude = amplitudes.get(i);

            if (i < amplitudeColorsSize){
                amplitudePaint.setColor(amplitudeColors.get(i));
            } else {
                c = Color.argb(Color.alpha(color),
                        (int) Utility.scale(amplitude, 0, max, Color.red(lightColor), Color.red(darkColor)),
                        (int) Utility.scale(amplitude, 0, max, Color.green(lightColor), Color.green(darkColor)),
                        (int) Utility.scale(amplitude, 0, max, Color.blue(lightColor), Color.blue(darkColor)));

                amplitudePaint.setColor(c);
                amplitudeColors.add(c);
            }

            amplitudeRectF.set(startPosX - startX,
                    startPosY - startX,
                    startPosX + startX,
                    startPosY + startX);

            canvas.drawOval(amplitudeRectF, amplitudePaint);

            startX -= s;
        }
    }


    private void updateCircle(){
        rectF.set(startPosX - lastValue,
                startPosY - lastValue,
                startPosX + lastValue,
                startPosY + lastValue);
    }


    public void start(long duration) {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }

        startView = getRootView().findViewById(startId);
        lastValue = startView.getWidth() / 2;

        endPosX = getLeft();
        endPosY = getTop();
        startPosX = (int) (startView.getLeft() + startView.getWidth() / 2f);
        startPosY = (int) (startView.getTop() + startView.getHeight() / 2f);

        float vecX = endPosX - startPosX;
        float vecY = endPosY - startPosY;

        float maxRadius = (float) Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));

        // Start animation
        animate(lastValue, (int) maxRadius , duration);
    }


    public void stop() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            //animate(lastValue, 0, 500);

            currentAnimator = null;
            lastValue = 0;
            updateCircle();
            invalidate();
        }
    }

    private void animate(int from, int to, long duration){
        currentAnimator = ValueAnimator.ofInt(from, to);
        currentAnimator.setStartDelay(0);
        currentAnimator.setInterpolator(new LinearInterpolator());
        currentAnimator.setDuration(duration);
        currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();

                updateCircle();
                lastValue = val;
                TimeLimitView.this.invalidate();
            }
        });
        currentAnimator.addListener(new ValueAnimator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                notifyListener();
                //stop();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        currentAnimator.start();
    }

    public void setTimeLimitListener(TimeLimitListener listener){
        this.listener = listener;
    }

    public void notifyListener(){
        if (this.listener != null){
            listener.finished();
        }
    }

    public void setAmplitudes(List<Integer> amplitudes){
        this.amplitudes = amplitudes;
        //invalidate();
    }
}
