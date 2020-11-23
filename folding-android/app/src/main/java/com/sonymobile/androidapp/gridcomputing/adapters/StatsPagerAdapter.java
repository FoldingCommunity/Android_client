package com.sonymobile.androidapp.gridcomputing.adapters;



import com.sonymobile.androidapp.gridcomputing.log.Log;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.sonymobile.androidapp.gridcomputing.fragments.StatsPageFragment;

import java.util.ArrayList;
import java.util.List;


public class StatsPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * Stats lists
     */
    private List<String> names;

    private List<long []> stats ;



    public StatsPagerAdapter(final FragmentManager fm) {
        super(fm);
        names = new ArrayList<>();
        stats = new ArrayList<>();

    }

    /**
     * Updates the stats
     *
     */
    public void setStats(final List<String> namesList, final List<long []> statsList) {
        names = namesList;
        stats = statsList;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(final int position) {

        return StatsPageFragment.newInstance(names.get(position), stats.get(position));
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public int getItemPosition(final Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
