package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 13.11.16.
 */
public class CircularTimelineView extends View {
    private final static String TAG = CircularTimelineView.class.getSimpleName();

    private float progress = 0;
    private int min = 0;
    private int max = 100;

    private float thickness = 4;
    private int startAngle = -90;
    private int color = Color.DKGRAY;

    private RectF rectF;
    private Paint backgroundPaint;
    private Paint foregroundPaint;


    public CircularTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircularTimeline, 0, 0);

        try {
            thickness = typedArray.getDimension(R.styleable.CircularTimeline_thickness, thickness);
            progress = typedArray.getFloat(R.styleable.CircularTimeline_progress, progress);
            color = typedArray.getInt(R.styleable.CircularTimeline_color, color);
            min = typedArray.getInt(R.styleable.CircularTimeline_min, min);
            max = typedArray.getInt(R.styleable.CircularTimeline_max, max);
        } finally {
            typedArray.recycle();
        }

        rectF = new RectF();
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);

        foregroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foregroundPaint.setStyle(Paint.Style.STROKE);

        setThickness(thickness);
        setColor(color);
    }


    private int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + thickness / 2, 0 + thickness / 2, min - thickness / 2, min - thickness / 2);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float angle = 360 * progress / max;
        canvas.drawOval(rectF, backgroundPaint);
        canvas.drawArc(rectF, startAngle, angle, false, foregroundPaint);
    }


    public float getProgress() {
        return progress;
    }


    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }


    public int getMin() {
        return min;
    }


    public void setMin(int min) {
        this.min = min;
    }


    public int getMax() {
        return max;
    }


    public void setMax(int max) {
        this.max = max;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
        this.backgroundPaint.setStrokeWidth(thickness);
        this.foregroundPaint.setStrokeWidth(thickness);
        invalidate();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        //this.backgroundPaint.setColor(adjustAlpha(color, 0.3f));
        this.backgroundPaint.setColor(Color.TRANSPARENT);
        this.foregroundPaint.setColor(color);
        invalidate();
    }
}
