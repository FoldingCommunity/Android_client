/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.utils;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.facebook.FacebookSdk;
import com.sonymobile.androidapp.gridcomputing.preferences.MiscPref;
import androidx.multidex.MultiDexApplication;

import de.greenrobot.event.EventBus;

/**
 * Stores the application context.
 */
public class ApplicationData extends MultiDexApplication {

    //Has username?
    private static String dName;

    //Has user logged in?
    private static boolean status;

    /**
     * The application context.
     */
    private static Context sContext;

    /**
     * Event bus used to send messages across the application.
     */
    private static EventBus sEventBus;
    /**
     * System default exception handler.
     */
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    /**
     * Custom exception handler.
     */

    //Donor stats loaded at runtime
    public static int[] donorstats = new int[3];

    //Team stats loaded at runtime
    public static int[] teamstats = new int[3];

    public String getName() {
        return dName;
    }

    public void setName(String someVariable) {
        this.dName = someVariable;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean someVariable) {
        this.status = someVariable;
    }

    public int[] getDonorstats(){
        return donorstats;
    }

    public int[] getTeamstats(){
        return teamstats;
    }

    public void setDonorStats(int donorpercentile, int donorwus, int donorcredit){
        donorstats = new int[]{donorpercentile, donorwus, donorcredit};
    }

    public void setTeamStats(int teampercentile, int teamwus, int teamcredit){
        teamstats = new int[]{teampercentile, teamwus, teamcredit};
    }

    private final Thread.UncaughtExceptionHandler mCaughtExceptionHandler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(final Thread thread, final Throwable ex) {
                    String classpath = null;
                    //ONLY ignores the "Results have already been set" exception from GTM
                    if (ex != null && ex.getStackTrace().length > 0) {
                        classpath = ex.getStackTrace()[0].toString();
                    }
                    final boolean gtmException = classpath != null
                            && ex.getMessage().contains("Results have already been set")
                            && classpath.contains("com.google.android.gms.tagmanager");
                    if (!gtmException) {
                        // run default handler
                        mDefaultUEH.uncaughtException(thread, ex);
                    }
                }
            };

    /**
     * Returns an application context.
     *
     * @return an application context.
     */
    public static Context getAppContext() {
        return sContext;
    }

    /**
     * Gets the event bus.
     *
     * @return the event bus.
     */
    public static EventBus getBus() {
        return sEventBus;
    }

    /**
     * Checks if the environment is JUnit this means, if the application
     * is running through JUnit.
     * @return true if the application is running from JUnit.
     */
    public static boolean isJUnit() {
        return "JUnit".equals(System.getProperty("Env"));
    }

    /**
     * Inits event bus instance.
     */
    private static void initEventBus() {
        sEventBus = new EventBus();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public final void onCreate() {
        super.onCreate();
        setContext();

        // Init Facebook SDK.
        FacebookSdk.sdkInitialize(getAppContext());

        // for catching app global unhandled exceptions
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(mCaughtExceptionHandler);

        //Fabric.with(getAppContext(), new Crashlytics());

        initEventBus();

        MiscPref.setLastBatteryPlateauTime(0);
    }

    /**
     * Sets the app context.
     */
    private void setContext() {
        sContext = this;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
