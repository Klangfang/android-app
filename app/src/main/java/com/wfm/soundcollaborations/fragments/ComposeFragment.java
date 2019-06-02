package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.exoplayer2.ExoPlayer;
import com.wfm.soundcollaborations.Classes.CompositionOverview;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionAdapter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The {@link ComposeFragment} ...
 *  - requests data for public compositions
 *  - displays a list of {@link CompositionOverview} objects using {@link CompositionAdapter}
 *  - provides the possibility to create new public compositions
 **/
public class ComposeFragment extends Fragment {
    private View root; // Needed when adding a Fragment, is returned below

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Assign the layout file to the fragment
        root = inflater.inflate(
                R.layout.fragment_compose,
                container,
                false
        );

        // Initialize the toolbar
        initToolbar();

        // New empty ArrayList for adding data to CompositionOverview
        ArrayList<CompositionOverview> compositions = new ArrayList<>();

        // Add Dummy Content to ArrayList above. TODO: Replace with JSON data
        compositions.add(new CompositionOverview(
                "title1",
                1,
                "https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg"));

        compositions.add(new CompositionOverview(
                "title2",
                2,
                "https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg"));

        compositions.add(new CompositionOverview(
                "title3",
                3,
                "https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg"));

        compositions.add(new CompositionOverview(
                "title4",
                2,
                "https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg"));

        compositions.add(new CompositionOverview(
                "title5",
                1,
                "https://stereoninjamusic.weebly.com/uploads/4/5/7/5/45756923/the_midnight_ninja.ogg"));

        // Create new instance of CompositionAdapter
        CompositionAdapter compositionAdapter = new CompositionAdapter(
                Objects.requireNonNull(getActivity()),
                compositions);

        // Create new ListView
        // TODO Use recycler view here instead of list view
        ListView listView = root.findViewById(R.id.public_compositions_list);

        // Provide the adapter for the listView
        listView.setAdapter(compositionAdapter);

        return root; // Needed when adding a Fragment (See top of Fragment)
    }

    /**
     * Initialize {@link Toolbar} and set custom title and background color.
     **/
    private void initToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        Toolbar toolbar = mainActivity.getToolbar();
        toolbar.setTitle(R.string.bnm_compose);
        toolbar.setBackgroundColor(getResources().getColor(R.color.navigation));
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
    }
}
