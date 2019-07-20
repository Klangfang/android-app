package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.wfm.soundcollaborations.Editor.model.composition.CompositionOverview;
import com.wfm.soundcollaborations.webservice.CompositionServiceClient;
import com.wfm.soundcollaborations.webservice.JsonUtil;
import com.wfm.soundcollaborations.webservice.OverviewResponse;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;
import com.wfm.soundcollaborations.adapter.CompositionOverviewAdapter;

import java.util.List;
import java.util.Objects;

public class ComposeFragment extends Fragment {

    //private RecyclerView recyclerView;
    //private RecyclerView.Adapter mAdapter;
    //private RecyclerView.LayoutManager layoutManager;
    private View root;


    private CompositionServiceClient client;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_compose, container, false);
        initToolbar();
        //initRecyclerView();


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



        return root;
    }


    private void fillActivity(String response) {

        OverviewResponse overviewResponse = JsonUtil.fromJson(response, OverviewResponse.class);
        if (overviewResponse != null) {
            List<CompositionOverview> compositions = overviewResponse.overviews;

            CompositionOverviewAdapter compositionOverviewAdapter = new CompositionOverviewAdapter(Objects.requireNonNull(getActivity()),
                    compositions);
            ListView listView = root.findViewById(R.id.public_compositions_list);
            listView.setAdapter(compositionOverviewAdapter);
        }

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





    /* TODO Use recycler view here instead of list view
     * This method initializes the recycler view that holds all public compositions
     * */
    /*
    private void initRecyclerView() {
        recyclerView = root.findViewById(R.id.open_compositions_recycler_view);

        // This improves performance of the recycler view, if the layout size is fixed
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        // TODO create adapter.java file to manage the views that will be loaded into the recycler view
        //mAdapter = new MyAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);
    }*/

}
