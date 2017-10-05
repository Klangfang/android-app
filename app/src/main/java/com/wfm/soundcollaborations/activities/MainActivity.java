package com.wfm.soundcollaborations.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.DatabaseHelper;
import com.wfm.soundcollaborations.database.FriendEntity;
import com.wfm.soundcollaborations.database.GroupRelationEntity;
import com.wfm.soundcollaborations.database.GroupSongEntity;
import com.wfm.soundcollaborations.database.RandomSongEntity;
import com.wfm.soundcollaborations.database.SoundEntity;
import com.wfm.soundcollaborations.database.TagEntity;
import com.wfm.soundcollaborations.fragments.ExplorationFragment;
import com.wfm.soundcollaborations.fragments.CompositionFragment;
import com.wfm.soundcollaborations.fragments.UserProfileFragment;
import com.wfm.soundcollaborations.fragments.interfaces.FragmentObserver;
import com.wfm.soundcollaborations.misc.PermissionManager;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 15.10.16.
 */
public class MainActivity extends BaseActivity implements FragmentObserver{
    private final static String TAG = MainActivity.class.getSimpleName();

    // Permissions
    private final static int REQUEST_MULTIPLE_PERMISSIONS = 0;

    // Preferences
    protected SharedPreferences preferences;
    public final static String KEY_ACCOUNT_NAME = "account_name";
    public final static String KEY_ACCOUNT_SOUND = "account_sound";

    // Database
    private DatabaseHelper databaseHelper;
    protected Dao<SoundEntity, Long> soundDao;
    protected Dao<TagEntity, Long> tagDao;
    protected Dao<FriendEntity, Long> friendDao;
    protected Dao<GroupSongEntity, Long> groupSongDao;
    protected Dao<RandomSongEntity, Long> randomSongDao;
    protected Dao<GroupRelationEntity, Long> groupRelationDao;

    // UI
    private BottomNavigationView bottomNavigation;
    protected FloatingActionButton btnAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base);

        // Request permissions
        PermissionManager.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS}, REQUEST_MULTIPLE_PERMISSIONS);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        bottomNavigation = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        btnAction = (FloatingActionButton) findViewById(R.id.btn_action);

        bottomNavigation.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.explore:
                                startExplorationActivity();
                                break;
                            case R.id.sound_post:
                                    startSoundPostActivity();
                                break;
                            case R.id.record:
                                    startProfileActivity();
                                break;
                        }
                        return false;
                    }
                });

        View profile = bottomNavigation.findViewById(R.id.record);
        profile.performClick();

        // Database
       /* try {
            initDatabaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    private void initDatabaseHelper() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(this,
                DatabaseHelper.class);

        soundDao = databaseHelper.getSoundDao();
        tagDao = databaseHelper.getTagDao();
        friendDao = databaseHelper.getFriendDao();
        groupSongDao = databaseHelper.getGroupSongDao();
        randomSongDao = databaseHelper.getRandomSongDao();
        groupRelationDao = databaseHelper.getGroupRelationDao();
    }


    @Override
    public void fragmentUpdate(FragmentObserver.Event event, Bundle args) {}

    private void startExplorationActivity(){
        ExplorationFragment explorationFragment = new ExplorationFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, explorationFragment);
        transaction.commit();
    }

    private void startSoundPostActivity(){
        CompositionFragment compositionFragment = new CompositionFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, compositionFragment);
        transaction.commit();
    }

    private void startProfileActivity(){
        UserProfileFragment userProfileFragment = new UserProfileFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_layout, userProfileFragment);
        transaction.commit();
    }


    public FloatingActionButton getActionButton(){
        return btnAction;
    }


    // TODO Implement
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                break;
            }
            default: super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
