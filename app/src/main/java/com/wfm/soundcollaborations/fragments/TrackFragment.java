package com.wfm.soundcollaborations.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.SongCreationActivity;
import com.wfm.soundcollaborations.database.GroupSongEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammed on 10/5/17.
 */

public class TrackFragment extends Fragment{

    private ListView lvTracks;
    private FloatingActionButton btnCreate;
    private View.OnClickListener btnCreateListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), SongCreationActivity.class);
            startActivity(intent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.track_management, container, false);

        lvTracks = (ListView) v.findViewById(R.id.lv_tracks);

        return v;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

}