package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Main activity of the app. It shows a view that creates a {@link RegisterActivity} if it is the
 * first that the app is used. Otherwise a view to introduce the PIN is shown and a
 * {@link TestActivity} is created when the corrected PIN is provided.
 *
 * @author Ana María Martínez Gómez
 */
public class MainActivity extends AppCompatActivity {
    //settings, mDbHelper, readable_db and projection are used repeatedly
    SharedPreferences settings;
    SQLiteDatabase readable_db;
    String[] projection = {FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initialization of shared preferences, mDbHelper
        settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        FeedTestDbHelper mDbHelper = new FeedTestDbHelper(this);
        readable_db = mDbHelper.getReadableDatabase();
        setMainView();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        setMainView();
    }

    /**
     * Set the appropriate view taking into account if it is the first time that app is used and
     * the database last entry.
     */
    private void setMainView(){
        boolean firstTime = settings.getBoolean("firstTime", true);
        if(firstTime) {
            setContentView(R.layout.activity_main_first_time);
        }
        else {
            setContentView(R.layout.activity_main);

            Cursor c = readable_db.query(
                    FeedTestContract.FeedEntry.TABLE_NAME, projection,
                    null,
                    null,
                    null,
                    null,
                    FeedTestContract.FeedEntry._ID + " DESC", "1");
            boolean moved = c.moveToFirst(); //false if it is empty
            if(moved && FeedTestContract.isToday(c.getString(0))){
                // Test has already been filled
                Button button = (Button) findViewById(R.id.button_start);
                button.setText(getString(R.string.start_change));
            }
        }
    }

    /**
     * Check if the introduced PIN is correct and in that case creates a {@link TestActivity}.
     * Otherwise it set an error on the PIN {@link EditText}.
     *
     * @param view  the {@link View} that calls the method
     */
    public void btnStart(View view) {
        int pin = settings.getInt("pin", 0);
        EditText pinEditText = (EditText) findViewById(R.id.pin);
        String pinText = pinEditText.getText().toString();
        if(!pinText.equals("") && pin == Integer.parseInt(pinText)) {
            //The PIN is correct
            Intent intent = new Intent(this, TestActivity.class);
            startActivity(intent);
        }
        else
            pinEditText.setError(getString(R.string.pin_error));
    }

    /**
     * Creates a {@link RegisterActivity}
     *
     * @param view  the {@link View} that calls the method
     */
    public void btnRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Creates a {@link RegisterActivity} specifying that the user has got a code.
     *
     * @param view  the {@link View} that calls the method
     */
    public void btnRegisterWithCode(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("WITH_CODE", true);
        startActivity(intent);
    }
}
