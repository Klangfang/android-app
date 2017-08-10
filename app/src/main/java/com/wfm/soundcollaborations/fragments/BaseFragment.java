package com.wfm.soundcollaborations.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.SoundRecordActivity;
import com.wfm.soundcollaborations.activities.UserProfileActivity;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.FriendEntity;
import com.wfm.soundcollaborations.database.GroupRelationEntity;
import com.wfm.soundcollaborations.database.GroupSongEntity;
import com.wfm.soundcollaborations.database.RandomSongEntity;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;
import com.wfm.soundcollaborations.fragments.interfaces.FragmentObserver;
import com.wfm.soundcollaborations.misc.PermissionManager;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 09.11.16.
 */
public class BaseFragment extends Fragment {

    // Preferences
    protected SharedPreferences preferences;

    // Database
    private DatabaseHelper databaseHelper;
    protected Dao<SoundEntity, Long> soundDao;
    protected Dao<TagEntity, Long> tagDao;
    protected Dao<FriendEntity, Long> friendDao;
    protected Dao<GroupSongEntity, Long> groupSongDao;
    protected Dao<RandomSongEntity, Long> randomSongDao;
    protected Dao<GroupRelationEntity, Long> groupRelationDao;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Database
        try {
            initDatabaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initDatabaseHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(getContext(),
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
        friendDao = databaseHelper.getFriendDao();
        groupSongDao = databaseHelper.getGroupSongDao();
        randomSongDao = databaseHelper.getRandomSongDao();
        groupRelationDao = databaseHelper.getGroupRelationDao();
    }
}
