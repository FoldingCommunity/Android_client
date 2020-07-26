/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.preferences;


import com.sonymobile.androidapp.gridcomputing.service.ServiceManager;

public final class SettingsPref {
    public static final String PREF_FILE = "settings_pref";
    public static final String EXECUTION_ENABLED_KEY = "EXECUTION_ENABLED_KEY";
    public static final String CELLULAR_ENABLED_KEY = "CELLULAR_ENABLED_KEY";
    public static final String PAUSE_TIME_KEY = "PAUSE_TIME_KEY";
    public static final String HAS_EXECUTED_KEY = "HAS_EXECUTED_KEY";
    public static final String MIN_BATTERY_KEY = "MIN_BATTERY_KEY";

    private SettingsPref() { }

    /**
     * Checks if the execution is enable by the user.
     *
     * @return true if the execution is enable by the user, false otherwise.
     */
    public static int getMinBattery() {
        return PrefUtils.getIntValue1(PREF_FILE, MIN_BATTERY_KEY, 20);
    }

    public static void setMinBattery(int val) {
        PrefUtils.setIntValue(PREF_FILE, MIN_BATTERY_KEY, val);
    }

    //check whether cellular networks are enabled
    public static boolean getCellularPref() {
        return PrefUtils.getBooleanValue(PREF_FILE, CELLULAR_ENABLED_KEY, false);
    }


    /**
     * Sets the execution flag enable or disable.
     *
     * @param enabled execution enable param.
     */
    public static void setExecutionEnabled(final boolean enabled) {
        PrefUtils.setBooleanValue(PREF_FILE, EXECUTION_ENABLED_KEY, enabled);
        ServiceManager.verifyConditionsAndStartService();
    }

    public static boolean isExecutionEnabled() {
        return PrefUtils.getBooleanValue(PREF_FILE, EXECUTION_ENABLED_KEY, true);
    }

    /**
     * Sets the application paused at the given time.
     *
     * @param time time since the application has been paused.
     */
    public static void setPausedTime(final long time) {
        PrefUtils.setLongValue(PREF_FILE, PAUSE_TIME_KEY, time);
    }

    /**
     * Returns the time (in milis) when the folding execution was paused.
     *
     * @return the time (in milis) when the folding execution was paused.
     */
    public static long getPauseTime() {
        return PrefUtils.getLongValue(PREF_FILE, PAUSE_TIME_KEY, 0);
    }

    public static boolean isPaused() {
        return getPauseTime() > 0;
    }


    /**
     * Marks that the app has executed jobs at some point.
     */
    public static void setHasExecuted() {
        PrefUtils.setBooleanValue(PREF_FILE, HAS_EXECUTED_KEY, true);
    }

    /**
     * Returns if the application has executed jobs.
     *
     * @return if the application has executed jobs.
     */
    public static boolean hasExecuted() {
        return PrefUtils.getBooleanValue(PREF_FILE, HAS_EXECUTED_KEY, false);
    }
}
