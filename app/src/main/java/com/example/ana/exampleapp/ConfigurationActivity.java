package com.example.ana.exampleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;


/**
 * It set a configuration view which contain a list of permissions with a checkbox each to enable or
 * disable
 *
 * @author Ana María Martínez Gómez
 */
public class ConfigurationActivity extends AppCompatActivity {
    private boolean isChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_activity);
        SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        isChecked = !(settings.getBoolean("only_wifi", false));
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_id);
        checkBox.setChecked(isChecked);
    }

    /**
     * Called when the {@link CheckBox} is clicked to enable or disable the only wifi option.
     *
     * @param checkBox The {@link CheckBox} clicked.
     */
    public void onCheckBoxClick(View checkBox) {
        boolean isCheckedNow = ((CheckBox) checkBox).isChecked();
        if (isCheckedNow != isChecked) {
            SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("only_wifi", !isCheckedNow);
            editor.commit();
            isChecked = isCheckedNow;
            Log.v("ConfigurationActivity:", "Only wifi: " + !isCheckedNow);
        }
    }

}