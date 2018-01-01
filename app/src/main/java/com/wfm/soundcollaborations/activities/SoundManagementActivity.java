package com.wfm.soundcollaborations.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.SoundListAdapter;
import com.wfm.soundcollaborations.database.SoundEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 09.10.16.
 */
public class SoundManagementActivity extends MainActivity {

    private ListView lvSounds;
    private SoundListAdapter soundListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.fragment_mysounds, (ViewGroup) findViewById(R.id.fl_content));

        lvSounds = (ListView) findViewById(R.id.lv_sounds);
    }

}
