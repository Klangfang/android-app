package com.wfm.soundcollaborations.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wfm.soundcollaborations.Editor.activities.CreateCompositionActivity;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.OverviewResponse;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionOverviewAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * The {@link ComposeFragment} ...
 *  - requests data for public compositions
 *  - displays a list of {@link CompositionOverview} objects using {@link CompositionOverviewAdapter}
 *  - provides the possibility to create new public compositions
 **/
public class ComposeFragment extends Fragment {

    private View root; // Needed when adding a Fragment, is returned below


    private CompositionServiceClient client;
    public static final String PICK_RESPONSE = "PICK";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_compose, container, false);
        // Assign the layout file to the fragment
        root = inflater.inflate(
                R.layout.fragment_compose,
                container,
                false
        );

        // Initialize the toolbar
        initToolbar();

        FloatingActionButton createCompositionButton = root.findViewById(R.id.new_composition_button);
        createCompositionButton.setOnClickListener(view -> startCreateCompositionActivity(view));


        client = new CompositionServiceClient(root.getContext());
        Response.Listener<String> listener = response -> fillActivity(response);
        client.getOverviews(listener);


        //New array with test data for composition views
       // ArrayList<Composition> compositions = new ArrayList<>();
        //compositions.add(new Composition("Uni Sounds", "Hamburg, München", "1/4 Mitglieder"));
        //compositions.add(new Composition("Quiet Fire", "Hamburg, München", "1/4 Mitglieder"));
        //compositions.add(new Composition("Baobab", "Hamburg, München", "1/4 Mitglieder"));
        //compositions.add(new Composition("Nom Nom Sounds", "Hamburg, München", "1/4 Mitglieder"));
        //compositions.add(new Composition("Blablabla", "Hamburg, München", "1/4 Mitglieder"));
        // New empty ArrayList for adding data to CompositionOverview
        //ArrayList<CompositionOverview> compositions = new ArrayList<>();

        // Add Dummy Content to ArrayList above. TODO: Replace with JSON data
        /*compositions.add(new CompositionOverview(
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
                */

        // Create new instance of CompositionAdapter

        return root; // Needed when adding a Fragment (See top of Fragment)
    }


    private void fillActivity(String response) {

        if (StringUtils.isNotBlank(response)) {

            OverviewResponse overviewResponse = JsonUtil.fromJson(response, OverviewResponse.class);
            if (overviewResponse != null) {
                List<CompositionOverview> compositions = overviewResponse.overviews;

                if (isAdded()) {

                    FragmentActivity fragmentActivity = Objects.requireNonNull(getActivity());
                    CompositionOverviewAdapter adapter = new CompositionOverviewAdapter(fragmentActivity,
                            compositions);
                    // TODO Use recycler view here instead of list view
                    ListView listView = root.findViewById(R.id.public_compositions_list);
                    // Provide the adapter for the listView
                    listView.setAdapter(adapter);

                }
            }

        }

    }


    private void startCreateCompositionActivity(View view) {
        Intent intent = new Intent(view.getContext(), CreateCompositionActivity.class);
        view.getContext().startActivity(intent);
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
