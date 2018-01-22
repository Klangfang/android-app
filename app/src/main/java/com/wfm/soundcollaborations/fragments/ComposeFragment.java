package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;

/**
 * Created by Markus Eberts on 09.11.16.
 */
public class ComposeFragment extends Fragment {

    private TabLayout tlComposition;
    private ViewPager vpComposition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compose, container, false);
        initToolbar();
        return  root;
    }

    private void initToolbar()
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.getToolbar();
        if(toolbar != null)
        {
            toolbar.setBackgroundColor(getResources().getColor(R.color.navigation));
            toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.composition, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


}
