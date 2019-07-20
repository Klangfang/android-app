package com.wfm.soundcollaborations.Collect;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.activities.SoundManagementActivity;
import com.wfm.soundcollaborations.activities.TrackManagementActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markus Eberts on 13.10.16.
 * Edited By Mohammed Abuiriban
 */
public class ProfileActivity extends MainActivity
{
    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.btn_sound_management)
    Button btnSoundManagement;
    @BindView(R.id.btn_track_management)
    Button btnTrackManagement;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        ButterKnife.bind(this);
        initBtnSoundManagement();
        initBtnTrackManagement();
    }

    private void initBtnSoundManagement()
    {
        btnSoundManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, SoundManagementActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initBtnTrackManagement()
    {
        btnTrackManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TrackManagementActivity.class);
                startActivity(intent);
            }
        });
    }


}
