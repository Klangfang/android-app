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
import com.wfm.soundcollaborations.adapter.ViewPagerAdapter;
import com.wfm.soundcollaborations.views.PlayerSimpleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markus Eberts on 09.11.16.
 */
public class ProfileFragment extends Fragment
{
    @BindView(R.id.tl_sounds)
    TabLayout soundsTabLayout;
    @BindView(R.id.vp_sounds)
    ViewPager soundsViewPager;
    @BindView(R.id.sound_player)
    PlayerSimpleView soundPlayer;

    private ViewPagerAdapter vpProfileAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, root);
        initToolbar();
        initViewPager();
        return  root;
    }

    private void initToolbar()
    {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        Toolbar toolbar = mainActivity.getToolbar();
        if(toolbar != null)
        {
            toolbar.setTitle(R.string.bnm_record);
            toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_profile_title));
            toolbar.setBackgroundColor(getResources().getColor(R.color.color_accent));
            setHasOptionsMenu(true);
        }
    }

    private void initViewPager()
    {
        vpProfileAdapter = new ViewPagerAdapter(getFragmentManager(),
                MySoundsFragment.class,
                TrackFragment.class);
        soundsViewPager.setAdapter(vpProfileAdapter);

        final TabLayout.Tab mySoundsTab = soundsTabLayout.newTab();
        final TabLayout.Tab finishedCollagesTab = soundsTabLayout.newTab();

        soundsTabLayout.addTab(finishedCollagesTab);
        soundsTabLayout.addTab(mySoundsTab);

        soundsTabLayout.setupWithViewPager(soundsViewPager);

        mySoundsTab.setText(getString(R.string.my_sounds));
        finishedCollagesTab.setText(getString(R.string.finished_collages));
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collect_activity_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
}
