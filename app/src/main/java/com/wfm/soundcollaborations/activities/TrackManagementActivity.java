package com.wfm.soundcollaborations.activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.GroupSongEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 31.10.16.
 */

public class TrackManagementActivity extends MainActivity {

    private ListView lvTracks;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.track_management, (ViewGroup) findViewById(R.id.content_layout));

        lvTracks = (ListView) findViewById(R.id.lv_tracks);

        List<GroupSongEntity> groupSongEntities = new ArrayList<>();
        try {
            groupSongEntities = groupSongDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> names = new ArrayList<>();

        for (GroupSongEntity e : groupSongEntities){
            names.add(e.getTitle());
        }

        ArrayAdapter<String> tracksAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                names);
        lvTracks.setAdapter(tracksAdapter);
    }
}
