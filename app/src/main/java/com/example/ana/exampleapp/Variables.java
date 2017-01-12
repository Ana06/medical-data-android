package com.example.ana.exampleapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * Class with static final variables and methods used around the program.
 *
 * @author Ana María Martínez Gómez
 */
public final class Variables {
    // The server IP
    private static final String IP = "localhost";
    private static final String PORT = "27017";
    // MongoDB uri with the IP, authentication (user, password and mechanism), database, etc.
    //public static final String mongo_uri = "mongodb://127.0.0.1:27017/test";

    //used for test purpose!
    public static final String mongo_uri = "mongodb://" + IP + ":" + PORT + "/test";

    // Name of shared preferences file;
    public static final String PREFS_NAME = "MyPrefsFile";
    // Regular expression to check email correction
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     * Final class: to prevent someone from accidentally instantiating the class, we give it an
     * empty constructor.
     */
    public Variables() {
    }

    /**
     * Hide the keyboard and clear focus. 
     * Source: http://stackoverflow.com/a/1109108/6245337 by Reto Meier
     *
     * @param activity the activity where we want to hide the keyboard and clear focus
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        view.clearFocus();
    }

    /**
     * Check if there is connection and if it is Wi-Fi connection.
     *
     * @param context The {@link Activity} context
     * @return -1 if there is not internet connection. 1 if there is Wi-Fi connection and 0 if there
     * there is any other kind of connection. So if the returned value is greater or equal to 0
     * there is connection.
     */
    public static int connection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // getActiveNetworkInfo() returns null when there is no default network.
        if (activeNetwork == null)
            return -1;
        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
            return 1;
        // isConnected() indicates whether network connectivity exists and it is possible to
        // establish connections and pass data
        if (activeNetwork.isConnected())
            return 0;
        return -1;
    }

    /**
     * Saves the local tests in the {@link @SharedPreferences}
     *
     * @param TAG         String for the Log
     * @param settings    the context's shared preferences
     * @param local_tests number of local test that haven't been saved
     */
    public static void saveLocalTests(String TAG, SharedPreferences settings, int local_tests) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("local_tests", local_tests);
        editor.commit();
        Log.v(TAG, "Tests: " + local_tests);
    }
}
