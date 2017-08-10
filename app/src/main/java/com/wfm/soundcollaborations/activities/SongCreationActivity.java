package com.wfm.soundcollaborations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.FriendEntity;
import com.wfm.soundcollaborations.database.GroupRelationEntity;
import com.wfm.soundcollaborations.database.GroupSongEntity;
import com.wfm.soundcollaborations.database.RandomSongEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 23.10.16.
 */
public class SongCreationActivity extends BaseActivity {

    private static final int PICK_FRIEND = 0;

    private EditText etTitle;
    private EditText etDescription;


    private Button btnGroup;
    private Button btnRandom;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_creation);

        etTitle = (EditText) findViewById(R.id.et_title);
        etDescription = (EditText) findViewById(R.id.et_description);

        btnGroup = (Button) findViewById(R.id.btn_group_collaboration);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongCreationActivity.this, FriendChoiceActivity.class);
                startActivityForResult(intent, PICK_FRIEND);
            }
        });

        btnRandom = (Button) findViewById(R.id.btn_random_collaboration);
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRandomSong(etTitle.getText().toString(), etDescription.getText().toString());
                Intent intent = new Intent(SongCreationActivity.this, SongEditorActivity.class);
                startActivity(intent);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FRIEND) {
            if (resultCode == RESULT_OK) {
                long[] selectedFriends = data.getLongArrayExtra("friendIds");
                List<FriendEntity> friendEntities = new ArrayList<>();

                for (long id : selectedFriends){
                    FriendEntity friend;

                    try {
                        friend = friendDao.queryForId(id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        continue;
                    }

                    friendEntities.add(friend);
                }

                createGroupSong(etTitle.getText().toString(), etDescription.getText().toString(),
                        friendEntities);

                Intent intent = new Intent(SongCreationActivity.this, SongEditorActivity.class);
                startActivity(intent);
            }
        }
    }

    private void createGroupSong(String title, String description, List<FriendEntity> friendEntities){
        GroupSongEntity groupSongEntity = new GroupSongEntity(title, description);

        try {
            groupSongDao.create(groupSongEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        for (FriendEntity friendEntity : friendEntities){
            GroupRelationEntity groupRelationEntity = new GroupRelationEntity(groupSongEntity, friendEntity);

            try {
                groupRelationDao.create(groupRelationEntity);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void createRandomSong(String title, String description){
        RandomSongEntity randomSongEntity = new RandomSongEntity(title, description);

        try {
            randomSongDao.create(randomSongEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}