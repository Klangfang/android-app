package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wfm.soundcollaborations.Classes.CompositionOverview;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionAdapter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The Compose Fragment provides public compositions that the user can join and an option
 * to create a new composition.
 *
 * The class is connected with {@link CompositionOverview} and {@link CompositionAdapter}:
 * It loads and displays the composition Overview objects.
 **/
public class ComposeFragment extends Fragment {
    private View root; // Needed when adding a Fragment, is returned below

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        root = inflater.inflate(
                R.layout.fragment_compose,
                container,
                false);

        initToolbar();

        //New array with test data for composition views
        ArrayList<CompositionOverview> compositions = new ArrayList<>();

        // Add Dummy Content. TODO: Replace with real content
        compositions.add(new CompositionOverview(
                "title1",
                "1/4 Mitglieder"));

        compositions.add(new CompositionOverview(
                "title2",
                "1/4 Mitglieder"));

        compositions.add(new CompositionOverview(
                "title3",
                "1/4 Mitglieder"));

        compositions.add(new CompositionOverview(
                "title4",
                "1/4 Mitglieder"));

        compositions.add(new CompositionOverview(
                "title5",
                "1/4 Mitglieder"));

        CompositionAdapter compositionAdapter = new CompositionAdapter(
                Objects.requireNonNull(getActivity()),
                compositions);

        // TODO Use recycler view here instead of list view
        ListView listView = root.findViewById(R.id.public_compositions_list);

        listView.setAdapter(compositionAdapter);

        return root; // Needed when adding a Fragment
    }

    /**
     * This method initializes the top app bar and sets a custom
     * title and background color.
     * */
    private void initToolbar() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;

        Toolbar toolbar = mainActivity.getToolbar();
        toolbar.setTitle(R.string.bnm_compose);
        toolbar.setBackgroundColor(getResources().getColor(R.color.navigation));
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
    }
}
