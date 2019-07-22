package com.wfm.soundcollaborations.views;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.cardview.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
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
 * Created by Markus Eberts on 12.11.16.
 */
public class SongCompositionView extends CardView {

    // UI
    private TextView tvTitle;
    private TextView tvInfo;
    private TextView tvPlaces;
    private ImageButton ibMarkFavorite;
    private ImageButton ibExtend;
    private LinearLayout llContribute;
    private LinearLayout llTeam;
    private CircularPlayerView playerView;
    private TextView tvName;
    private View[] actions = new View[ActionType.values().length];

    private ActionType actionType = ActionType.TEAM;
    enum ActionType {
        TEAM,
        FAVORITE,
        NAME
    }

    private boolean extendable = false;
    private boolean displayContributionBar = false;

    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;

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
        View.inflate(getContext(), R.layout.song_composition, this);

        tvTitle = findViewById(R.id.composition_title);
        //tvInfo = (TextView) findViewById(R.id.tv_info);
        tvPlaces = findViewById(R.id.tv_places);
        //llContribute = findViewById(R.id.join_button);
        ibMarkFavorite = findViewById(R.id.ib_mark_favorite);
        tvName = findViewById(R.id.tv_name);
        playerView = findViewById(R.id.circular_player);

        actions = new View[]{llTeam, ibMarkFavorite, tvName};

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.PlayerSmall, 0, 0);

        try {
            String title = typedArray.getString(R.styleable.PlayerSmall_title);
            if (title != null){
                tvTitle.setText(title);
            }
            //tvInfo.setText(typedArray.getString(R.styleable.PlayerSmall_info));

            int fileRes = typedArray.getResourceId(R.styleable.PlayerSmall_file, -1);
            invalidate();

            if (!isInEditMode() && fileRes != -1) {
                playRes(fileRes);
            }
        } finally {
            typedArray.recycle();
        }

        typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.Song, 0, 0);

        try {
            actionType = ActionType.values()[typedArray.getInt(R.styleable.Song_action_type, actionType.ordinal())];
            extendable = typedArray.getBoolean(R.styleable.Song_extendable, extendable);
            displayContributionBar = typedArray.getBoolean(R.styleable.Song_display_contribution_bar, displayContributionBar);
        } finally {
            typedArray.recycle();
        }


        for (int i = 0; i < actions.length; i++){
            if (i != actionType.ordinal()){
                actions[i].setVisibility(GONE);
            }
        }

        if (!extendable){
            ibExtend.setVisibility(GONE);
        }

        if (!displayContributionBar){
            llContribute.setVisibility(GONE);
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

    public SongCompositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
