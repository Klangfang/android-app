package com.wfm.soundcollaborations.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.activities.SongCreationActivity;
import com.wfm.soundcollaborations.activities.SoundEditorActivity;
import com.wfm.soundcollaborations.adapter.SoundListAdapter;
import com.wfm.soundcollaborations.adapter.ViewPagerAdapter;
import com.wfm.soundcollaborations.database.GroupSongEntity;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.sound.SoundRecorder;
import com.wfm.soundcollaborations.sound.SoundVisualizer;
import com.wfm.soundcollaborations.views.PlayerSimpleView;
import com.wfm.soundcollaborations.views.SoundVisualizationView;
import com.wfm.soundcollaborations.views.TimeLimitView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Markus Eberts on 09.11.16.
 */
public class UserProfileFragment extends BaseFragment {

    private TabLayout tlProfile;
    private ViewPager vpProfile;
    private ViewPagerAdapter vpProfileAdapter;
    private PlayerSimpleView soundPlayer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.user_profile, container, false);

        soundPlayer = (PlayerSimpleView) v.findViewById(R.id.sound_player);

        // Toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ActionBar supportActionBar = ((AppCompatActivity)(getActivity())).getSupportActionBar();

        supportActionBar.setHomeButtonEnabled(true);
        supportActionBar.setDisplayHomeAsUpEnabled(false);
        supportActionBar.setTitle(preferences.getString(MainActivity.KEY_ACCOUNT_NAME, ""));
        setHasOptionsMenu(true);

        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();

        tlProfile = (TabLayout) getActivity().findViewById(R.id.tl_user_profile);
        vpProfile = (ViewPager) getActivity().findViewById(R.id.vp_user_profile);

        vpProfileAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                SoundFragment.class,
                TrackFragment.class);
        vpProfile.setAdapter(vpProfileAdapter);

        final TabLayout.Tab soundTab = tlProfile.newTab();
        final TabLayout.Tab trackTab = tlProfile.newTab();

        tlProfile.addTab(trackTab);
        tlProfile.addTab(soundTab);
        tlProfile.setupWithViewPager(vpProfile);

        soundTab.setText("Meine Klänge");
        trackTab.setText("Fertige Collagen");

        // Sound visualization
        SoundVisualizer vs = new SoundVisualizer();
        long profileSoundID = preferences.getLong(MainActivity.KEY_ACCOUNT_SOUND, -1);

        SoundEntity sound = null;
        try {
            sound = soundDao.queryForId(profileSoundID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (sound != null) {
            soundPlayer.play(sound);
        } else {
            soundPlayer.noSound();
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_profile, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    public static class SoundFragment extends BaseFragment implements TimeLimitView.TimeLimitListener, SoundRecorder.RecordListener {
        private final static String TAG = SoundFragment.class.getSimpleName();

        // Record
        private final long MAX_DURATION = 30000; // 30000, 5000

        private Activity activity;
        private ListView lvSounds;
        private SoundListAdapter soundListAdapter;

        private DisplayMetrics metrics;

        private SoundRecorder soundRecorder;

        private final static String fileBaseURI = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        private final static String fileExtension = ".3gp";
        private String currentFileURI;

        private FloatingActionButton btnRecord;
        private TimeLimitView timeLimit;


        private View.OnTouchListener recordListener = new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                switch (m.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentFileURI = fileBaseURI + UUID.randomUUID().toString() + fileExtension;

                        // Start recorder and animation
                        soundRecorder.start(currentFileURI, MAX_DURATION);
                        timeLimit.start(MAX_DURATION);
                        break;

                    case MotionEvent.ACTION_UP:
                        soundRecorder.stop();
                        break;
                }
                return true;
            }
        };


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            activity = getActivity();

            View v = inflater.inflate(R.layout.sound_management, container, false);
            lvSounds = (ListView) v.findViewById(R.id.lv_sounds);
            timeLimit = (TimeLimitView) v.findViewById(R.id.time_limit);
            timeLimit.setTimeLimitListener(this);

            // Record button
            btnRecord = (FloatingActionButton) getActivity().findViewById(R.id.btn_action);
            initActionButton();

            // Display metrics to get screen height
            metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            // Sound recorder
            soundRecorder = new SoundRecorder(activity);
            soundRecorder.setListener(this);

            refresh();
            return v;
        }

        @Override
        public void onResume(){
            super.onResume();
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            initActionButton();
        }

        private void initActionButton(){
            if (btnRecord != null) {
                if (getUserVisibleHint()) {
                    // Show record button
                    btnRecord.setImageResource(R.drawable.red_circle);
                    btnRecord.setOnTouchListener(recordListener);
                    btnRecord.setVisibility(View.VISIBLE);
                } else {
                    btnRecord.setOnTouchListener(null);
                }
            }
        }

        public void deleteSound(long soundID){
            try {
                soundDao.deleteById(soundID);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
            refresh();
        }

        private void refresh(){
            List<SoundEntity> sounds = new ArrayList<>();
            try {
                sounds = soundDao.queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            soundListAdapter = new SoundListAdapter(this, R.layout.sound_row, sounds);
            lvSounds.setAdapter(soundListAdapter);
        }

        private void recordStopped(){
            timeLimit.stop();
            List<Integer> amplitudes = soundRecorder.getAmplitudes();
            int[] amplitudesArray = new int[soundRecorder.getAmplitudes().size()];

            for (int i = 0; i < amplitudes.size(); i++){
                amplitudesArray[i] = amplitudes.get(i);
            }

            Intent intent = new Intent(activity, SoundEditorActivity.class);
            intent.putExtra("fileURI", currentFileURI);
            intent.putExtra("amplitudes", amplitudesArray);
            startActivity(intent);
        }

        @Override
        public void finished() {
            Log.e(TAG, "Time limit stopped");
        }

        @Override
        public void recordUpdate(int e, Object data) {
            if (e == 1){
                recordStopped();
            } else if (e == 2){
                List<Integer> amplitudes = (List<Integer>) data;
                timeLimit.setAmplitudes(amplitudes);
            } else if(e == 4){
                timeLimit.stop();
            }
        }
    }


    public static class TrackFragment extends BaseFragment{

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

            btnCreate = (FloatingActionButton) getActivity().findViewById(R.id.btn_action);
            initActionButton();

            return v;
        }

        @Override
        public void onStart(){
            super.onStart();

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

            ArrayAdapter<String> tracksAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    names);
            lvTracks.setAdapter(tracksAdapter);

            if (!names.isEmpty()){
                //v.findViewById(R.id.tv_no_track).setVisibility(View.GONE);
            }
        }


        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            initActionButton();
        }

        private void initActionButton(){
            if (btnCreate != null) {
                if (getUserVisibleHint()) {
                    // Show creation button
                    btnCreate.setImageResource(R.drawable.ic_add_black_24dp);
                    btnCreate.setOnClickListener(btnCreateListener);
                    btnCreate.setVisibility(View.GONE);
                } else {
                    btnCreate.setOnClickListener(null);
                }
            }
        }
    }
}
