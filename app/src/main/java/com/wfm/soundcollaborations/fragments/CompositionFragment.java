package com.wfm.soundcollaborations.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.activities.SongCreationActivity;
import com.wfm.soundcollaborations.adapter.ViewPagerAdapter;
import com.wfm.soundcollaborations.database.GroupSongEntity;
import com.wfm.soundcollaborations.views.PlayerSmallView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Eberts on 09.11.16.
 */
public class CompositionFragment extends BaseFragment {

    private PlayerSmallView player;
    private TabLayout tlComposition;
    private ViewPager vpComposition;
    private ViewPagerAdapter vpCompositionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.composition, container, false);

        player = (PlayerSmallView) v.findViewById(R.id.player);

        // Toolbar
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);

        ActionBar supportActionBar = ((AppCompatActivity)(getActivity())).getSupportActionBar();

        supportActionBar.setHomeButtonEnabled(false);
        supportActionBar.setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);

        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();

        tlComposition = (TabLayout) getActivity().findViewById(R.id.tl_composition);
        vpComposition = (ViewPager) getActivity().findViewById(R.id.vp_composition);

        vpCompositionAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(),
                PublicFragment.class,
                PrivateFragment.class);
        vpComposition.setAdapter(vpCompositionAdapter);

        final TabLayout.Tab soundTab = tlComposition.newTab();
        final TabLayout.Tab trackTab = tlComposition.newTab();

        tlComposition.addTab(trackTab);
        tlComposition.addTab(soundTab);
        tlComposition.setupWithViewPager(vpComposition);

        soundTab.setText("Öffentlich");
        trackTab.setText("Privat");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.composition, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public static class PublicFragment extends BaseFragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.composition_public, container, false);
            return v;
        }

        @Override
        public void onStart(){
            super.onStart();
        }
    }

    public static class PrivateFragment extends BaseFragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.composition_private, container, false);
            return v;
        }

        @Override
        public void onStart(){
            super.onStart();
        }
    }
}
