package com.example.ana.exampleapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.os.Handler;

/**
 * Service which send the daily tests to the server database. It is run in a different thread with
 * background priority to avoid blocking the main thread and disrupt the UI. If it can not send the
 * tests a {@link BroadcastReceiver} which notifies connection changes is set. When the app is
 * closed, the service is closed too, but it is restarted to avoid losing data. There is a bug in
 * Android 4.4.x that prevent the server to restart when the app is closed.
 *
 * @autor Ana María Martínez Gómez
 */
public class TestsService extends Service {

    private static BroadcastReceiver networkChangeReceiver; // To be aware of connection changes
    private Handler handler; // Handler for the separate Thread
    private static String TAG = "TestsService";

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.v(TAG, "Service created");
        // Starts up the thread running the service to avoid blocking the main thread and makes it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread handlerThread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        // Gets the Looper from the HandlerThread so that we can create a Handler attached to the
        // HandlerThread. This call will block until the HandlerThread gets control and initializes
        // its Looper.
        Looper looper = handlerThread.getLooper();
        // Create a handler for the service
        handler = new Handler(looper);
        try {
            SendTest runner = new SendTest();
            runner.execute(this);
            if (runner.get()) {
                // There are remaining tests, so we need to be aware of connection changes.
                registerNetworkChangeReceiver();
            } else {
                // There are not more tests to send. The services finishes.
                stopSelf();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Starting the service");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
        networkChangeReceiver = null;
        Log.v(TAG, "Service destroyed");
    }

    /**
     * Register a {@link BroadcastReceiver} which will resent the tests when connection changes.
     */
    private void registerNetworkChangeReceiver() {
        networkChangeReceiver = new BroadcastReceiver() {
            private String TAG = "registerNetworkChangeReceiver";

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "Connection changed received");
                try {
                    SendTest runner = new SendTest();
                    runner.execute(context);
                    if (!runner.get()) {
                        stopSelf();
                    }
                } catch (Exception e) {
                }
            }
        };
        IntentFilter filter = new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        // Register the broadcast receiver to run on the separate Thread
        registerReceiver(networkChangeReceiver, filter, null, handler);
    }
}