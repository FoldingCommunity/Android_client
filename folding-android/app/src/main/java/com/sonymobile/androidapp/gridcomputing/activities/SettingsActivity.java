package com.sonymobile.androidapp.gridcomputing.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonymobile.androidapp.gridcomputing.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            PreferenceManager manager = getPreferenceManager();
            manager.setSharedPreferencesName("settings_pref");

            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

    }
}