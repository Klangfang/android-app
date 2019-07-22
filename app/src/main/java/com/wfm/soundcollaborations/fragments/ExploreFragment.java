package com.wfm.soundcollaborations.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.MainActivity;

/**
 * Created by Markus Eberts on 09.11.16.
 * Edited by Mohammed Abuiriban
 */
public class ExploreFragment extends Fragment
{
    private static final String TAG = ExploreFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_explore, container, false);
        initToolbar();
        return  root;
    }

    private void initToolbar()
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        Toolbar toolbar = mainActivity.getToolbar();
        if(toolbar != null)
        {
            toolbar.setTitle(R.string.app_name);
            toolbar.setBackgroundColor(getResources().getColor(R.color.navigation));
            toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_title));
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.explore_activity_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}
