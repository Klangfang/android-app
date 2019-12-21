package com.wfm.soundcollaborations.Editor.views.composition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.wfm.soundcollaborations.Editor.activities.EditorActivity;
import com.wfm.soundcollaborations.Editor.tasks.VisualizeSoundTask;
import com.wfm.soundcollaborations.Editor.utils.DPUtils;
import com.wfm.soundcollaborations.R;

import java.util.ArrayList;
import java.util.UUID;


public class SoundView extends View {
    private static final String TAG = SoundView.class.getSimpleName();

    private SoundViewStatus soundViewStatus;

    private View deleteBtn;
    private TrackViewContainer trackViewContainer;

    private Paint linePaint;
    private ArrayList<Integer> waves;
    private int trackIndex;

    private Path clipPath;
    private RectF rectangle;
    private Paint rectPaint;
    int radius = 50;
    private Paint viewPaint;

    private String uuid;


    private static final int DOWNLOAD_COLOR = R.color.color_primary;
    private static final int RECORD_COLOR = R.color.color_my_sound;
    private static final int SELECT_FOR_DELETE_COLOR = R.color.color_error;


    public static class Builder {

        private final Context context;

        private SoundViewStatus status;
        private Integer trackIndex;
        private Integer startPosition;
        private Integer duration;
        private String url; //optional


        Builder(Context context) {

            this.context = context;

        }


        Builder status(SoundViewStatus status) {

            this.status = status;
            return this;

        }


        Builder trackIndex(Integer trackIndex) {

            this.trackIndex = trackIndex;
            return this;

        }


        Builder startPosition(Integer startPosition) {

            this.startPosition = startPosition;
            return this;

        }


        Builder url(String url) {

            this.url = url;
            return this;

        }


        public Builder duration(Integer duration) {

            this.duration = duration;
            return this;

        }


        public SoundView build(TrackViewContainer trackViewContainer) {

            int layoutWidth = status.equals(SoundViewStatus.DOWNLOAD) ? DPUtils.getValueInDP(duration) : 0;
            int layoutHeight = DPUtils.TRACK_HEIGHT;
            int marginLeft = status.equals(SoundViewStatus.DOWNLOAD) ? DPUtils.getValueInDP(startPosition) : startPosition;
            RelativeLayout.LayoutParams soundParams =
                    new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
            soundParams.setMargins(marginLeft, 0, 0, 0);
            SoundView soundView = new SoundView(context, status, trackViewContainer);
            soundView.setLayoutParams(soundParams);
            soundView.setTrackIndex(trackIndex);
            soundView.setOnLongClickListener(soundView::update);

            if (soundView.getSoundViewStatus().equals(SoundViewStatus.DOWNLOAD)) {
                VisualizeSoundTask soundTask = new VisualizeSoundTask(soundView, url);
                soundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }


            return soundView;

        }

    }


    private SoundView(Context context, SoundViewStatus soundViewStatus, TrackViewContainer trackViewContainer) {

        super(context);
        this.soundViewStatus = soundViewStatus;
        EditorActivity editorActivity = (EditorActivity) context;
        deleteBtn = editorActivity.findViewById(R.id.btn_delete);
        this.trackViewContainer = trackViewContainer;
        init();

    }


    public SoundView(Context context, AttributeSet attrs) {

        super(context, attrs);
        this.soundViewStatus = SoundViewStatus.RECORD;
        init();

    }

    private void init() {

        uuid = UUID.randomUUID().toString();

        // clipping
        rectangle = new RectF();
        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        initColor();
        //rectPaint.setStyle(Paint.Style.FILL); //das hier macht nichts
        //rectPaint.setStrokeCap(Paint.Cap.ROUND); //das hier macht nichts
        // setBackground(new ColorDrawable(getResources().getColor(R.color.color_error))); //sets the wrapper background color of downloaded sounds
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

    public void setTrackIndex(int trackIndex) {
        this.trackIndex = trackIndex;
    }

    public void increaseWidth(int width) {
        getLayoutParams().width = getLayoutParams().width + width;
        invalidate();
    }

    public int getTrackIndex() {
        return trackIndex;
    }


    // Set and change fill color of recorded sound when longclicked
    private void refresh() {

        soundViewStatus = hasFinishRecordState() ? SoundViewStatus.SELECT_FOR_DELETE : SoundViewStatus.RECORD_FINISH;
        deleteBtn.setEnabled(trackViewContainer.hasDeleteSoundViews() || hasDeleteState());
        int color = hasFinishRecordState() ? RECORD_COLOR : SELECT_FOR_DELETE_COLOR;
        rectPaint.setColor(getResources().getColor(color));
        invalidate();

    }


    private void initColor() {

        rectPaint.setColor(getResources().getColor(soundViewStatus.equals(SoundViewStatus.DOWNLOAD) ? DOWNLOAD_COLOR : RECORD_COLOR));

    }


    public boolean hasRecordState() {

        return soundViewStatus.equals(SoundViewStatus.RECORD);

    }


    public boolean hasFinishRecordState() {

        return soundViewStatus.equals(SoundViewStatus.RECORD_FINISH);

    }


    public boolean hasDeleteState() {

        return soundViewStatus.equals(SoundViewStatus.SELECT_FOR_DELETE);

    }


    //TODO no param neaded?!!!!
    public boolean update(View clickView) {

        float xPosition = clickView.getX();
        Log.v("long clicked", "pos: " + xPosition);

        if (clickView instanceof SoundView) {

            SoundView soundView = (SoundView) clickView;

            soundView.refresh();

            return true;

        }

        return false;

    }


    public String getUuid() {

        return uuid;

    }


    public SoundViewStatus getSoundViewStatus() {

        return soundViewStatus;

    }


    public String finishRecording() {

        if (hasRecordState()) {
            soundViewStatus = SoundViewStatus.RECORD_FINISH;
        }

        return uuid;

    }
}
