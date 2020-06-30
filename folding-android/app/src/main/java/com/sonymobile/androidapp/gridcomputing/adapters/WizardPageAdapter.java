/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.adapters;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sonymobile.androidapp.gridcomputing.fragments.WizardMainFragment;
import com.sonymobile.androidapp.gridcomputing.fragments.WizardPageFragment;

/**
 * Adapter for the wizard pages.
 */
public class WizardPageAdapter extends FragmentStatePagerAdapter {

    /**
     * Constructor for the WizardPageAdapter.
     *
     * @param fragmentManager A FragmentManager used to build this adapter.
     */
    public WizardPageAdapter(final FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public final Fragment getItem(final int position) {
        final Fragment frag = new WizardPageFragment();
        final Bundle args = new Bundle();
        args.putInt(WizardPageFragment.PAGE_POSITION, position);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public final int getCount() {
        return WizardMainFragment.NUM_PAGES;
    }

}
