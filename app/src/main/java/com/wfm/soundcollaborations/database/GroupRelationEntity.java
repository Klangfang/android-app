package com.wfm.soundcollaborations.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Markus Eberts on 27.10.16.
 */
@DatabaseTable(tableName = "groupRelation")
public class GroupRelationEntity {

    @DatabaseField(id = true)
    private long id;

    @DatabaseField(foreign = true, foreignAutoRefresh=true)
    private GroupSongEntity groupSongEntity;

    @DatabaseField(foreign = true, foreignAutoRefresh=true)
    private FriendEntity friendEntity;

    public GroupRelationEntity(){}

    public GroupRelationEntity(GroupSongEntity groupSongEntity, FriendEntity friendEntity){
        this.groupSongEntity = groupSongEntity;
        this.friendEntity = friendEntity;
    }

    public FriendEntity getFriendEntity() {
        return friendEntity;
    }

    public void setFriendEntity(FriendEntity friendEntity) {
        this.friendEntity = friendEntity;
    }

    public GroupSongEntity getGroupSongEntity() {
        return groupSongEntity;
    }

    public void setGroupSongEntity(GroupSongEntity groupSongEntity) {
        this.groupSongEntity = groupSongEntity;
    }

    public long getId() {
        return id;
    }
}
