package com.sonymobile.androidapp.gridcomputing.activities.ui.login;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.sonymobile.androidapp.gridcomputing.activities.SummaryActivity;
import com.sonymobile.androidapp.gridcomputing.conditions.ConditionsHandler;
import com.sonymobile.androidapp.gridcomputing.log.Log;

import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;


import com.sonymobile.androidapp.gridcomputing.R;

import com.sonymobile.androidapp.gridcomputing.preferences.MiscPref;
import com.sonymobile.androidapp.gridcomputing.preferences.PrefUtils;

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

public class LoginActivity extends AppCompatActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        final EditText usernameEditText = findViewById(R.id.username);
        final Button loginButton = findViewById(R.id.login);

        final Intent i = new Intent(LoginActivity.this, SummaryActivity.class);

        boolean status = PrefUtils.getBooleanValue("account_pref", "STATUS", false);
        Log.d("STATUS:" + String.valueOf(status));
        if (status) {
            startActivity(i);
        }

        final boolean[] legitSignUp = {false};

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onclick");
                final String username = usernameEditText.getText().toString();


                String url = "https://stats.foldingathome.org/api/donor/" + username;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("Username found");
                            String donorName = username;
                            legitSignUp[0] = true;
                            Log.d(donorName);
                            int uID=getID(donorName);
                            startActivity(i);
                            String donorJSON = response.body().string();

                        } else{
                            //TODO: Handle creation of new usernames

                        }
                    }
                });
                if(legitSignUp[0]){
                    startActivity(i);
                }
            }


        });
    }

    public int getID(String name){
        final int[] id = {0};
        PrefUtils.setBooleanValue("account_pref", "STATUS", true);
        Log.d(String.valueOf("STATUS: " + PrefUtils.getBooleanValue("account_pref", "STATUS", false)));
        PrefUtils.setStringValue("account_pref", "USERNAME", name);
        String url = "https://stats.foldingathome.org/api/donor/" + name;
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
                    String donorJSON = response.body().string();
                    try {
                        JSONObject donorJSONobj = new JSONObject(donorJSON);
                        id[0] =donorJSONobj.getInt("id");
                        Log.d(String.valueOf(id[0]));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }



        });

        try {
            sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MiscPref.setUUID(String.valueOf(id[0]));
        return id[0];
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent i = new Intent(LoginActivity.this, SummaryActivity.class);

        boolean status = PrefUtils.getBooleanValue("account_pref", "STATUS", false);
        Log.d("STATUS:" + String.valueOf(status));
        if (status) {
            startActivity(i);
        }

    }

}

