package com.wfm.soundcollaborations.editor.views.composition;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by mohammed on 11/18/17.
 */

public class CompositionScrollView extends HorizontalScrollView
{
    private boolean canScroll = true;

    public CompositionScrollView(Context context)
    {
        super(context);
    }

    public CompositionScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return canScroll ? super.onInterceptTouchEvent(ev) : canScroll;
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        return canScroll ? super.onTouchEvent(ev) : canScroll;
    }

    @Override
    public boolean performClick()
    {
        return canScroll;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        canScroll = enabled;
    }
}
