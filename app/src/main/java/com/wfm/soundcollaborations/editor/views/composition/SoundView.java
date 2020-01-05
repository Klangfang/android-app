package com.wfm.soundcollaborations.editor.views.composition;

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

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.editor.activities.EditorActivity;
import com.wfm.soundcollaborations.editor.tasks.VisualizeSoundTask;
import com.wfm.soundcollaborations.editor.utils.DPUtils;

import java.util.ArrayList;
import java.util.UUID;

import static com.wfm.soundcollaborations.editor.views.composition.SoundViewStatus.SELECT_FOR_DELETE;


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
    private static final int LOCAL_COLOR = R.color.color_my_sound;
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

            SoundView soundView = new SoundView(context, status, trackViewContainer);
            int layoutWidth = 0;
            int layoutHeight = DPUtils.TRACK_MAX_HEIGHT;
            int marginLeft = startPosition;
            if (status.equals(SoundViewStatus.REMOTE)) {

                layoutWidth = DPUtils.getValueInDP(duration);
                marginLeft = DPUtils.getValueInDP(startPosition);
            } else {

                soundView.setOnLongClickListener(soundView::selectForDelete);

            }

            RelativeLayout.LayoutParams soundParams =
                    new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
            soundParams.setMargins(marginLeft, 0, 0, 0);

            soundView.setLayoutParams(soundParams);
            soundView.setTrackIndex(trackIndex);

            if (soundView.getSoundViewStatus().equals(SoundViewStatus.REMOTE)) {
                VisualizeSoundTask soundTask = new VisualizeSoundTask(soundView, url);
                soundTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }


            return soundView;

        }

    }


    private SoundView(Context context, SoundViewStatus soundViewStatus, TrackViewContainer trackViewContainer) {

        super(context);
        this.soundViewStatus = soundViewStatus;
        //TODO alternative über runnable ectc.....
        EditorActivity editorActivity = (EditorActivity) context;
        deleteBtn = editorActivity.findViewById(R.id.btn_delete);
        this.trackViewContainer = trackViewContainer;
        init();

    }


    public SoundView(Context context, AttributeSet attrs) {

        super(context, attrs);
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


    // Sets and change fill color of local sound when longclicked
    private void refreshAfterSelection() {

        soundViewStatus = hasLocalCompletedState() ? SELECT_FOR_DELETE : SoundViewStatus.LOCAL_COMPLETED;
        deleteBtn.setEnabled(trackViewContainer.hasDeleteSoundViews() || hasDeleteState());
        int color = hasLocalCompletedState() ? LOCAL_COLOR : SELECT_FOR_DELETE_COLOR;
        rectPaint.setColor(getResources().getColor(color, null));
        invalidate();

    }


    private void initColor() {

        int newColor = hasRemoteState() ? DOWNLOAD_COLOR : LOCAL_COLOR;
        rectPaint.setColor(getResources().getColor(newColor, null));

    }


    public boolean hasRemoteState() {

        return soundViewStatus.equals(SoundViewStatus.REMOTE);

    }


    public boolean hasLocalRecordingState() {

        return soundViewStatus.equals(SoundViewStatus.LOCAL_RECORDING);

    }


    public boolean hasLocalCompletedState() {

        return soundViewStatus.equals(SoundViewStatus.LOCAL_COMPLETED);

    }


    public boolean hasDeleteState() {

        return soundViewStatus.equals(SELECT_FOR_DELETE);

    }


    public boolean selectForDelete(View view) {

        float xPosition = view.getX();
        Log.v("long clicked", "pos: " + xPosition);

        if (view instanceof SoundView) {

            SoundView soundView = (SoundView) view;

            if (soundView.hasLocalCompletedState() || soundView.isSelectedForDelete()) {

                refreshAfterSelection();

                return true;

            }

            return false;

        }

        return false;

    }


    public String getUuid() {

        return uuid;

    }


    public SoundViewStatus getSoundViewStatus() {

        return soundViewStatus;

    }


    public boolean isSelectedForDelete() {

        return soundViewStatus.equals(SELECT_FOR_DELETE);

    }


    /**
     * Changes the state for the sound view to local completed
     *
     * @return uuid of the sound view
     */
    public String completeLocalSoundView() {

        if (hasLocalRecordingState()) {
            soundViewStatus = SoundViewStatus.LOCAL_COMPLETED;
        }

        return uuid;

    }
}
