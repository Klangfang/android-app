package com.wfm.soundcollaborations.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Markus Eberts on 11.10.16.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    Class[] tabs;

    public ViewPagerAdapter(FragmentManager fm, Class... tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        try {
            fragment = (Fragment) tabs[position].newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

}
