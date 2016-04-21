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
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    /**
     *  Final class: to prevent someone from accidentally instantiating the class, we give it an
     *  empty constructor.
     */
    public Variables() {}

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
