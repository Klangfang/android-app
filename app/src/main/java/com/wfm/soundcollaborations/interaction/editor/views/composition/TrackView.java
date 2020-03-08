package com.wfm.soundcollaborations.interaction.editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.wfm.soundcollaborations.databinding.ViewTrackBinding;


public class TrackView extends LinearLayout {

    private Paint linePaint;
    private ViewTrackBinding binding;


    protected TrackView(Context context) {

        super(context);

        binding = ViewTrackBinding.inflate(LayoutInflater.from(context), this, true);

        init();

    }


    protected TrackView(Context context, AttributeSet attrs) {

        super(context, attrs);

        binding = ViewTrackBinding.inflate(LayoutInflater.from(context), this, true);

        init();

    }


    private void init() {

        setOrientation(LinearLayout.HORIZONTAL);
        setWillNotDraw(false);
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setColor(Color.rgb(0x5a, 0x5a, 0x5a));

        deactivate();

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        super.dispatchDraw(canvas);
        drawLine(canvas);

    }


    protected void drawLine(Canvas canvas) {

        canvas.drawLine(0, getHeight()/2, getWidth(), getHeight()/2, linePaint);

    }


    protected void activate() {

        linePaint.setColor(Color.WHITE);
        invalidate();

    }


    protected void deactivate() {

        linePaint.setColor(Color.rgb(0x5a, 0x5a, 0x5a));
        invalidate();

    }


    protected void addSoundView(SoundView soundView) {

        binding.llSoundsHolder.addView(soundView);

    }


    protected void deleteSoundViews(SoundView soundView) {

        binding.llSoundsHolder.removeView(soundView);

    }

}
