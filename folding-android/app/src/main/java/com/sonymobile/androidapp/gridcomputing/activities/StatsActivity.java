package com.sonymobile.androidapp.gridcomputing.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sonymobile.androidapp.gridcomputing.R;
import com.sonymobile.androidapp.gridcomputing.log.Log;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;


import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import java.io.IOException;
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

    // layout elements variables
    private TextView creditText;
    private TextView rankText;
    private TextView percentileText;
    private Switch switch1;
    private TextView researchTypeText;
    private TextView researchID;
    private TextView name;
    private TextView researchDescription;
    private TextView prompt;
    private ImageView typeImage;
    private ProgressBar progressWheel;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private View v3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("IN");
        setContentView(R.layout.activity_stats);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    DonorStats();
                } catch (Exception ex) {
                    Log.d(ex.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                percentileText.setText(String.valueOf(donorpercentile) + " Percentile");
                rankText.setText("WUs: " + Integer.toString(donorwus));
                creditText.setText(Long.toString(donorCredit) + " Pts.");
                progressWheel = findViewById(R.id.progressWheel);
                progressWheel.setProgress((int)donorpercentile);
                name.setText(donorName);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        try {
                            TeamStats();
                        } catch (Exception ex) {
                            Log.d(ex.toString());
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
                        switch1.setVisibility(View.VISIBLE);

                    }
                }.execute();
            }
        }.execute();

        try {
            sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("1");
        //TeamStats();
        Log.d("2");
        //RecentProjects();
        Log.d("3");
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
        creditText = findViewById(R.id.credit_text);
        rankText = findViewById(R.id.rank_text);
        percentileText = findViewById(R.id.Percentile_text);
        name = findViewById(R.id.textView3);
        switch1 = findViewById(R.id.switch1);
        switch1.setVisibility(View.GONE);
        researchTypeText = findViewById(R.id.researchtype);
        researchID = findViewById(R.id.researchid);
        researchDescription = findViewById(R.id.desc_text);
        researchDescription.setMovementMethod(new ScrollingMovementMethod());
     /*   percentileText.setText(String.valueOf(donorpercentile) + " Percentile");
        rankText.setText("WUs: " + Integer.toString(donorwus));
        creditText.setText(Long.toString(donorCredit) + " Pts.");
        progressWheel = findViewById(R.id.progressWheel);
        progressWheel.setProgress((int)donorpercentile);
        name.setText(donorName); */
       /* b1 = findViewById(R.id.button2);
        b2 = findViewById(R.id.button3);
        b3 = findViewById(R.id.button4);
        b4 = findViewById(R.id.button5);
        b5 = findViewById(R.id.button6); */
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
     /*   b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);d
        b4.setOnClickListener(this);
        b5.setOnClickListener(this); */
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    name.setText(teamName);
                    progressWheel.setProgress((int) teamPercentile);
                    percentileText.setText(String.valueOf(teamPercentile) + " Percentile");
                    rankText.setText("WUs: " + Integer.toString(teamwus));
                    creditText.setText(Long.toString(teamCredit) + " Pts.");
                }
                else {
                    name.setText(donorName);
                    progressWheel.setProgress((int)donorpercentile);
                    percentileText.setText(String.valueOf(donorpercentile) + " Percentile");
                    rankText.setText("WUs: " + Integer.toString(donorRank));
                    creditText.setText(Long.toString(donorCredit) + "Pts.");
                }
            }
        });

        BottomNavigationView navView = findViewById(R.id.nav_view1);
        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigation_home:
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
                        finish();
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });


        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
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
                                Log.d("PI:" + projectInfo[3]);

                            }
                        }
                    });
                } catch (Exception ex) {
                    Log.d(ex.toString());
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                displayResearch();
            }
        }.execute();


        Log.d("all the way through");

    }


    public void displayResearch(){
        Log.d("DISPLAY: " + projectInfo[2]);
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
          /*  case R.id.button2:
                UpdateResearch(0);
                b1.setBackgroundColor(getResources().getColor(R.color.orange));
                b2.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b3.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b4.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b5.setBackgroundColor(getResources().getColor(R.color.sony_gray));

            case R.id.button3:
                UpdateResearch(1);
                b2.setBackgroundColor(getResources().getColor(R.color.orange));
                b1.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b3.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b4.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b5.setBackgroundColor(getResources().getColor(R.color.sony_gray));

            case R.id.button4:
                UpdateResearch(2);
                b3.setBackgroundColor(getResources().getColor(R.color.orange));
                b2.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b1.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b4.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b5.setBackgroundColor(getResources().getColor(R.color.sony_gray));

            case R.id.button5:
                UpdateResearch(3);
                b4.setBackgroundColor(getResources().getColor(R.color.orange));
                b2.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b3.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b1.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b5.setBackgroundColor(getResources().getColor(R.color.sony_gray));

            case R.id.button6:
                UpdateResearch(4);
                b5.setBackgroundColor(getResources().getColor(R.color.orange));
                b2.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b3.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b4.setBackgroundColor(getResources().getColor(R.color.sony_gray));
                b1.setBackgroundColor(getResources().getColor(R.color.sony_gray));*/






        }

    }



}