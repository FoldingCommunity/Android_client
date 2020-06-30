/*
 * Licensed under the LICENSE.
 * Copyright 2017, Sony Mobile Communications Inc.
 */

package com.sonymobile.androidapp.gridcomputing.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

import com.sonymobile.androidapp.gridcomputing.assets.CopyAssets;
import com.sonymobile.androidapp.gridcomputing.log.Log;
import com.sonymobile.androidapp.gridcomputing.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import static java.lang.Thread.sleep;

/**
 * Class responsible for executing the node.
 */
public class ComputeEnvironment {

    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("node");
    }

    /**
     * LD library path.
     */
    private static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
    /**
     * Encode.
     */
    private static final String CHARSET = "UTF-8";

    /**
     * Context.
     */
    private final Context mContext;
    /**
     * Job execution listener.
     */
    private final JobExecutionListener mJobExecutionListener;
    /**
     * Wake lock.
     */
    private final PowerManager.WakeLock mActiveLock;
    /**
     * Process.
     */
    private Process mExecProccess;

    private Thread jobThread;

    private Thread listenThread;

    /**
     * The class constructor.
     *
     * @param context  the context.
     * @param listener the listener.
     */
    @SuppressLint("InvalidWakeLockTag")
    public ComputeEnvironment(final Context context,
                              final JobExecutionListener listener) {
        mContext = context;
        mJobExecutionListener = listener;
        mActiveLock = ((PowerManager) context
                .getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK, "compute-running");
    }

    /**
     * Starts the process.
     *
     * @return the process.
     * @throws IOException the io exception.
     */
    private Process startProcess() throws IOException {
        Log.d("PROCCESS-IN");
        ProcessBuilder processBuilder;
        final File dir = mContext.getDir(CopyAssets.EXEC_DIR, Context.MODE_PRIVATE);
        final String gcompExecDir = mContext.getApplicationInfo().nativeLibraryDir;
        final String gcompExecFile = gcompExecDir + "/" + CopyAssets.GCOMP;
        processBuilder = new ProcessBuilder(gcompExecFile, CopyAssets.CLIENT_JS_FILE);

        processBuilder.directory(dir);
        processBuilder.redirectErrorStream(true);


        final String ldLibrary = processBuilder.environment().get(LD_LIBRARY_PATH);
        processBuilder.environment()
                .put(LD_LIBRARY_PATH, ldLibrary + ":" + gcompExecDir);


        Log.d("Process out");
        return processBuilder.start();
    }

    /**
     * Executes the job.
     */
    public final void runJob() {
        jobThread = new RunJobThread();
        jobThread.start();
        listenThread = new RunListenThread();
        listenThread.start();
    }

    /**
     * Method invoked when conditions change.
     *
     * @param active   the active
     * @param hardStop the hard stop
     */
    public void conditionChanged(final boolean active, final boolean hardStop) {
        Log.d("conditionChanged active: " + active + " hardStop: " + hardStop);
        if (active) {
            resumeJob();
        } else {
            stopJob(hardStop);
        }
    }

    /**
     * Resumes the job.
     */
    private void resumeJob() {
        Log.d("Resume Job");
        getProcessOutputStream(EnvironmentMessenger
                    .getJsonResumeJobClient());

    }

    /**
     * Stops the job.
     *
     * @param hardStop the hard stop
     */
    private void stopJob(final boolean hardStop) {
        getProcessOutputStream(EnvironmentMessenger
                .getJsonKillClient(hardStop));
        stopProcess();
        jobThread.interrupt();
        listenThread.interrupt();
        Log.d("STOPPED JOB");

    }

    /**
     * Stops process.
     */
    private void stopProcess() {
            //int c = stopNode();
            //Log.d("EXIT CODE: " + String.valueOf(c));

            if (isActiveLockHeld()) {
                mActiveLock.release();
            }
            if (mJobExecutionListener != null) {
                mJobExecutionListener.clientStopped();
            }
        Log.d("STOPPED PROCESS");

    }

    /**
     * Method that checks the wake lock.
     *
     * @return true if is held
     */
    public final boolean isActiveLockHeld() {
        return mActiveLock.isHeld();
    }

    /**
     * Gets buffered reader.
     *
     * @return the buffered reader
     * @throws UnsupportedEncodingException the encoding exception
     */
    protected String getReader() {
        //return new BufferedReader(
        //       new InputStreamReader(mExecProccess.getInputStream(), CHARSET));
        String nodeResponse="";
        try {
            URL localNodeServer = new URL("http://localhost:3000/");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(localNodeServer.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                nodeResponse=nodeResponse+inputLine;
            in.close();
            Log.d("Readerout");
        } catch (Exception ex) {
            Log.d("exception: " + ex.toString());
            nodeResponse=ex.toString();
        }
        return nodeResponse;

    }

    /**
     * Gets output stream.
     *
     * @return the output stream
     * @param data
     */
    protected void getProcessOutputStream(String data){
        new AsyncTask<String,Void,String>() {
            @Override
            protected String doInBackground(String... params) {
                String urlString = "http://localhost:3000/"; // URL to call
                HttpURLConnection urlConnection = null;
                String data = "";

                DataOutputStream writer = null;
                try {
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    writer = new DataOutputStream(urlConnection.getOutputStream());


                    //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                    writer.writeBytes(params[0]);
                    writer.flush();
                    writer.close();
                    //out.close();
                    Log.d("WROTE: " + params[0]);


                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(in);
                    int inputStreamData = inputStreamReader.read();
                    while(inputStreamData != -1){
                        char current = (char) inputStreamData;
                        inputStreamData = inputStreamReader.read();
                        data += current;
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                } finally {
                    if(urlConnection != null){ urlConnection.disconnect();}
                }
                return data;
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("RESPONSE: "  + result);

            }
        }.execute(data);

    }

    /**
     * Runs job thread.
     */
    private class RunJobThread extends Thread {
        @Override
        public void run() {
            BufferedReader reader = null;

            try {
                //mExecProccess = startProcess();

                String nodeDir=mContext.getDir(CopyAssets.EXEC_DIR, Context.MODE_PRIVATE).getAbsolutePath();
                startNodeWithArguments(new String[]{"node",
                        nodeDir+File.separator + CopyAssets.CLIENT_JS_FILE});

                Log.d("THREAD: JOB");
                mActiveLock.acquire();
                String str = getReader();

            } catch (final Exception e) {
                Log.e(e.getLocalizedMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (final IOException e) {
                    Log.e(e.getLocalizedMessage());
                }
                stopProcess();
            }
        }

    }

    private class RunListenThread extends Thread {
        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                mActiveLock.acquire();
                //reader = getReader();
                String str = getReader();

                // reads the inputstream from the gcomp_node process
                while (str != null) {
                    if(this.isInterrupted()) break;
                    Log.d("Read from Client > " + str);
                    final JSONObject jsonObject = JSONUtils.parseJSONObject(str);
                    final String action = JSONUtils.getString(jsonObject, "action", "");
                    final JSONObject content = JSONUtils.getJSONObject(jsonObject, "content");

                    if ("no_job_available".equalsIgnoreCase(action)) {
                    } else if ("number_of_users".equalsIgnoreCase(action)) {
                        mJobExecutionListener.numberOfUsersReceived(JSONUtils
                                .getLong(content, "number_of_users", 0L));
                    } else if ("research_details".equalsIgnoreCase(action)) {
                        mJobExecutionListener.researchDetailsReceived(content);
                    } else if ("limit_storage".equalsIgnoreCase(action)) {
                    } else if ("get_key".equalsIgnoreCase(action)) {
                        getProcessOutputStream(EnvironmentMessenger.getJsonKeyReply());
                    } else if ("job_received".equalsIgnoreCase(action)) {
                    } else if ("job_finished".equalsIgnoreCase(action)) {
                    } else if ("key_accepted".equalsIgnoreCase(action)) {
                    } else if ("job_execution_error".equalsIgnoreCase(action)) {
                        try {
                            final String error = content.getString("error");
                            if (error != null && !error.isEmpty()
                                    && !"undefined".equalsIgnoreCase(error)) {
                                // Ignore error.
                            }
                        } catch (final Exception exception) {
                            Log.e(exception.getLocalizedMessage());
                        }
                    } else if ("client_killed".equalsIgnoreCase(action)) {
                        break;
                    }

                    str = getReader();
                }
            } catch (final Exception e) {
                Log.e(e.getLocalizedMessage());
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (final IOException e) {
                    Log.e(e.getLocalizedMessage());
                }
                stopProcess();
            }
        }

    }

    public native Integer startNodeWithArguments(String[] arguments);

   // public native Integer stopNode();

}
