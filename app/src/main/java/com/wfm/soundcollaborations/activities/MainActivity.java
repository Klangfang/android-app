package com.wfm.soundcollaborations.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wfm.soundcollaborations.R;
import com.wfm.soundcollaborations.fragments.ComposeFragment;
import com.wfm.soundcollaborations.fragments.ExploreFragment;
import com.wfm.soundcollaborations.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Markus Eberts on 15.10.16.
 */
public class MainActivity extends AppCompatActivity
{
    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar_main)
    Toolbar main_toolbar;
    @BindView(R.id.bnv_bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initBottomNavigationView();
        showDefaultFragment();
    }

    private void initToolbar()
    {
        setSupportActionBar(main_toolbar);
    }

    private void initBottomNavigationView()
    {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        switch (item.getItemId())
                        {
                            case R.id.bnm_explore:
                                startExploreFragment();
                                break;
                            case R.id.bnm_compose:
                                startComposeFragment();
                                break;
                            case R.id.bnm_record:
                                startProfileFragment();
                                break;
                        }
                        return true;
                    }
                });
    }

    private void startExploreFragment(){
        ExploreFragment exploreFragment = new ExploreFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, exploreFragment);
        transaction.commit();
    }

    private void startComposeFragment(){
        ComposeFragment composeFragment = new ComposeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, composeFragment);
        transaction.commit();
    }

    private void startProfileFragment(){
        ProfileFragment profileFragment = new ProfileFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_content, profileFragment);
        transaction.commit();
    }

    private void showDefaultFragment()
    {
        startExploreFragment();
    }

    public Toolbar getToolbar()
    {
        return main_toolbar;
    }
}
