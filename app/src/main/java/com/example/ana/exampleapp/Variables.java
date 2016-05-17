package com.example.ana.exampleapp;

import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * Class with static final variables and methods used in the program.
 *
 * @author Ana María Martínez Gómez
 */
public final class Variables {
    // Name of shared preferences file;
    public static final String PREFS_NAME = "MyPrefsFile";
    // Regular expression to check email correction
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    // MongoDB uri with the IP, authentication (user, password and mechanism), database, etc.
    public static final String mongo_uri =
            "mongodb://androidUser:1234@192.168.1.62:27017/bipolarDatabase?authMechanism=MONGODB-CR&connectTimeoutMS=2000";

    /**
     * Final class: to prevent someone from accidentally instantiating the class, we give it an
     * empty constructor.
     */
    public Variables() {
    }

    /**
     * Hide the keyboard and clear focus
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
}
