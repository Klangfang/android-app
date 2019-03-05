package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;

public class ComposeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_compose, container, false);
        initToolbar();
        initRecyclerView();
        return root;
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

    /**
     * This method initializes the recycler view that holds all public compositions
     * */
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
    }
}
