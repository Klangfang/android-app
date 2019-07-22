package com.wfm.soundcollaborations.activities;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
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
 * Created by Markus Eberts on 15.10.16.
 */
public class BaseActivity extends AppCompatActivity implements FragmentObserver {
    private final static String TAG = BaseActivity.class.getSimpleName();

    // Permissions
    private final static int REQUEST_MULTIPLE_PERMISSIONS = 0;

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

        // Request permissions
        PermissionManager.requestPermissions(this, new String[] {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.MODIFY_AUDIO_SETTINGS}, REQUEST_MULTIPLE_PERMISSIONS);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Database
        try {
            initDatabaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    public void fragmentUpdate(Event event, Bundle args) {}

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
