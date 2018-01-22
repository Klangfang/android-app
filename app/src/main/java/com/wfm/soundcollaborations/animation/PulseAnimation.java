package com.wfm.soundcollaborations.animation;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * Created by mohammed on 10/6/17.
 */

public class PulseAnimation
{
    View view;
    ObjectAnimator scaleDown;

    public PulseAnimation(View view)
    {
        this.view = view;
        init();
    }

    private void init()
    {
         scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                this.view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);

        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
    }

    public void start()
    {
        scaleDown.start();
    }

    public void stop()
    {
        scaleDown.cancel();
        view.setScaleX(1f);
        view.setScaleY(1f);
    }
}
