package com.wfm.soundcollaborations.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.FriendChoiceAdapter;
import com.wfm.soundcollaborations.database.FriendEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 24.10.16.
 */
public class FriendChoiceActivity extends MainActivity {

    private EditText etSearch;
    private ListView lvFriends;
    private FriendChoiceAdapter friendChoiceAdapter;
    private Button btnCompleteSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.friend_choice, (ViewGroup) findViewById(R.id.content_layout));

        etSearch = (EditText) findViewById(R.id.et_search);
        lvFriends = (ListView) findViewById(R.id.lv_items);
        btnCompleteSelection = (Button) findViewById(R.id.btn_complete_selection);
        friendChoiceAdapter = new FriendChoiceAdapter(this, R.layout.friend_choice_row);
        lvFriends.setAdapter(friendChoiceAdapter);
        lvFriends.setTextFilterEnabled(true);
        refresh();


        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cbFriendSelection = (CheckBox) view.findViewById(R.id.cb_selection);
                cbFriendSelection.setChecked(!cbFriendSelection.isChecked());
            }
        });

        btnCompleteSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return result to the calling activity
                FriendEntity[] selectedFriends = friendChoiceAdapter.
                        getSelectedItems().toArray(new FriendEntity[0]);

                long[] ids = new long[selectedFriends.length];
                for (int i = 0; i < selectedFriends.length; i++){
                    ids[i] = selectedFriends[i].getId();
                }

                Intent data = new Intent();
                data.putExtra("friendIds", ids);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            public void afterTextChanged(Editable s) {
                String searchText = s.toString();
                lvFriends.setFilterText(searchText);
            }
        });
    }

    private void refresh(){
        List<FriendEntity> friends = new ArrayList<>();

        try {
            friends = friendDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        friendChoiceAdapter.setItems(friends);
    }

    private void refresh(String searchText){
        List<FriendEntity> friends = new ArrayList<>();

        try {
            QueryBuilder<FriendEntity, Long> queryBuilder = friendDao.queryBuilder();
            queryBuilder
                    .where()
                    .like("name", "%" + searchText + "%")
                    .or()
                    .like("contactName", "%" + searchText + "%");

            PreparedQuery<FriendEntity> preparedQuery = queryBuilder.prepare();

            friends = friendDao.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        friendChoiceAdapter.setItems(friends);
    }

}
