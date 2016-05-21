package com.example.ana.exampleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class NetworkChangeReceiver extends BroadcastReceiver {
    private String TAG = "SendTest2";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Connection changed received");
        SharedPreferences settings =
                context.getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        int local_tests = settings.getInt("local_tests", 0);
        Log.v(TAG, "Tests: " + local_tests);
        try {
            SendTest runner = new SendTest();
            runner.execute(context);
            runner.get();
        } catch (Exception e) {
        }
    }
}
