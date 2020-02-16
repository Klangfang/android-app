package com.wfm.soundcollaborations.interaction.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.wfm.soundcollaborations.R;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class SongView extends View {
    private final static String TAG = SongView.class.getSimpleName();

    private ValueAnimator currentAnimator;
    private int lastValue = 0;

    private RectF rectF;
    private Paint paint;
    private int color = Color.BLUE;
    private int startRadius = 0;
    private int startPosX = 0;
    private int startPosY = 0;
    private int endPosX = 0;
    private int endPosY = 0;
    private int startId;
    private View startView;

    private TimeLimitListener listener;
    public interface TimeLimitListener{
        void finished();
    }

    public SongView(Context context, AttributeSet attrs) {
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

        rectF = new RectF();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
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

        canvas.drawOval(rectF, paint);
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

        lastValue = 0;

        startView = getRootView().findViewById(startId);

        endPosX = getLeft();
        endPosY = getTop();
        startPosX = (int) (startView.getLeft() + startView.getWidth() / 2f);
        startPosY = (int) (startView.getTop() + startView.getHeight() / 2f);

        float vecX = endPosX - startPosX;
        float vecY = endPosY - startPosY;

        float maxRadius = (float) Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));

        // Start animation
        animate(startView.getWidth() / 2, (int) maxRadius , duration);
    }


    public void stop() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            //animate(lastValue, 0, 500);

            currentAnimator = null;
            lastValue = 0;
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
                ViewGroup.LayoutParams layoutParams = SongView.this.getLayoutParams();
                layoutParams.height = val;
                layoutParams.width = val;

                updateCircle();
                lastValue = val;
                SongView.this.invalidate();
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
}
