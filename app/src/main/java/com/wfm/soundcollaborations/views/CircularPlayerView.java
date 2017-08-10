package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;
import com.wfm.soundcollaborations.sound.SoundPlayer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by Markus Eberts on 16.11.16.
 */
public class CircularPlayerView extends RelativeLayout{
    private final static String TAG = CircularPlayerView.class.getSimpleName();

    private CircularTimelineView timelineView;
    private ImageButton ibState;
    private boolean playing;
    private SoundPlayer soundPlayer;

    private String soundFileURI;
    private int soundFileRes;
    private SoundEntity sound;

    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;


    public CircularPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void play(String soundFileURI){
        this.soundFileURI = soundFileURI;

        initPlayer();
    }


    public void play(long soundID){
        try {
            this.sound = soundDao.queryForId(soundID);
            this.soundFileURI = sound.getFileURI();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        initPlayer();
    }


    public void play(SoundEntity sound){
        this.sound = sound;
        this.soundFileURI = sound.getFileURI();

        initPlayer();
    }


    public void playRes(int res){
        this.soundFileRes = res;

        initPlayer();
    }


    private void init(Context context, AttributeSet attrs){
        View.inflate(getContext(), R.layout.player_circular, this);

        timelineView = (CircularTimelineView) findViewById(R.id.timeline);
        ibState = (ImageButton) findViewById(R.id.ib_player_state);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.CircularTimeline, 0, 0);

        float progress = timelineView.getProgress();
        float thickness = timelineView.getThickness();
        int color = timelineView.getColor();

        try {
            progress = typedArray.getFloat(R.styleable.CircularTimeline_progress, progress);
            thickness = typedArray.getDimension(R.styleable.CircularTimeline_thickness, thickness);
            color = typedArray.getInt(R.styleable.CircularTimeline_color, color);
        } finally {
            typedArray.recycle();
        }

        timelineView.setProgress(progress);
        timelineView.setThickness(thickness);
        timelineView.setColor(color);
    }


    private void initPlayer(){
        playing = false;
        soundPlayer = new SoundPlayer();

        if (soundFileURI != null) {
            soundPlayer.switchTrack(soundFileURI);
        } else {
            soundPlayer.switchTrack(getContext(), soundFileRes);
        }

        soundPlayer.setTimeListener(new SoundPlayer.TimeListener() {
            @Override
            public void onTimeUpdate(long time, float percentage) {
                timelineView.setProgress((int) (timelineView.getMax() * percentage));

                if (percentage == 1){
                    playing = false;
                    ibState.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    timelineView.setProgress(0);
                    //soundPlayer.stop();
                }
            }
        }, 25);

        ibState.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        ibState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;

                if (playing){
                    soundPlayer.resume();
                    ibState.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                } else {
                    soundPlayer.pause();
                    ibState.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });
    }


    private void initDBHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getContext(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
    }
}
