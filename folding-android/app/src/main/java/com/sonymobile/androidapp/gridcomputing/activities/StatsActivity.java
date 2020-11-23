package com.sonymobile.androidapp.gridcomputing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.adapters.ConditionsSlidePagerAdapter;
import com.sonymobile.androidapp.gridcomputing.adapters.StatsPagerAdapter;
import com.sonymobile.androidapp.gridcomputing.log.Log;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;


import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class StatsActivity extends AppCompatActivity implements View.OnClickListener {
    //Donor Stats Variables
    private String donorJSON="";
    private String donorName = "jcoffland"; //((ApplicationData) getApplication()).getName();
    private String url="https://stats.foldingathome.org/api/donor/"+donorName;
    private int donorwus = 0;
    private int donorRank = 0;
    private long donorCredit = 0;
    private int totalUsers=0;
    private double donorpercentile=0.0;

    //Team Stats Variables
    private String teamJSON="";
    private int teamno = 1;
    private int teamwus;
    private int teamRank;
    private int total_teams;
    private double teamPercentile;
    private long teamCredit;
    private String teamName="";
    private String[][] teamMemberData = new String[4][1000000]; //I should find a good way to set the length to teamarr.length later on
    private String url2;

    //Recent Projects Contributed to Variables
    private String url3="https://api.foldingathome.org/user/"+donorName+"/projects";
    private String projectsList="";
    private String[] projectInfo = new String[4];


    /**
     * The parent view of the Stats layout.
     */
    private View mStatsLayout;
    /**
     * The viewpager used to render the Stats.
     */
    private ViewPager mStatsViewPager;

    /**
     * The viewgroup that contains all the indicators.
     */
    private ViewGroup mStatsIndicator;

    /**
     * The Stats page adapter.
     */
    private StatsPagerAdapter mAdapter;

    //layout variabes

    // Donor Stats retrieval function
    public void DonorStats(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    donorJSON = response.body().string();
                    try {
                        ParseDonorJSON();
                        TeamStats();
                        RecentProjects();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void ParseDonorJSON() throws JSONException {
        JSONObject donorJSONobj = new JSONObject(donorJSON);
        donorwus = (int) donorJSONobj.get("wus");
        donorRank = (int)  donorJSONobj.get("rank");
        donorCredit = (long)  donorJSONobj.getLong("credit");
        totalUsers= (int) donorJSONobj.get("total_users");
        Log.d(String.valueOf(donorRank));
        Log.d(String.valueOf(totalUsers));
        donorpercentile = (int)((1.0-((float)donorRank)/((float)totalUsers))*1000.0)/10.0;
        JSONArray teamarr = donorJSONobj.getJSONArray("teams");
        int i=0;
        for (int j=0; j<teamarr.length();j++){
            if (teamarr.getJSONObject(i).getInt("credit") < teamarr.getJSONObject(j).getInt("credit"))
                i=j;
        }
        Log.d(String.valueOf(teamarr.getJSONObject(i).getInt("team")));
        teamno=teamarr.getJSONObject(i).getInt("team");
        url2 = "https://stats.foldingathome.org/api/team/"+ String.valueOf(teamno);


    }


    // Team Stats retrieval function
    public void TeamStats() {
        OkHttpClient client = new OkHttpClient();
        Log.d(url2);
        Request request = new Request.Builder()
                .url(url2)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    teamJSON = response.body().string();
                    try {
                        ParseTeamJSON();
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {


                                typeImage = findViewById(R.id.typeImage);
                                v3 = findViewById(R.id.view3);
                                prompt = findViewById(R.id.textView2);

                                researchTypeText = findViewById(R.id.researchtype);
                                researchID = findViewById(R.id.researchid);
                                researchDescription = findViewById(R.id.desc_text);
                                researchDescription.setMovementMethod(new ScrollingMovementMethod());


                                mStatsLayout = findViewById(R.id.stats_conditions_layout);
                                mStatsViewPager = (ViewPager) findViewById(R.id.stats_view_pager);
                                mStatsIndicator = (ViewGroup) findViewById(R.id.stats_page_indicator_container);


                                if (mAdapter == null) {
                                    mAdapter = new StatsPagerAdapter(getSupportFragmentManager());


                                    mStatsViewPager.setAdapter(mAdapter);


                                    mStatsViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                        @Override
                                        public void onPageScrolled(final int position,
                                                                   final float positionOffset,
                                                                   final int positionOffsetPixels) {

                                        }

                                        @Override
                                        public void onPageSelected(final int position) {
                                            //updates the indicator selection
                                            for (int i = 0; i < mStatsIndicator.getChildCount(); i++) {
                                                mStatsIndicator.getChildAt(i).setSelected(i == position);
                                            }
                                        }

                                        @Override
                                        public void onPageScrollStateChanged(final int state) {

                                        }
                                    });
                                }

                                List<String> namePay = new ArrayList<>();
                                namePay.add(donorName); namePay.add(teamName);
                                List<long []> statsPay = new ArrayList<>();
                                long [] donorStats = {(long) donorpercentile, (long) donorwus, (long) donorCredit};
                                statsPay.add(donorStats);
                                long [] teamStats = {(long) teamPercentile, (long) teamwus, (long) teamCredit};
                                statsPay.add(teamStats);

                                mAdapter.setStats(namePay, statsPay);

                                //adding the indicators and selecting the first one
                                mStatsIndicator.removeAllViews();

                                for (int i = 0; i < 2; i++) {
                                    View.inflate(getApplicationContext(), R.layout.view_pager_indicator, mStatsIndicator);
                                }
                                mStatsIndicator.getChildAt(0).setSelected(true);



                                if(projectInfo[3] == null){
                                    prompt.setAlpha(0.0F);
                                    researchID.setAlpha(0.0f);
                                    researchTypeText.setAlpha(0.0f);
                                    researchDescription.setAlpha(0.0f);
                                    v3.setAlpha(0.0f);
                                } else {
                                    researchID.setText(projectInfo[0]);
                                    researchTypeText.setText(Character.toUpperCase(projectInfo[2].charAt(0)) + projectInfo[2].substring(1));
                                    researchDescription.setText(Html.fromHtml(Html.fromHtml(projectInfo[3]).toString()));
                                }


                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void ParseTeamJSON() throws JSONException {
        JSONObject teamJSONobj = new JSONObject(teamJSON);
        teamwus = (int)teamJSONobj.get("wus");
        teamName = teamJSONobj.getString("name");
        teamRank = (int)teamJSONobj.get("rank");
        Log.d(String.valueOf(teamRank));
        Log.d(String.valueOf(teamCredit));
        total_teams = (int)teamJSONobj.get("total_teams");
        Log.d(String.valueOf(total_teams));
        teamPercentile= (int)((1.0-((float)teamRank)/((float)total_teams))*1000.0)/10.0;
        Log.d(String.valueOf(((float)teamRank)/((float)total_teams)));
        Log.d(String.valueOf(teamPercentile));
        teamCredit = (long) teamJSONobj.getLong("credit");
        JSONArray teamarr = teamJSONobj.getJSONArray("donors");
        int numOfMembers=teamarr.length();
        for (int i=0; i<teamarr.length(); i++){
            teamMemberData[0][i]=teamarr.getJSONObject(i).getString("name");
            teamMemberData[1][i]=teamarr.getJSONObject(i).get("wus").toString();
            teamMemberData[2][i]=teamarr.getJSONObject(i).get("rank").toString();
            teamMemberData[3][i]=teamarr.getJSONObject(i).get("credit").toString();
        }


        // uses https://github.com/stleary/JSON-java

    }
    //Recent Projects retrieval
    public void RecentProjects() {
        Log.d("in here");
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url3)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String f = response.body().string();
                    projectsList = f.substring(1, f.length() - 1);
                    String[] temparr = projectsList.split(",");
                    int i=0;

                    while (projectInfo[3]==null && i<temparr.length){
                        Log.d("while loop");
                        Log.d(projectInfo[3]);
                        projectInfo[0] = temparr[temparr.length - 1 - i];
                        projectInfo = projectRequests(projectInfo);
                        i++;
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            displayResearch(projectInfo);

                        }
                    });
                }
            }
        });
    }


    // call another request
    public String[] projectRequests(String[] projectdata) {
        final String [] temparr2=projectdata;
        String url4= "https://api.foldingathome.org/project/"+projectdata[0];
        final OkHttpClient client = new OkHttpClient();
        Request request2 = new Request.Builder()
                .url(url4)
                .build();
        client.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String projectJSON=response.body().string();
                    JSONObject projectJSONobj= null;
                    try {
                        projectJSONobj = new JSONObject(projectJSON);
                        temparr2[1]=projectJSONobj.getString("manager");
                        temparr2[2]=projectJSONobj.getString("cause");
                        temparr2[3]=projectJSONobj.getString("description");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // uses https://github.com/stleary/JSON-java


                }}});
        return temparr2;
    }

    // layout element variables




    private TextView researchTypeText;
    private TextView researchID;

    private TextView researchDescription;
    private TextView prompt;
    private ImageView typeImage;

    private Button share;
    private View v3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("IN");
        setContentView(R.layout.activity_stats);
        DonorStats();
        Log.d("1");
        //TeamStats();
        Log.d("2");
        //RecentProjects();
        Log.d("3");
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        Log.d(String.valueOf(donorCredit));
        Log.d(String.valueOf(donorpercentile));
        Log.d(String.valueOf(donorRank));
        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        typeImage = findViewById(R.id.typeImage);
        typeImage.setVisibility(View.GONE);
        v3 = findViewById(R.id.view3);

        prompt = findViewById(R.id.textView2);

        researchTypeText = findViewById(R.id.researchtype);
        researchID = findViewById(R.id.researchid);
        researchDescription = findViewById(R.id.desc_text);
        researchDescription.setMovementMethod(new ScrollingMovementMethod());


        share = findViewById(R.id.shareButton);
        share.setOnClickListener(this);
        if(projectInfo[3] == null){
            prompt.setAlpha(0.0F);
            researchID.setAlpha(0.0f);
            researchTypeText.setAlpha(0.0f);
            researchDescription.setAlpha(0.0f);
            v3.setAlpha(0.0f);
        } else {
            researchID.setText(projectInfo[0]);
            researchTypeText.setText(Character.toUpperCase(projectInfo[2].charAt(0)) + projectInfo[2].substring(1));
            researchDescription.setText(Html.fromHtml(Html.fromHtml(projectInfo[3]).toString()));
        }


        BottomNavigationView navView = findViewById(R.id.nav_view1);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
                     /*   startActivity(new Intent(getApplicationContext(), StatsActivity.class));
                        overridePendingTransition(0,0); */
                        return true;
                    case R.id.navigation_dashboard:
                        startActivity(new Intent(getApplicationContext(), SummaryActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.navigation_notifications:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });



        Log.d("all the way through");

    }


    public void displayResearch(String [] projectInfo){
        Log.d("DISPLAY: " + projectInfo[2]);
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        if(projectInfo[3] == null){
            prompt.setAlpha(0.0F);
            researchID.setAlpha(0.0f);
            researchTypeText.setAlpha(0.0f);
            researchDescription.setAlpha(0.0f);
            typeImage.setVisibility(View.GONE);
            v3.setAlpha(0.0f);
        } else {
            typeImage.setVisibility(View.VISIBLE);
            prompt.setAlpha(1.0F);
            researchID.setAlpha(1.0f);
            researchTypeText.setAlpha(1.0f);
            researchDescription.setAlpha(1.0f);
            v3.setAlpha(1.0f);
            researchID.setText(projectInfo[0]);
            switch(projectInfo[2]){
                case "cancer":
                    typeImage.setImageResource(R.drawable.cancer);
                case "alzheimers":
                    typeImage.setImageResource(R.drawable.alzheimers);
                case "diabetes":
                    typeImage.setImageResource(R.drawable.diabetes);
                case "huntingtons":
                    typeImage.setImageResource(R.drawable.huntingtons);
                case "parkinsons":
                    typeImage.setImageResource(R.drawable.parkinsons);
                case "influenza":
                    typeImage.setImageResource(R.drawable.influenza);
                case "undefined":
                    typeImage.setImageResource(R.drawable.undefined);
                case "covid-19":
                    typeImage.setImageResource(R.drawable.covid);

            }
            researchTypeText.setText(Character.toUpperCase(projectInfo[2].charAt(0)) + projectInfo[2].substring(1));
            researchDescription.setText(Html.fromHtml(Html.fromHtml(projectInfo[3]).toString()));
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shareButton:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                String shareBody = "I completed " + String.valueOf(donorwus) + " Work Units and earned " + String.valueOf(donorCredit) + " points for Folding@Home";
                i.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(i, "Share via"));
        }

    }



}