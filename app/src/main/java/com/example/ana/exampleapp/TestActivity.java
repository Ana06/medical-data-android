package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.ContentValues;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.graphics.Rect;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Text;

/**
 * Class that manages the diary text, whose recollection is the main aim of the app.
 *
 * @author Ana María Martínez Gómez
 */
public class TestActivity extends AppCompatActivity {
    /*rating stars: no_value = 10. questions 1 and 2 = -3 to 3. questions 3 to 6 = 1 to 5
    radio group: no = 0, si = 1, not_checked = -1 (menstruation 0 by default - men haven't got
                 menstruation)
    text field: no_value = -1
    time fields: in minutes, non_value = -1 */
    private int[] questions = new int[]{10,10,10,10,10,10,0,-1,-1,-1,-1,-1,-1};
    long pin_time;
    private boolean repeating_test;
    private boolean isFemale;
    private FeedTestDbHelper mDbHelper;
    private final static String[] projection = {
            FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q1,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q2,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q3,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q4,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q5,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q6,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q7,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q8,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q9,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q10,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q11,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q12,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q13
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        pin_time = intent.getLongExtra("PIN_TIME", 0);

        setContentView(R.layout.test_activity);

        TimePicker tp11 = (TimePicker) findViewById(R.id.question11_rating);
        TimePicker tp12 = (TimePicker) findViewById(R.id.question12_rating);
        TimePicker tp13 = (TimePicker) findViewById(R.id.question13_rating);
        tp11.setIs24HourView(true);
        tp12.setIs24HourView(true);
        tp13.setIs24HourView(true);

        SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        isFemale = settings.getBoolean("gender", true);
        if(!isFemale){ //the user is a man
            RelativeLayout gender_layout = (RelativeLayout) findViewById(R.id.question7_layout);
            gender_layout.setVisibility(View.GONE);
        }

        mDbHelper = new FeedTestDbHelper(this);
        SQLiteDatabase readable_db = mDbHelper.getReadableDatabase();
        Cursor c = readable_db.query(
                FeedTestContract.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                FeedTestContract.FeedEntry._ID +" DESC", "2");
        boolean has_test = c.moveToFirst(); //has_test = false if it is empty
        if(has_test) {
            RatingStars r1 = (RatingStars) findViewById(R.id.question1_rating);
            RatingStars r2 = (RatingStars) findViewById(R.id.question2_rating);
            RatingStars r3 = (RatingStars) findViewById(R.id.question3_rating);
            RatingStars r4 = (RatingStars) findViewById(R.id.question4_rating);
            RatingStars r5 = (RatingStars) findViewById(R.id.question5_rating);
            RatingStars r6 = (RatingStars) findViewById(R.id.question6_rating);

            repeating_test = FeedTestContract.isToday(c.getString(0));
            if(repeating_test){ // Test has already been filled
                r1.setAnswer(c.getInt(1));
                r2.setAnswer(c.getInt(2));
                r3.setAnswer(c.getInt(3));
                r4.setAnswer(c.getInt(4));
                r5.setAnswer(c.getInt(5));
                r6.setAnswer(c.getInt(6));

                RadioGroup r7= (RadioGroup) findViewById(R.id.question7_rating);
                ((RadioButton)r7.getChildAt(c.getInt(7))).setChecked(true);

                EditText r8 = (EditText) findViewById(R.id.question8_rating);
                EditText r9 = (EditText) findViewById(R.id.question9_rating);
                r8.setText(String.valueOf(c.getInt(8)));
                r9.setText(String.valueOf(c.getInt(9)));

                RadioGroup r10= (RadioGroup) findViewById(R.id.question10_rating);
                ((RadioButton)r10.getChildAt(c.getInt(10))).setChecked(true);

                //Taking into account deprecated methods
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
                    tp11.setHour(c.getInt(11)/60); tp11.setMinute(c.getInt(11)%60);
                    tp12.setHour(c.getInt(12)/60); tp12.setMinute(c.getInt(12)%60);
                    tp13.setHour(c.getInt(13)/60); tp13.setMinute(c.getInt(13)%60);
                } else{
                    tp11.setCurrentHour(c.getInt(11)/60); tp11.setCurrentMinute(c.getInt(11)%60);
                    tp12.setCurrentHour(c.getInt(12)/60); tp12.setCurrentMinute(c.getInt(12)%60);
                    tp13.setCurrentHour(c.getInt(13)/60); tp13.setCurrentMinute(c.getInt(13)%60);
                }

                has_test = c.moveToNext(); //has_test = false if it only has one row
            }
            if(has_test){ // Previous day
                r1.setPink(c.getInt(1));
                r2.setPink(c.getInt(2));
                r3.setPink(c.getInt(3));
                r4.setPink(c.getInt(4));
                r5.setPink(c.getInt(5));
                r6.setPink(c.getInt(6));
            }
            r1.updateColor();
            r2.updateColor();
            r3.updateColor();
            r4.updateColor();
            r5.updateColor();
            r6.updateColor();
        }
    }

    /**
     * Creates a {@link HelpActivity} providing it a question and a help text
     *
     * @param view  the clicked {@link View}. Expected to be help1, help2, help3, help4, help5,
     *              help6, help7, help8, help9, help10, help11, help12 or help13
     */
    public void btnHelp(View view) {
        String help="", question="";
        switch(view.getId()){
            case R.id.question1_help:       question = getString(R.string.question1);
                                            help = getString(R.string.help1);
                                            break;
            case R.id.question2_help:       question = getString(R.string.question2);
                                            help = getString(R.string.help2);
                                            break;
            case R.id.question3_help:       question = getString(R.string.question3);
                                            help = getString(R.string.help3);
                                            break;
            case R.id.question4_help:       question = getString(R.string.question4);
                                            help = getString(R.string.help4);
                                            break;
            case R.id.question5_help:       question = getString(R.string.question5);
                                            help = getString(R.string.help5);
                                            break;
            case R.id.question6_help:       question = getString(R.string.question6);
                                            help = getString(R.string.help6);
                                            break;
            case R.id.question7_help:       question = getString(R.string.question7);
                                            help = getString(R.string.help7);
                                            break;
            case R.id.question8_help:       question = getString(R.string.question8);
                                            help = getString(R.string.help8);
                                            break;
            case R.id.question9_help:       question = getString(R.string.question9);
                                            help = getString(R.string.help9);
                                            break;
            case R.id.question10_help:      question = getString(R.string.question10);
                                            help = getString(R.string.help10);
                                            break;
            case R.id.question11_help:      question = getString(R.string.question11);
                                            help = getString(R.string.help11);
                                            break;
            case R.id.question12_help:      question = getString(R.string.question12);
                                            help = getString(R.string.help12);
                                            break;
            case R.id.question13_help:      question = getString(R.string.question13);
                                            help = getString(R.string.help13);
                                            break;
        }
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("HELP", help);
        intent.putExtra("QUESTION", question);
        startActivity(intent);
    }

    /**
     * To finish the text, storing the answers in the database. It checks that all question have
     * been answered (except menstruation if it is a man). In that case, it stores them, deleting
     * the previous test from the database if the test has already been filled and set a
     * confirmation view. Otherwise, it sets errors for all questions that haven't been answered and
     * put the first one with error on screen.
     *
     * @param view  the clicked {@link View}.
     * @see TextView#setError(CharSequence)
     * @see TextView#requestRectangleOnScreen(Rect)
     */
    public void btnFinish(View view) {
        Variables.hideKeyboard(this);
        TextView error = null;
        TextView tv;

        // Check the questions 1 to 6
        error = RatingStarsAnswered(0, R.id.question1_rating, R.id.question1, error);
        error = RatingStarsAnswered(1, R.id.question2_rating, R.id.question2, error);
        error = RatingStarsAnswered(2, R.id.question3_rating, R.id.question3, error);
        error = RatingStarsAnswered(3, R.id.question4_rating, R.id.question4, error);
        error = RatingStarsAnswered(4, R.id.question5_rating, R.id.question5, error);
        error = RatingStarsAnswered(5, R.id.question6_rating, R.id.question6, error);

        // Check question 7 (RadioGroup)
        if(isFemale) {
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.question7_rating);
            int id = radioGroup.getCheckedRadioButtonId();
            tv = (TextView) findViewById(R.id.question7);
            if (id == -1) {
                tv.setError("");
                if(error == null) error = tv;
            } else {
                tv.setError(null);
                if (id == R.id.question7_rating_no) questions[6] = 0;
                else questions[6] = 1;
            }
        }

        boolean keyboard_needed = false;
        // Check question 8 (EditText)
        EditText field = (EditText) findViewById(R.id.question8_rating);
        String value = field.getText().toString();
        tv = (TextView) findViewById(R.id.question8);
        if(value.equals("")){
            tv.setError("");
            if(error == null){
                field.requestFocus();
                keyboard_needed = true;
                error = tv;
            }
        }
        else{
            questions[7] = Integer.parseInt(value);
            tv.setError(null);
        }

        // Check question 9 (EditText)
        field = (EditText) findViewById(R.id.question9_rating);
        value = field.getText().toString();
        tv = (TextView) findViewById(R.id.question9);
        if(value.equals("")){
            tv.setError("");
            if(error == null){
                field.requestFocus();
                keyboard_needed = true;
                error = tv;
            }
        }
        else{
            questions[8] = Byte.parseByte(value);
            tv.setError(null);
        }

        // Check question 10 (RadioGroup)
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.question10_rating);
        int id = radioGroup.getCheckedRadioButtonId();
        tv = (TextView) findViewById(R.id.question10);
        if (id == -1){
            tv.setError("");
            if(error == null) error = tv;
        }
        else{
            tv.setError(null);
            if (id == R.id.question10_rating_no) questions[9] = 0;
            else questions[9] = 1;
        }

        if(error == null) {
            // Get times from TimePickers in minutes.
            TimePicker tp11 = (TimePicker) findViewById(R.id.question11_rating);
            TimePicker tp12 = (TimePicker) findViewById(R.id.question12_rating);
            TimePicker tp13 = (TimePicker) findViewById(R.id.question13_rating);
            // Taking into account deprecated methods
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
                questions[10] = tp11.getHour()*60 + tp11.getMinute();
                questions[11] = tp12.getHour()*60 + tp12.getMinute();
                questions[12] = tp13.getHour()*60 + tp13.getMinute();
            } else {
                questions[10] = tp11.getCurrentHour()*60 + tp11.getCurrentMinute();
                questions[11] = tp12.getCurrentHour()*60 + tp12.getCurrentMinute();
                questions[12] = tp13.getCurrentHour()*60 + tp13.getCurrentMinute();
            }

            // Save the test in the database
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedTestContract.FeedEntry.COLUMN_NAME_PIN, pin_time);
            for(int i=0; i<questions.length; i++)
                values.put(FeedTestContract.QUESTION_COLUMNS_NAMES[i], questions[i]);
            if(repeating_test){
                //If test has already been filled, we delete the last entry from the database
                String selection = "_ID = (SELECT MAX(_ID) FROM " +  FeedTestContract.FeedEntry.TABLE_NAME + ")";
                db.delete(FeedTestContract.FeedEntry.TABLE_NAME, selection,null);
            }
            db.insert(
                    FeedTestContract.FeedEntry.TABLE_NAME,
                    null,
                    values);

            setContentView(R.layout.finish_activity);
        }
        else{
            // Show a message to indicate that the test can't be sent.
            TextView errors = (TextView) findViewById(R.id.errors);
            errors.setText(getString(R.string.test_error));
            // Put the first question without answer on screen
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) error.getLayoutParams();
            Rect rect = new Rect(0, -lp.topMargin, error.getWidth(), error.getHeight());
            error.requestRectangleOnScreen(rect);
        }
    }

    /**
     * Auxiliar function to get questions 1 to 6 value and set an error if they have not been
     * answered.
     *
     * @param i         Number of question
     * @param rating    {@link RatingStars} id
     * @param text      {@link TextView} id of the question title
     * @param error     {@link TextView} if of the first question title whose question has an error
     * @return          <code>true</code> if an error was set; <code>false</code> otherwise.
     */
    private TextView RatingStarsAnswered(int i, int rating, int text, TextView error) {
        RatingStars r = (RatingStars) findViewById(rating);
        questions[i] = r.getAnswer();
        TextView tv = (TextView) findViewById(text);
        if(questions[i] == 10){ // default value is 10
            tv.setError("");
            if(error == null) return tv;
        }
        else{
            tv.setError(null);
        }
        return error;
    }

    /**
     * It finishes the activity.
     *
     * @param view  the {@link View} clicked
     * @see #finish()
     */
    public void btnChange(View view) {
        finish();
    }
}
