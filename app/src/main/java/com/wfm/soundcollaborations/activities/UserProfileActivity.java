package com.wfm.soundcollaborations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.views.PlayerLargeView;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 13.10.16.
 */
public class UserProfileActivity extends MainActivity {

    // UI
    private Button btnSoundManagement;
    private Button btnTrackManagement;
    private Button btnFriendlist;
    private PlayerLargeView soundPlayerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.user_profile_old, (ViewGroup) findViewById(R.id.content_layout));
        //getSupportActionBar().setTitle(preferences.getString(MainActivity.KEY_ACCOUNT_NAME, ""));

        btnSoundManagement = (Button) findViewById(R.id.btn_sound_management);
        btnTrackManagement = (Button) findViewById(R.id.btn_track_management);
        btnFriendlist = (Button) findViewById(R.id.btn_friendlist);
        soundPlayerView = (PlayerLargeView) findViewById(R.id.sound_player);

        btnSoundManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, SoundManagementActivity.class);
                startActivity(intent);
            }
        });

        btnTrackManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, TrackManagementActivity.class);
                startActivity(intent);
            }
        });

        btnFriendlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, FriendlistActivity.class);
                startActivity(intent);
            }
        });

        SoundEntity sound = null;
        if (preferences.contains(MainActivity.KEY_ACCOUNT_SOUND)){
            long soundID = preferences.getLong(MainActivity.KEY_ACCOUNT_SOUND, -1);

            try {
                sound = soundDao.queryForId(soundID);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (sound != null){
            soundPlayerView.play(sound);
        } else {
            soundPlayerView.setVisibility(View.GONE);
        }
    }
}
