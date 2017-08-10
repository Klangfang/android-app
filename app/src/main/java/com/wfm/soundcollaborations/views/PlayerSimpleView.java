package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 10.10.16.
 */
public class PlayerSimpleView extends LinearLayout {
    private final static String TAG = PlayerSimpleView.class.getSimpleName();

    private ImageButton ibState;
    private SoundVisualizationView visualization;

    private boolean playing;
    private SoundPlayer soundPlayer;

    private String soundFileURI;
    private SoundEntity sound;
    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;

    public PlayerSimpleView(Context context) {
        super(context);

        createLayout();
        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerSimpleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        createLayout();
        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void noSound(){
        ibState.setVisibility(GONE);
        visualize(new ArrayList<Integer>());
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


    public void visualize(List<Integer> amplitudes){
        visualization.setAmplitudes(amplitudes);
    }


    private void createLayout(){
        View.inflate(getContext(), R.layout.player_simple, this);

        ibState = (ImageButton) findViewById(R.id.ib_player_state);
        visualization = (SoundVisualizationView) findViewById(R.id.visualization);
    }


    private void initPlayer(){
        if (sound != null) {
            visualize(sound.getAmplitudes());
        } else {
            visualize(new ArrayList<Integer>());
        }

        ibState.setVisibility(VISIBLE);

        playing = false;
        soundPlayer = new SoundPlayer();
        soundPlayer.switchTrack(soundFileURI);

        soundPlayer.setTimeListener(new SoundPlayer.TimeListener() {
            @Override
            public void onTimeUpdate(long time, float percentage) {
                visualization.animateIndex(percentage);

                if (percentage == 1){
                    playing = false;
                    ibState.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        }, 25);

        ibState.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
        ibState.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;
                visualization.animateAmplitudes();

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
