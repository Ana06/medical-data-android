package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;


/**
 * Activity creates when the register has been finished.
 *
 * @author Ana María Martínez Gómez
 */
public class FinishRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("firstTime", false);
        editor.commit();

        setContentView(R.layout.finish_register_activity);
    }

    /**
     * It finishes the activity.
     *
     * @param view the {@link View} clicked
     * @see #finish()
     */
    public void btnStart(View view) {
        finish();
    }
}
