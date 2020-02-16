package com.wfm.soundcollaborations.interaction.main.fragments;

import android.content.Context;
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
import com.wfm.soundcollaborations.CompositionOverviewViewModel;
import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.interaction.main.MainActivity;
import com.wfm.soundcollaborations.interaction.main.adapter.CompositionOverviewAdapter;
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

    private Consumer<CompositionResponse> startEditorCallback;
    private Consumer<View> startCreateCompositionCallback;

    public static final String PICK_RESPONSE = "PICK";

    private CompositionOverviewAdapter adapter;

    @Inject
    CompositionOverviewViewModel compositionOverviewViewModel;


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

        initFragmentActivity();

        return root; // Needed when adding a Fragment (See top of Fragment)

    }

    public void onAttach(Context context) {

        super.onAttach(context);
        ((MainActivity) context).mainComponent.inject(this);

    }

    public void setCallbacks(Consumer<CompositionResponse> startEditorCallback,
                             Consumer<View> startCreateCompositionCallback) {

        this.startEditorCallback = startEditorCallback;
        this.startCreateCompositionCallback = startCreateCompositionCallback;

    }

    private void initFragmentActivity() {

            if (isAdded()) {

                FragmentActivity fragmentActivity = Objects.requireNonNull(getActivity());
                adapter = new CompositionOverviewAdapter(fragmentActivity,
                        compositionOverviewViewModel,
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
