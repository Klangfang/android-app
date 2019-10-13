package com.wfm.soundcollaborations.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wfm.soundcollaborations.Editor.activities.CreateCompositionActivity;
import com.wfm.soundcollaborations.Editor.model.composition.Composition;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.OverviewResponse;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionOverviewAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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

        // New empty ArrayList for adding data to CompositionOverview
        //ArrayList<CompositionOverview> compositions = new ArrayList<>();

        // Add Dummy Content to ArrayList above.
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


        return root; // Needed when adding a Fragment (See top of Fragment)
    }


    private void fillActivity(String response) {

        if (StringUtils.isNotBlank(response)) {

            //OverviewResponse overviewResponse = JsonUtil.fromJson(response, OverviewResponse.class);
            // Test Composition Data
            OverviewResponse overviewResponse = new OverviewResponse();

            CompositionOverview c1 = new CompositionOverview();
            c1.numberOfMembers = 2;
            c1.pickUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c1.snippetUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c1.title = "Titel 1";

            CompositionOverview c2 = new CompositionOverview();
            c2.title = "Titel 2";
            c2.pickUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c2.snippetUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c2.numberOfMembers = 1;

            CompositionOverview c3 = new CompositionOverview();
            c3.title = "Titel 3";
            c3.pickUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c3.snippetUrl = "https://freesound.org/people/Soughtaftersounds/sounds/145426/";
            c3.numberOfMembers = 3;

            overviewResponse.overviews = Arrays.asList(c1, c2, c3);
            if (overviewResponse != null) {
                List<CompositionOverview> compositions = overviewResponse.overviews;

                if (isAdded()) {

                    FragmentActivity fragmentActivity = Objects.requireNonNull(getActivity());
                    CompositionOverviewAdapter adapter = new CompositionOverviewAdapter(fragmentActivity,
                            compositions);
                    RecyclerView recyclerView = root.findViewById(R.id.public_compositions_list);
                    // Provide the adapter for the recyclerView
                    recyclerView.setAdapter(adapter);
                    // Set LayoutManager to define the List Layout of RecyclerView
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.HORIZONTAL, false));

                    // Enable Snapping when scrolling compositions horizontally
                    final SnapHelper snapHelper = new PagerSnapHelper();
                    snapHelper.attachToRecyclerView(recyclerView);

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
        toolbar.setBackgroundColor(getResources().getColor(R.color.bars));
        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
    }
}
