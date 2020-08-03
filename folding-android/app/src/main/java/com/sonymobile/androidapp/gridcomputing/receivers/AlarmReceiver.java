/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */
package com.sonymobile.androidapp.gridcomputing.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sonymobile.androidapp.gridcomputing.log.Log;
import com.sonymobile.androidapp.gridcomputing.preferences.SettingsPref;
import com.sonymobile.androidapp.gridcomputing.service.ServiceManager;
import com.sonymobile.androidapp.gridcomputing.utils.AlarmUtils;
import com.sonymobile.androidapp.gridcomputing.activities.SummaryActivity;

/**
 * Alarm receiver class handles broadcast from AlarmManager.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public final void onReceive(final Context context, final Intent intent) {
        // TODO: Do whatever we have to do when the alarm "rings".
        Log.d(intent.getAction());
        if (intent.getAction().equals(AlarmUtils.AlarmType.REPEAT_1_MIN.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.REPEAT_1_MIN.name());
            if (SettingsPref.isExecutionEnabled()) {
                ServiceManager.verifyConditionsAndStartService();
            }
        } else if (intent.getAction().equals(AlarmUtils.AlarmType.REPEAT_30_MIN.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.REPEAT_30_MIN.name());
        } else if (intent.getAction().equals(AlarmUtils.AlarmType.REPEAT_24_HOUR.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.REPEAT_24_HOUR.name());
            ServiceManager.resume();
        } else if (intent.getAction().equals(AlarmUtils.AlarmType.SCHEDULED.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.SCHEDULED.name());
            ServiceManager.resume();
        }
        else if (intent.getAction().equals(AlarmUtils.AlarmType.SCHEDULED_START.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.SCHEDULED_START.name());
            ServiceManager.resume();
        }
        else if (intent.getAction().equals(AlarmUtils.AlarmType.SCHEDULED_END.name())) {
            Log.d("Alarm - " + AlarmUtils.AlarmType.SCHEDULED_END.name());
            Log.d(String.valueOf(SummaryActivity.isChecked()));
            if(SummaryActivity.isChecked()){
                if(!SummaryActivity.clickSwitch()){
                    ServiceManager.pause();
                }
            }
            ServiceManager.pause();
        }
    }
}
