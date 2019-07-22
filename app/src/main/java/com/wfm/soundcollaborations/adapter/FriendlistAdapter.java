package com.wfm.soundcollaborations.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.database.FriendEntity;

import java.util.List;

/**
 * Created by Markus Eberts on 22.10.16.
 */
public class FriendlistAdapter extends ArrayAdapter<FriendEntity> {

    public FriendlistAdapter(Context context, int resource) {
        super(context, resource);
    }

    public FriendlistAdapter(Context context, int resource, List<FriendEntity> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.friendlist_friend_row, null);
        }

        final FriendEntity friend = getItem(position);

        if (friend != null) {
            ((TextView) v.findViewById(R.id.tv_name)).setText(friend.getName());

            if (friend.getContactName() != null) {
                ((TextView) v.findViewById(R.id.tv_contact_name)).setText(friend.getContactName());
            } else {
                v.findViewById(R.id.tv_separator).setVisibility(View.GONE);
                v.findViewById(R.id.tv_contact_name).setVisibility(View.GONE);
            }
        }

        return v;
    }

}
