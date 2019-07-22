package com.wfm.soundcollaborations.activities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.FriendlistAdapter;
import com.wfm.soundcollaborations.adapter.ViewPagerAdapter;
import com.wfm.soundcollaborations.database.FriendEntity;
import com.wfm.soundcollaborations.fragments.FriendFragment;

import java.util.Random;

/**
 * Created by Markus Eberts on 21.10.16.
 */
public class FriendlistActivity extends MainActivity {
    private final static String TAG = FriendlistActivity.class.getSimpleName();

    private static final int PICK_CONTACT = 0;

    private TabLayout tlFeed;
    private ViewPager vpFeed;
    private ViewPagerAdapter vpAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.friendlist, (ViewGroup) findViewById(R.id.fl_content));

        tlFeed = (TabLayout) findViewById(R.id.tl_friendlist);
        vpFeed = (ViewPager) findViewById(R.id.vp_friendlist);

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FriendlistFragment.class, FriendRequestsFragment.class);
        vpFeed.setAdapter(vpAdapter);

        final TabLayout.Tab friendsTab = tlFeed.newTab();
        final TabLayout.Tab requestsTab = tlFeed.newTab();

        tlFeed.addTab(requestsTab);
        tlFeed.addTab(friendsTab);
        tlFeed.setupWithViewPager(vpFeed);

        friendsTab.setText("Partner");
        requestsTab.setText("Anfragen");
    }

    public static class FriendlistFragment extends Fragment {

        private FriendlistActivity activity;

        private ListView lvFriends;
        private FriendlistAdapter friendlistAdapter;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.friendlist_friends, container, false);

            activity = ((FriendlistActivity) getActivity());

            lvFriends = (ListView) v.findViewById(R.id.lv_items);
            friendlistAdapter = new FriendlistAdapter(getActivity(), R.layout.friendlist_friend_row);
            lvFriends.setAdapter(friendlistAdapter);

            v.findViewById(R.id.btn_add_friend).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, ContactsActivity.class);
                    startActivityForResult(intent, PICK_CONTACT);
                }
            });

            lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FriendEntity selectedFriend = friendlistAdapter.getItem(position);

                    FriendFragment friendFragment = new FriendFragment();

                    Bundle args = new Bundle();
                    args.putLong("friendId", selectedFriend.getId());
                    friendFragment.setArguments(args);

                    friendFragment.show(getFragmentManager(), "dialog");
                }
            });
            return v;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == PICK_CONTACT) {
                if (resultCode == RESULT_OK) {
                    String[] friendNames = new String[]{"The musician", "Beatbox", "Toran"};
                    Random random = new Random();

                    FriendEntity friend = new FriendEntity(data.getLongExtra("contactId", -1),
                            friendNames[random.nextInt(friendNames.length)],
                            data.getStringExtra("contactName"));


                }
            }
        }
    }


    public static class FriendRequestsFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.friendlist_requests, container, false);
            return v;
        }
    }
}
