/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.fragments.ReportChartFragment;

public class ReportsActivity extends AppCompatActivity {

    private ViewPager mPager;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.contribution_details);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPager = (ViewPager) findViewById(R.id.reports_viewpager);
        mPager.setAdapter(new ContactsTabPageAdapter(getSupportFragmentManager()));

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.reports_tablayout);
        tabLayout.setupWithViewPager(mPager);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                // ignore
                break;
        }
        return true;
    }

    static class ContactsTabPageAdapter extends FragmentStatePagerAdapter {

        public ContactsTabPageAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            return ReportChartFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return ReportChartFragment.DataType.values().length;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return ReportChartFragment.DataType.getTitle(position);
        }
    }
}
