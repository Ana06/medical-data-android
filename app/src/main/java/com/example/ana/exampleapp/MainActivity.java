package com.example.ana.exampleapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.Calendar;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;


/**
 * Main activity of the app. It shows a view that creates a {@link RegisterActivity} if it is the
 * first that the app is used. Otherwise a view to introduce the PIN is shown and a
 * {@link TestActivity} is created when the correct PIN is provided.
 *
 * @author Ana María Martínez Gómez
 */
public class MainActivity extends AppCompatActivity {
    //settings, mDbHelper, readable_db and projection are used repeatedly
    SharedPreferences settings;
    SQLiteDatabase readable_db;
    String[] projection = {FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP};
    long startTotalTime = -1;
    long startTime = -1;
    int pin_tries = 0;
    EditText pinEditText;


    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        FeedTestDbHelper mDbHelper = new FeedTestDbHelper(this);
        readable_db = mDbHelper.getReadableDatabase();

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        setMainView();
    }

    @Override
    protected void onRestart() {
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
    private void setMainView() {
        boolean firstTime = settings.getBoolean("firstTime", false);
        if (firstTime) {
            setContentView(R.layout.activity_main_first_time);
        } else {
            setContentView(R.layout.activity_main);

            Cursor c = readable_db.query(
                    FeedTestContract.FeedEntry.TABLE_NAME, projection,
                    null,
                    null,
                    null,
                    null,
                    FeedTestContract.FeedEntry._ID + " DESC", "1");
            boolean moved = c.moveToFirst(); // false if it is empty
            if (moved && FeedTestContract.isToday(c.getString(0))) {
                // Test has already been filled
                Button button = (Button) findViewById(R.id.button_start);
                button.setText(getString(R.string.start_change));
            }

            pinEditText = (EditText) findViewById(R.id.pin);
            TextWatcher tw = new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (startTotalTime == -1 && pinEditText.length() == 1) {
                        startTotalTime = System.nanoTime(); // current timestamp in nanoseconds
                        startTime = System.nanoTime(); // current timestamp in nanoseconds
                    } else if (startTime == -1 && pinEditText.length() == 1) {
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
     * Creates a ticker to remind the user to take their medication
     * at the specified time (epooch time milliseconds)
     * @return
     */
    public NotificationCompat.Builder medicationNotification(long tickerTime){
        NotificationCompat.Builder notif = new NotificationCompat.Builder(this);
        notif.setAutoCancel(false);
        notif.setTicker("Remember to take your daily dose of medication");
        notif.setWhen(tickerTime);
        return notif;
    }

    /**
     * Triggers a notification to remind the user to update their information
     * @return
     */
    public NotificationCompat.Builder updateInfoNotification(){
        NotificationCompat.Builder notif = new NotificationCompat.Builder(this);
        notif.setAutoCancel(false);
        notif.setContentText("Please update your medical information");
        notif.setContentTitle("Information Update Required");
        notif.setWhen(System.currentTimeMillis());
        return notif;
    }

    /**
     * Check if the introduced PIN is correct and in that case saves the time spent to do it and
     * creates a {@link TestActivity}. Otherwise it set an error on the PIN {@link EditText}.
     *
     * @param view the {@link View} that calls the method
     */
    public void btnStart(View view) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];

        inboxStyle.setBigContentTitle("Event tracker details:");

        for (int i=0; i < events.length; i++) {

            //inboxStyle.addLine(events[i]);
            inboxStyle.addLine("String " + i);
        }

        Intent notifIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setStyle(inboxStyle);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

        int pin = settings.getInt("pin", 0);
        String pinText = pinEditText.getText().toString();
        pin_tries++;
        if (!pinText.equals("") && pin == Integer.parseInt(pinText)) {
            //The PIN is correct
            int pin_time = (int) ((System.nanoTime() - startTime) / 1000000); // in milliseconds
            int pin_time_total = (int) ((System.nanoTime() - startTotalTime) / 1000000); // in milliseconds
            Intent intent = new Intent(this, TestActivity.class);
            intent.putExtra("PIN_TIME", pin_time);
            intent.putExtra("PIN_TIME_TOTAL", pin_time_total);
            intent.putExtra("PIN_TRIES", pin_tries);
            startActivity(intent);
        } else {
            startTime = -1;
            pinEditText.setText("");
            pinEditText.setError(getString(R.string.pin_error));
        }
    }

    /**
     * Checks if the email is in the database and the password is correct and, in that case,
     * download the user information from the database and creates a {@link FinishRegisterActivity}
     * to confirm that the sign up process has been completed successfully. It also checks that
     * there is internet connection before trying to connect with the database.If the data
     * introduced is not correct or there is any problem while connecting with the database the user
     * is informed using a {@link Toast} message.
     *
     * @param view the {@link View} clicked
     */
    public void btnSignIn(View view) {
        boolean error = false;
        EditText email = (EditText) findViewById(R.id.email_answer);
        String email_text = email.getText().toString();
        if (email_text.equals("")) {
            email.setError(getString(R.string.email_blank));
            email.requestFocus();
            error = true;
        }
        EditText pin = (EditText) findViewById(R.id.pin_answer);
        String pin_text = pin.getText().toString();
        if (pin_text.equals("")) {
            pin.setError(getString(R.string.pin_blank));
            if (!error) pin.requestFocus();
            error = true;
        }
        if (!error) {
            if (Variables.connection(this) < 0)
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            else {
                int pin_number = Integer.parseInt(pin.getText().toString());
                User user = new User(email_text, pin_number);

                // Request an information update from the user
                updateInfoNotification()
                
                //Save register in the server database
                try {
                    DownloadRegistration runner = new DownloadRegistration();
                    runner.execute(user);
                    int option = runner.get();
                    if (option == 0) {
                        //Save register in the app
                        user.save(this);
                        // Feedback: register has been completed
                        Intent intent = new Intent(this, FinishRegisterActivity.class);
                        startActivity(intent);
                    } else if (option == 1) {
                        Toast.makeText(this, R.string.wrong_data, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.register_error, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, R.string.register_error, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Creates a {@link RegisterActivity} if there is internet connection. Otherwise a {@link Toast}
     * message is showed to informed that internet connection is needed.
     *
     * @param view the {@link View} clicked
     */
    public void btnSignUp(View view) {
        if (Variables.connection(this) < 0)
            Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
        else {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    /**
     * This class is used to check that a user exits in the MongoDB database and download his
     * information from database after signing in.
     *
     * @author Ana María Martínez Gómez
     */
    private class DownloadRegistration extends AsyncTask<User, Void, Integer> {
        @Override
        protected Integer doInBackground(User... params) {
            try {
                MongoClientURI mongoClientURI = new MongoClientURI(Variables.mongo_uri);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                MongoDatabase dbMongo = mongoClient.getDatabase(mongoClientURI.getDatabase());
                MongoCollection<Document> coll = dbMongo.getCollection("users");
                User local_user = params[0];
                Document user = coll.find(eq("email", local_user.getEmail())).first();
                mongoClient.close();
                if (user == null || !(user.get("pin").equals(local_user.getPin()))) {
                    return 1; // Wrong data
                }
                Date d = (Date) user.get("birthDate");
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                // WARNING: Calendar.MONTH starts in 0 Calendar.DAY_OF_MONTH starts in 1
                local_user.completeSignIn((String) user.get("name"), cal.get(Calendar.DAY_OF_MONTH) - 1, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), (Boolean) user.get("gender"), user.getObjectId("_id").toString());
                return 0; //Successfully saved
            } catch (Exception e) {
                return 2; // Error
            }
        }
    }
}
