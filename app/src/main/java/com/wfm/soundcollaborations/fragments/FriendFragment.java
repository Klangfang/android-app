package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.FriendEntity;

import java.sql.SQLException;

/**
 * Created by Markus Eberts on 23.10.16.
 */
public class FriendFragment extends BaseDialogFragment {

    private TextView tvName;
    private TextView tvContactName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.friendlist_friend_info, container, false);

        tvName = (TextView) v.findViewById(R.id.tv_name);
        tvContactName = (TextView) v.findViewById(R.id.tv_contact_name);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        long friendId = getArguments().getLong("friendId");

        FriendEntity friend;
        try {
            friend = friendDao.queryForId(friendId);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        tvName.setText(friend.getName());
        tvContactName.setText(friend.getContactName());
    }
}
