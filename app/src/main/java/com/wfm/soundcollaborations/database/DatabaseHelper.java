package com.wfm.soundcollaborations.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.wfm.soundcollaborations.R;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 13.10.16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "sound_collaborations";
    private static final int DATABASE_VERSION = 20;

    private Dao<SoundEntity, Long> soundDao;
    private Dao<TagEntity, Long> tagDao;
    private Dao<FriendEntity, Long> friendDao;
    private Dao<GroupSongEntity, Long> groupSongDao;
    private Dao<RandomSongEntity, Long> randomSongDao;
    private Dao<GroupRelationEntity, Long> groupRelationDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SoundEntity.class);
            TableUtils.createTable(connectionSource, TagEntity.class);
            TableUtils.createTable(connectionSource, FriendEntity.class);
            TableUtils.createTable(connectionSource, GroupSongEntity.class);
            TableUtils.createTable(connectionSource, RandomSongEntity.class);
            TableUtils.createTable(connectionSource, GroupRelationEntity.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource,
                          int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SoundEntity.class, false);
            TableUtils.dropTable(connectionSource, TagEntity.class, false);
            TableUtils.dropTable(connectionSource, FriendEntity.class, false);
            TableUtils.dropTable(connectionSource, GroupSongEntity.class, false);
            TableUtils.dropTable(connectionSource, RandomSongEntity.class, false);
            TableUtils.dropTable(connectionSource, GroupRelationEntity.class, false);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<SoundEntity, Long> getSoundDao() throws SQLException {
        if (soundDao == null) {
            soundDao = getDao(SoundEntity.class);
        }
        return soundDao;
    }


    public Dao<TagEntity, Long> getTagDao() throws SQLException {
        if (tagDao == null) {
            tagDao = getDao(TagEntity.class);
        }
        return tagDao;
    }


    public Dao<FriendEntity, Long> getFriendDao() throws SQLException {
        if (friendDao == null) {
            friendDao = getDao(FriendEntity.class);
        }
        return friendDao;
    }


    public Dao<GroupSongEntity, Long> getGroupSongDao() throws SQLException {
        if (groupSongDao == null) {
            groupSongDao = getDao(GroupSongEntity.class);
        }
        return groupSongDao;
    }


    public Dao<RandomSongEntity, Long> getRandomSongDao() throws SQLException {
        if (randomSongDao == null) {
            randomSongDao = getDao(RandomSongEntity.class);
        }
        return randomSongDao;
    }


    public Dao<GroupRelationEntity, Long> getGroupRelationDao() throws SQLException {
        if (groupRelationDao == null) {
            groupRelationDao = getDao(GroupRelationEntity.class);
        }
        return groupRelationDao;
    }
}
