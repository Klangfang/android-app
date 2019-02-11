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

import com.wfm.soundcollaborations.Editor.model.composition.Sound;
import com.wfm.soundcollaborations.R;

import java.util.ArrayList;

/**
 * Created by mohammed on 10/27/17.
 */

public class SoundView extends View {
    private static final String TAG = SoundView.class.getSimpleName();

    private static final int SOUND_SECOND_WIDTH = 60;

    private Paint linePaint;
    private ArrayList<Integer> waves;
    private int track = -1;

    private Path clipPath;
    private RectF rectangle;
    private Paint rectPaint;
    int radius = 50;
    private Paint viewPaint;

    private Sound sound;

    public SoundView(Context context) {
        super(context);
        init();
    }

    public SoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // clipping
        rectangle = new RectF();
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //rectPaint.setStyle(Paint.Style.FILL); //das hier macht nichts
        //rectPaint.setStrokeCap(Paint.Cap.ROUND); //das hier macht nichts
        rectPaint.setColor(getResources().getColor(R.color.color_primary)); // sets the color of downloaded sounds
        //setBackground(new ColorDrawable(getResources().getColor(R.color.color_error))); //sets the wrapper background color of downloaded sounds
        clipPath = new Path();
        //clipPath.addRoundRect(rectangle, radius, radius, Path.Direction.CW);

        // waves
        waves = new ArrayList<>();
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //linePaint.setStyle(Paint.Style.STROKE); /das hier macht nichts

        // Das folgende macht nichts
        //viewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //viewPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectangle.set(0, 0, getLayoutParams().width, getLayoutParams().height);
        canvas.drawRoundRect(rectangle, radius, radius, rectPaint); //This draws the soundView shape with rounded corners for both downloaded and recorded sounds
        drawWaves(canvas); //This draws vertical lines on top of the soundView shape to represent the amplitude
        //canvas.clipPath(clipPath); //This does nothing

    }

    public void addWave(int frame) {
        this.waves.add(frame);
        invalidate();
    }

    private void drawWaves(Canvas canvas) {
        // draw lines
        float width = 3;
        linePaint.setStrokeWidth(width);
        int currentLineX = 0;
        for (int i = 0; i < waves.size(); i++) {
            linePaint.setColor(Color.argb(getWaveAlpha(waves.get(i)), 0, 0, 0)); // TODO How to use Hex Colors here?
            canvas.drawLine(currentLineX, 0, currentLineX, getHeight(), linePaint);
            currentLineX += width;
        }
    }

    private int getWaveAlpha(int frame) {
        return (frame * 255 / 32768) * 4 > 255 ? 255 : (frame * 255 / 32768) * 4;
    }

    public void reset() {
        this.waves.clear();
        waves = null;
        getLayoutParams().width = 0;
        init();
        invalidate();
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public void increaseWidth(int width) {
        getLayoutParams().width = getLayoutParams().width + width;
        invalidate();
    }

    public int getTrack() {
        return this.track;
    }

    // Set fill color of recorded sounds
    public void setDefaultSoundColor() {
        rectPaint.setColor(getResources().getColor(R.color.color_my_sound));
        invalidate();
    }

    // Change fill color of recorded sound when longclicked
    public void setSelectedSoundColor() {
        rectPaint.setColor(getResources().getColor(R.color.color_error));
        invalidate();
    }

    public Sound getSound() {
        return sound;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    public long getSoundLength() {
        long soundLength = sound.getLengthInMs();
        int width = 0;
        width += (soundLength / 1000) * SOUND_SECOND_WIDTH;
        width += (soundLength % 1000) * SOUND_SECOND_WIDTH / 1000;
        return width;
    }
}
