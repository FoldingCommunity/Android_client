/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.activities.ui.login.LoginActivity;
import com.sonymobile.androidapp.gridcomputing.adapters.ConditionsSlidePagerAdapter;
import com.sonymobile.androidapp.gridcomputing.conditions.ConditionType;
import com.sonymobile.androidapp.gridcomputing.conditions.ConditionsHandler;
import com.sonymobile.androidapp.gridcomputing.database.JobCheckpointsContract;
import com.sonymobile.androidapp.gridcomputing.gamification.Scores;
import com.sonymobile.androidapp.gridcomputing.log.Log;
import com.sonymobile.androidapp.gridcomputing.messages.ConditionMessage;
import com.sonymobile.androidapp.gridcomputing.messages.JobExecutionMessage;
import com.sonymobile.androidapp.gridcomputing.notifications.NotificationHelper;
import com.sonymobile.androidapp.gridcomputing.notifications.NotificationStatus;
import com.sonymobile.androidapp.gridcomputing.preferences.MiscPref;
import com.sonymobile.androidapp.gridcomputing.preferences.PrefUtils;
import com.sonymobile.androidapp.gridcomputing.preferences.RunningPref;
import com.sonymobile.androidapp.gridcomputing.preferences.SettingsPref;
import com.sonymobile.androidapp.gridcomputing.service.ServiceManager;
import com.sonymobile.androidapp.gridcomputing.utils.AlarmUtils;
import com.sonymobile.androidapp.gridcomputing.utils.ApplicationData;
import com.sonymobile.androidapp.gridcomputing.utils.FormatUtils;
import com.sonymobile.androidapp.gridcomputing.utils.ViewUtils;
import com.sonymobile.androidapp.gridcomputing.views.CheckableImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary activity that presents the number of jobs contributed and the total
 * contributed time.
 */
