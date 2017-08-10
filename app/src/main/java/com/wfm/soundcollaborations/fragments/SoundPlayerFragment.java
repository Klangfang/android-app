package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

/**
 * Created by Markus Eberts on 10.10.16.
 */
public class SoundPlayerFragment extends Fragment {
    private final static String TAG = SoundPlayerFragment.class.getSimpleName();

    private TextView tvSoundName;
    private SeekBar sbTimeline;
    private TextView tvTime;
    private ImageButton ibState;
    private boolean playing;
    private SoundPlayer soundPlayer;

    private long soundID;
    private String soundFileURI;
    private SoundEntity sound;
    // Database
    private DatabaseHelper databaseHelper;
    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            initDBHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (getArguments().containsKey("fileURI")) {
            soundFileURI = getArguments().getString("fileURI");
        }

        if (getArguments().containsKey("soundID")){
            soundID = getArguments().getLong("soundID");

            try {
                sound = soundDao.queryForId(soundID);
                soundFileURI = sound.getFileURI();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.player_large, container, false);

        tvSoundName = (TextView) v.findViewById(R.id.tv_sound_name);
        sbTimeline = (SeekBar) v.findViewById(R.id.sb_player_timeline);
        tvTime = (TextView) v.findViewById(R.id.tv_player_time);
        ibState = (ImageButton) v.findViewById(R.id.ib_player_state);
        return v;
    }

    @Override
    public void onStart(){
        super.onStart();

        if (sound != null) {
            tvSoundName.setText(sound.getName());
        }

        playing = false;
        soundPlayer = new SoundPlayer();
        soundPlayer.switchTrack(soundFileURI);

        soundPlayer.setTimeListener(new SoundPlayer.TimeListener() {
            @Override
            public void onTimeUpdate(long time, float percentage) {
                tvTime.setText(String.format("%.2f", time / 1000f));
                sbTimeline.setProgress((int) (sbTimeline.getMax() * percentage));

                if (percentage == 1){
                    playing = false;
                    ibState.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        }, 200);

        ibState.setImageResource(android.R.drawable.ic_media_play);
        ibState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;

                if (playing){
                    soundPlayer.resume();
                    ibState.setImageResource(android.R.drawable.ic_media_pause);
                } else {
                    soundPlayer.pause();
                    ibState.setImageResource(android.R.drawable.ic_media_play);
                }
            }
        });

        sbTimeline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    soundPlayer.seek(progress / 100f);
                    soundPlayer.resume();
                    playing = true;
                    ibState.setImageResource(android.R.drawable.ic_media_pause);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initDBHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getActivity(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
    }
}
