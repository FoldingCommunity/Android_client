package com.sonymobile.androidapp.gridcomputing.activities;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.preferences.PrefUtils;
import com.sonymobile.androidapp.gridcomputing.utils.AlarmUtils;
import com.sonymobile.androidapp.gridcomputing.utils.ApplicationData;
import com.sonymobile.androidapp.gridcomputing.views.TimePreference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        BottomNavigationView navView = findViewById(R.id.nav_view3);
        navView.setSelectedItemId(R.id.navigation_notifications);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                        finish();
                        startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
                SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                          String key) {
                        Log.d("SETTINGS", "PREFKEY: " + key);
                        if(key.equals("SCHEDULING_KEY")){
                            boolean enable = PrefUtils.getBooleanValue("settings_pref", "SCHEDULING_KEY", false);
                            long start_time = PrefUtils.getLongValue("settings_pref", "START_TIME", 0);
                            long end_time = PrefUtils.getLongValue("settings_pref", "END_TIME", 0);
                            if(enable){
                                AlarmUtils.setRTCAlarm(AlarmUtils.AlarmType.SCHEDULED_START.name(), 147855, start_time, AlarmManager.INTERVAL_DAY, true);
                                AlarmUtils.setRTCAlarm(AlarmUtils.AlarmType.SCHEDULED_END.name(), 147856, end_time, AlarmManager.INTERVAL_DAY, true);
                            }else{
                                AlarmUtils.cancelAlarm(AlarmUtils.AlarmType.SCHEDULED_END);
                                AlarmUtils.cancelAlarm(AlarmUtils.AlarmType.SCHEDULED_START);
                            }
                        }

                    }
                };

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName("settings_pref");

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            manager.getSharedPreferences().registerOnSharedPreferenceChangeListener(spChanged);
        }


    }
}