public class SummaryActivity extends GameLoginActivity implements
        OnClickListener {

    /**
     * Legal filename.
     */
    private static final String LEGAL_FILENAME = "Legal.txt";

    /**
     * Alpha value used to dim the background image when the conditions viewpager is visible.
     */
    private static final float BACKGROUND_IMAGE_ALPHA = .1F;

    /**
     * Status bar view.
     */
    private View mStatusView;

    /**
     * Main switch used to turn on/off the contribution.
     */
    private CheckableImageButton mMenuSwitch;

    /**
     * Text View of description.
     */
    private TextView mTvDescription;

    /**
     * Text View of view more.
     */
    private TextView mTvViewMore;

    /**
     * Text View of title research type.
     */
    private TextView mTvResearchType;

    /**
     * View of description and view more.
     */
    private View mDetailsLayout;

    /**
     * Background image.
     */
    private ImageView mBackgroundImage;

    /**
     * The parent view onf the conditions layout.
     */
    private View mConditionsLayout;

    /**
     * The viewpager used to render the conditions.
     */
    private ViewPager mConditionsViewPager;

    /**
     * The viewgroup that contains all the indicators.
     */
    private ViewGroup mConditionsIndicator;

    /**
     * The conditions page adapter.
     */
    private ConditionsSlidePagerAdapter mAdapter;
    /**
     * Network animation
     */
    private LottieAnimationView anim;

    private View coverview;

    private Button logout;



    @Override
    protected final void onCreate(final Bundle savedState) {
        super.onCreate(savedState);
        Log.d("Activity > SummaryActivity onCreate");
        overridePendingTransition(0, 0);
        Bundle bundle = getIntent().getExtras();
        boolean check = false;
        if(bundle != null){
            check = bundle.getBoolean("CHECK");
        }

        setContentView(R.layout.activity_summary);
        mStatusView = findViewById(R.id.status_bar);
        mConditionsLayout = findViewById(R.id.summary_conditions_layout);
        mConditionsViewPager = (ViewPager) findViewById(R.id.summary_view_pager);
        mConditionsIndicator = (ViewGroup) findViewById(R.id.summary_page_indicator_container);
        coverview = findViewById(R.id.coverview);
        coverview.setVisibility(View.GONE);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);


        /*mBackgroundImage = findViewById(R.id.AnimView);
        Glide.with(this).load(R.drawable.animationnetgif).into(mBackgroundImage);*/

        anim = findViewById(R.id.animation);

        mConditionsLayout.setVisibility(View.GONE);

        findViewById(R.id.summary_research_type_layout).setOnClickListener(this);
        findViewById(R.id.summary_contributed_time_layout).setOnClickListener(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        mMenuSwitch = (CheckableImageButton) findViewById(R.id.summary_menu_power_toggle);
        mMenuSwitch.setOnClickListener(this);

        //final boolean pause = PrefUtils.getBooleanValue("account_pref", "CHECK", false);
        //Log.d("CHECK: " + String.valueOf(pause));
        boolean ischekc = mMenuSwitch.isChecked();
        Log.d("ISCHECK:" + String.valueOf(ischekc));
        if(!ischekc && check){
            mMenuSwitch.performClick();
        }

        //  toggleShareButtons();

        turnOff();
        AlarmUtils.createAlarm(AlarmUtils.AlarmType.REPEAT_1_MIN);

        if (getIntent().getBooleanExtra("login_ggs", false)) {
            doLogin();
        }

        mTvResearchType = (TextView) findViewById(R.id.title_research_type);

        LinearLayout linearLayout = (LinearLayout) findViewById(
                R.id.summary_research_type_internal_layout);
        mDetailsLayout = View.inflate(this, R.layout.research_details, linearLayout);
        mTvDescription = (TextView) mDetailsLayout.findViewById(R.id.description);
        mTvViewMore = (TextView) mDetailsLayout.findViewById(R.id.view_more);
        mTvViewMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                showResearchDescription();
            }
        });


        //setup tab bar
        BottomNavigationView navView = findViewById(R.id.nav_view2);
       // navView.setSelectedItemId(R.id.navigation_dashboard);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        //PrefUtils.setBooleanValue("account_pref", "CHECK", true);
                        startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        //PrefUtils.setBooleanValue("account_pref", "CHECK", true);
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
        navView.setSelectedItemId(R.id.navigation_dashboard);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationData.getBus().registerSticky(this);
        ConditionsHandler.getInstance().notifyConditionChanged(true);
        loadJobsStats(
                RunningPref.getResearchType(), RunningPref.getAccumulatedTime());
    }

    @Override
    protected void onPause() {
        ApplicationData.getBus().unregister(this);
        super.onPause();
    }


    /**
     * Load and set the contributed time and contributed number of jobs.
     *
     * //@param numberOfUsers   the number of users.
     * @param title           the title.
     * @param accumulatedTime the accumulated time.
     */
    public void loadJobsStats( final String title,
                              final long accumulatedTime) {
        Log.d("LoadStats");
        if (!TextUtils.isEmpty(RunningPref.getResearchType())) {
            findViewById(R.id.summary_first_line).setVisibility(View.VISIBLE);
            findViewById(R.id.summary_second_line).setVisibility(View.VISIBLE);
            findViewById(R.id.summary_research_type_layout).setVisibility(View.VISIBLE);
            //findViewById(R.id.summary_people_helping_out_layout.setVisibility(View.VISIBLE);

            //((TextView) findViewById(R.id.summary_num_people_helping)).setText(String.valueOf(numberOfUsers));
            ((TextView) findViewById(R.id.summary_research_type)).setText(title);
        } else {
            //findViewById(R.id.summary_first_line).setVisibility(View.GONE);
            //findViewById(R.id.summary_second_line).setVisibility(View.GONE);
            //findViewById(R.id.summary_research_type_layout).setVisibility(View.GONE);
            //findViewById(R.id.summary_people_helping_out_layout).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.summary_donated_time_tv))
                .setText(FormatUtils.getMainTimeString(accumulatedTime));
        Log.d("LoadedStats");
    }

    @Override
    public final void onClick(final View view) {
        if (view.getId() == R.id.summary_menu_power_toggle) {
            if (isEnabled()) {
                if (SettingsPref.isPaused()) {
                    ServiceManager.resume();
                } else {
                    ServiceManager.pause();
                }
            } else {
                MiscPref.setLastBatteryPlateauTime(0);
                ServiceManager.resume();
            }
        } else if (view.getId() == R.id.summary_research_type_layout) {
            showResearchDescription();
            //uncomment the lines below to add expand/collapse behavior
//            if (mTvDescription.getVisibility() == View.GONE) {
//                mTvDescription.setText(RunningPref.getResearchDescription());
//                mTvDescription.setVisibility(View.VISIBLE);
//                mTvViewMore.setVisibility(View.VISIBLE);
//
//                mTvResearchType.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        null, null,
//                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_up),
//                        null);
//            } else {
//                mTvDescription.setVisibility(View.GONE);
//                mTvViewMore.setVisibility(View.GONE);
//
//                mTvResearchType.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                        null, null, ContextCompat.getDrawable(getApplicationContext(),
//                                R.drawable.ic_arrow_down), null);
//            }
        } else if (view.getId() == R.id.summary_contributed_time_layout) {
            final Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        } else if(view.getId() == R.id.logout) {
            PrefUtils.setBooleanValue("account_pref", "STATUS", false);
            final Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Shows a dialog with the research details.
     */
    private void showResearchDescription() {
        Log.d("Showresearch");
        final String url = RunningPref.getResearchUrl();
        if (!TextUtils.isEmpty(url)) {
            String id = RunningPref.getResearchId();
            Scores.unlockAchievementResearchDescription(id);
            //DialogFragment newFragment = WebviewDialogFragment.newInstance(url);
            //newFragment.show(getFragmentManager(), WebviewDialogFragment.DIALOG_TAG);
            final Intent intent = new Intent(this, ProjectDetailsActivity.class);
            startActivity(intent);
        }
    }



    /**
     * Turns off the execution.
     */
    private void turnOff() {
        ViewUtils.updateStatusBar(mStatusView, false, false, false, false, false);
        anim.pauseAnimation();
        coverview.setVisibility(View.GONE);
    }

    /**
     * Verifies that folding is enabled.
     *
     * @return true if folding is enabled.
     */
    protected final boolean isEnabled() {
        return SettingsPref.isExecutionEnabled();
    }


    @SuppressWarnings("unused")
    public void onEventMainThread(final ConditionMessage message) {
        final boolean enabled = !message.getNotMetConditions().contains(ConditionType.ENABLED);
        final boolean charger = !message.getNotMetConditions().contains(ConditionType.CHARGER);
        final boolean battery = !message.getNotMetConditions().contains(ConditionType.BATTERY);
        final boolean wifi = !message.getNotMetConditions().contains(ConditionType.WIFI);
        final boolean paused = message.getNotMetConditions().contains(ConditionType.PAUSED);
        ViewUtils.updateStatusBar(mStatusView, enabled, paused, battery, charger, wifi);
        if(enabled && !paused){
            if(battery && charger && wifi && !paused){
                anim.playAnimation();
                coverview.setVisibility(View.GONE);
            } else{
                anim.pauseAnimation();
                coverview.setVisibility(View.VISIBLE);
            }
        }else{
            anim.pauseAnimation();
            coverview.setVisibility(View.GONE);
        }

        if (!enabled && JobCheckpointsContract.get24HourAccumulatedTime() > 0) {
            NotificationHelper.showNotification(NotificationStatus.STATUS_FINISHED);
        }
        setupConditionsLayout(message);
    }

    /**
     * Setup the not met conditions pager.
     * @param message the message fired when the conditions changes.
     */
    private void setupConditionsLayout(final ConditionMessage message) {
        final boolean charger = !message.getNotMetConditions().contains(ConditionType.CHARGER);
        final boolean battery = !message.getNotMetConditions().contains(ConditionType.BATTERY);
        final boolean wifi = !message.getNotMetConditions().contains(ConditionType.WIFI);
        final boolean allConditionsMet = charger && battery && wifi;
        if (allConditionsMet) {
            mConditionsLayout.setVisibility(View.INVISIBLE);
            //mBackgroundImage.setAlpha(1F);
        } else {
            mConditionsLayout.setVisibility(View.VISIBLE);
            //mBackgroundImage.setAlpha(BACKGROUND_IMAGE_ALPHA);

            if (mAdapter == null) {
                mAdapter = new ConditionsSlidePagerAdapter(getSupportFragmentManager());
                mConditionsViewPager.setAdapter(mAdapter);

                mConditionsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(final int position,
                                               final float positionOffset,
                                               final int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(final int position) {
                        //updates the indicator selection
                        for (int i = 0; i < mConditionsIndicator.getChildCount(); i++) {
                            mConditionsIndicator.getChildAt(i).setSelected(i == position);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(final int state) {

                    }
                });
            }

            // Creates a new list because we're only interested in 3 conditions:
            // charger, battery, wifi.
            final List<ConditionType> notMetList = new ArrayList<>();
            if (!charger) {
                notMetList.add(ConditionType.CHARGER);
            }
            if (!battery) {
                notMetList.add(ConditionType.BATTERY);
            }
            if (!wifi) {
                notMetList.add(ConditionType.WIFI);
            }
            mAdapter.setConditionsNotMetList(notMetList);
            if(notMetList.size()>0){coverview.setVisibility(View.VISIBLE);}

            //adding the indicators and selecting the first one
            mConditionsIndicator.removeAllViews();
            if (notMetList.size() > 1) {
                for (int i = 0; i < notMetList.size(); i++) {
                    View.inflate(this, R.layout.view_pager_indicator, mConditionsIndicator);
                }
                mConditionsIndicator.getChildAt(0).setSelected(true);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(final JobExecutionMessage message) {
        loadJobsStats(message.getTitle(), message.getContributedTime());
        //call invalidateOptionsMenu to check if the research's detail
        //is available and show a new icon in the menu to open the detail
        invalidateOptionsMenu();
    }
}
