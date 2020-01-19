package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wfm.soundcollaborations.KlangfangApp;
import com.wfm.soundcollaborations.MainActivity;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.adapter.CompositionOverviewAdapter;
import com.wfm.soundcollaborations.compose.ComposeComponent;
import com.wfm.soundcollaborations.compose.model.ComposeViewModel;
import com.wfm.soundcollaborations.webservice.dtos.CompositionResponse;

import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;

/**
 * The {@link ComposeFragment} ...
 *  - requests data for public compositions
 *  - displays a list of {@link com.wfm.soundcollaborations.webservice.dtos.CompositionOverviewResp} objects using {@link CompositionOverviewAdapter}
 *  - provides the possibility to create new public compositions
 **/
public class ComposeFragment extends Fragment {

    private View root; // Needed when adding a Fragment, is returned below

    private final Consumer<CompositionResponse> startEditorCallback;
    private final Consumer<View> startCreateCompositionCallback;

    public static final String PICK_RESPONSE = "PICK";

    private CompositionOverviewAdapter adapter;

    private ComposeComponent composeComponent;

    @Inject
    ComposeViewModel composeViewModel;


    public ComposeFragment(Consumer<CompositionResponse> startEditorCallback,
                           Consumer<View> startCreateCompositionCallback) {

        this.startEditorCallback = startEditorCallback;
        this.startCreateCompositionCallback = startCreateCompositionCallback;

    }


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
        createCompositionButton.setOnClickListener(startCreateCompositionCallback::accept);

        composeComponent = ((KlangfangApp) getActivity().getApplicationContext())
                .appComponent
                .composeComponent()
                .create();
        composeComponent.inject(this);

        initFragmentActivity();

        return root; // Needed when adding a Fragment (See top of Fragment)

    }


    private void initFragmentActivity() {

            if (isAdded()) {

                FragmentActivity fragmentActivity = Objects.requireNonNull(getActivity());
                adapter = new CompositionOverviewAdapter(fragmentActivity,
                        composeViewModel,
                        startEditorCallback);

                RecyclerView recyclerView = root.findViewById(R.id.public_compositions_list);
                recyclerView.setAdapter(adapter);
                // Set horizontal scrolling
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));

                // Enable Snapping when scrolling compositions horizontally
                final SnapHelper snapHelper = new PagerSnapHelper();
                snapHelper.attachToRecyclerView(recyclerView);

            }
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
