package com.wfm.soundcollaborations.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.wfm.soundcollaborations.R;

import java.util.Random;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class TimeLimitAnimator {
    private ValueAnimator currentAnimator;
    private int[] limitColors;
    private int maxValue;
    private int lastValue;
    private View animatedView;
    private Random randGenerator;

    public TimeLimitAnimator(View animatedView, int maxValue, int[] limitColors){
        this.maxValue = maxValue;
        this.animatedView = animatedView;
        this.limitColors = limitColors;
        this.lastValue = 0;
        this.randGenerator = new Random();
    }

    public void start(long duration) {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
        }

        //animatedView.setBackgroundColor(limitColors[randGenerator.nextInt(limitColors.length)]);
        animate(0, maxValue, duration);
    }


    public void stop() {
        if (currentAnimator != null && currentAnimator.isRunning()) {
            currentAnimator.cancel();
            animate(lastValue, 0, 500);

            currentAnimator = null;
            lastValue = 0;
        }
    }

    private void animate(int from, int to, long duration){
        currentAnimator = ValueAnimator.ofInt(from, to);
        currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = animatedView.getLayoutParams();
                layoutParams.height = val;
                layoutParams.width = val;

                animatedView.setLayoutParams(layoutParams);
                lastValue = val;
                animatedView.invalidate();
            }
        });
        currentAnimator.addListener(new ValueAnimator.AnimatorListener(){
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                stop();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        currentAnimator.setDuration(duration);
        currentAnimator.start();
    }
}
