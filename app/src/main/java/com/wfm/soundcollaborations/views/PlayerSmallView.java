package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Markus Eberts on 10.10.16.
 */
public class PlayerSmallView extends LinearLayout {
    private final static String TAG = PlayerSmallView.class.getSimpleName();

    private TextView tvTitle;
    private TextView tvInfo;
    private CircularPlayerView playerView;

    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;


    public PlayerSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void play(String soundFileURI){
        playerView.play(soundFileURI);
    }


    public void playRes(int res){
        playerView.playRes(res);
    }


    public void play(long soundID){
        SoundEntity sound;
        try {
            sound = soundDao.queryForId(soundID);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        play(sound);
    }


    public void play(SoundEntity sound){
        playerView.play(sound);
        initDisplay(sound.getName(), sound.getCreationDate());
    }


    private void init(Context context, AttributeSet attrs){
        View.inflate(getContext(), R.layout.player_small, this);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        playerView = (CircularPlayerView) findViewById(R.id.circular_player);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PlayerSmall, 0, 0);

        try {
            tvTitle.setText(typedArray.getString(R.styleable.PlayerSmall_title));
            tvInfo.setText(typedArray.getString(R.styleable.PlayerSmall_info));

            int fileRes = typedArray.getResourceId(R.styleable.PlayerSmall_file, -1);
            invalidate();

            if (!isInEditMode() && fileRes != -1) {
                playRes(fileRes);
            }
        } finally {
            typedArray.recycle();
        }
    }


    private void initDisplay(String title, Date date){
        tvTitle.setText(title);

        if (date != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            tvInfo.setText(dateFormat.format(date));
        } else {
            tvInfo.setVisibility(View.INVISIBLE);
        }
    }


    private void initDBHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getContext(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
    }
}
