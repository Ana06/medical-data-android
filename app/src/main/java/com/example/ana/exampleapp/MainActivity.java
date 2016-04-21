package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
public class MainActivity extends AppCompatActivity implements OnFocusChangeListener{
    //settings, mDbHelper, readable_db and projection are used repeatedly
    SharedPreferences settings;
    SQLiteDatabase readable_db;
    String[] projection = {FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP};
    long startTime = -1;
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
        startTime = -1;
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
            pinEditText.setOnFocusChangeListener(this);
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
        if(!pinText.equals("") && pin == Integer.parseInt(pinText)) {
            //The PIN is correct
            long pin_time = (System.nanoTime() - startTime) / 1000000; // in milliseconds
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("PIN_TIME", pin_time);
            startActivity(intent);
        }
        else
            pinEditText.setError(getString(R.string.pin_error));
    }

    /**
     * Creates a {@link RegisterActivity}
     *
     * @param view  the {@link View} clicked
     */
    public void btnRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Creates a {@link CodeRegisterActivity}.
     *
     * @param view  the {@link View} clicked
     */
    public void btnRegisterWithCode(View view) {
        Intent intent = new Intent(this, CodeRegisterActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus && startTime == -1)
            startTime = System.nanoTime(); // current timestamp in nanoseconds
    }
}
