package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
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
public class MainActivity extends AppCompatActivity{
    //settings, mDbHelper, readable_db and projection are used repeatedly
    SharedPreferences settings;
    SQLiteDatabase readable_db;
    String[] projection = {FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP};
    long startTotalTime = -1;
    long startTime = -1;
    int pin_tries = 0;
    EditText pinEditText;

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
        startTotalTime = -1;
        startTime = -1;
        pin_tries = 0;
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

            pinEditText = (EditText) findViewById(R.id.pin);
            TextWatcher tw = new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(startTotalTime == -1 && pinEditText.length() == 1) {
                        startTotalTime = System.nanoTime(); // current timestamp in nanoseconds
                        startTime = System.nanoTime(); // current timestamp in nanoseconds
                    }
                    else if(startTime == -1 && pinEditText.length() == 1){
                        startTime = System.nanoTime(); // current timestamp in nanoseconds
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //pinEditText.setError(null);
                }
            };
            pinEditText.addTextChangedListener(tw);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        boolean firstTime = settings.getBoolean("firstTime", true);
        menu.findItem(R.id.profile).setVisible(!firstTime);
        menu.findItem(R.id.profile).setEnabled(!firstTime);
        menu.findItem(R.id.configuration).setVisible(!firstTime);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it isn't the first time the app is
        used
         */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.profile:
                intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.configuration:
                intent = new Intent(this, ConfigurationActivity.class);
                startActivity(intent);
                return true;
            case R.id.information:
                intent = new Intent(this, InformationActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Check if the introduced PIN is correct and in that case saves the time spent to do it and
     * creates a {@link TestActivity}. Otherwise it set an error on the PIN {@link EditText}.
     *
     * @param view  the {@link View} that calls the method
     */
    public void btnStart(View view) {
        int pin = settings.getInt("pin", 0);
        String pinText = pinEditText.getText().toString();
        pin_tries ++;
        if(!pinText.equals("") && pin == Integer.parseInt(pinText)) {
            //The PIN is correct
            long pin_time = (System.nanoTime() - startTime) / 1000000; // in milliseconds
            long pin_time_total = (System.nanoTime() - startTotalTime) / 1000000; // in milliseconds
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("PIN_TIME", pin_time);
            intent.putExtra("PIN_TIME_TOTAL", pin_time_total);
            intent.putExtra("PIN_TRIES", pin_tries);
            startActivity(intent);
        }
        else {
            startTime = -1;
            pinEditText.setText("");
            pinEditText.setError(getString(R.string.pin_error));
        }
    }

    /**
     * Check if the email is in the database and the password is correct and, in that case,
     * download the user information from the database and allow him/her to star the daily test.
     *
     * @param view  the {@link View} clicked
     */
    public void btnSignIn(View view) {
        //In process
    }

    /**
     * Creates a {@link RegisterActivity}
     *
     * @param view  the {@link View} clicked
     */
    public void btnSignUp(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}
