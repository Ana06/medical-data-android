package com.example.ana.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;


/**
 * This activity allows a user to register in the app by asking his name, email, age, gender and a
 * 4 numbers PIN.
 *
 * @author Ana María Martínez Gómez
 */
public class ProfileActivity extends AppCompatActivity {
    String original_email;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        String name = settings.getString("name", "");
        EditText et = (EditText) findViewById(R.id.name_answer);
        et.setText(name);
        original_email = settings.getString("email", "");
        et = (EditText) findViewById(R.id.email_answer);
        et.setText(original_email);
        int birthDay = settings.getInt("birthDay", -1);
        int birthMonth = settings.getInt("birthMonth", -1);
        int birthYear = settings.getInt("birthYear", -1);
        DatePicker dp = (DatePicker) findViewById(R.id.age_answer);
        dp.updateDate(birthYear, birthMonth, birthDay);
        // gender = false => male, gender = true => female
        Boolean gender = settings.getBoolean("gender", true);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
        int radio;
        if (gender)
            radio = R.id.radio_female;
        else
            radio = R.id.radio_male;
        radioGroup.check(radio);
    }

    /**
     * To update the user's personal information. It checks that that the PIN is correct and, in
     * that case, saves the data in the database and the app and finishes. A {@link Toast} message
     * is used to inform the user if the data has been successfully updated or there has been any
     * problem while connecting with the database.
     *
     * @param view the clicked {@link View}.
     * @see #finish()
     * @see TextView#setError(CharSequence)
     * @see EditText#setError(CharSequence)
     */
    public void btnFinish(View view) {
        boolean error = false;

        // check name correction
        EditText name = (EditText) findViewById(R.id.name_answer);
        String name_text = name.getText().toString();
        if (name_text.equals("")) {
            name.setError(getString(R.string.name_blank));
            focusFirstError(name, R.id.name);
            error = true;
        }

        // check email correction
        EditText email = (EditText) findViewById(R.id.email_answer);
        String email_text = email.getText().toString();
        if (email_text.equals("")) {
            email.setError(getString(R.string.email_blank));
            if (!error) {
                focusFirstError(email, R.id.email);
                error = true;
            }
        } else if (!email_text.matches(Variables.emailPattern)) {
            email.setError(getString(R.string.email_format));
            if (!error) {
                focusFirstError(email, R.id.email);
                error = true;
            }
        }

        // check PIN correction
        EditText pin = (EditText) findViewById(R.id.pin_answer);
        String pin_text = pin.getText().toString();
        int pin_number = settings.getInt("pin", -1);
        String original_pin = String.valueOf(pin_number);
        if (!pin_text.equals(original_pin)) {
            pin.setError(getString(R.string.incorrect_pin));
            if (!error) {
                focusFirstError(pin, R.id.pin);
                error = true;
            }
        }

        // ¿Everything correct?
        if (!error) {
            String user_id = settings.getString("user_id", "");
            DatePicker birthDate = (DatePicker) findViewById(R.id.age_answer);
            // gender = false => male, gender = true => female
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
            boolean gender = radioGroup.getCheckedRadioButtonId() == R.id.radio_female;
            User user = new User(
                    email_text,
                    name_text,
                    pin_number,
                    birthDate.getDayOfMonth(),
                    birthDate.getMonth(),
                    birthDate.getYear(),
                    gender,
                    user_id);

            //Save changes in the server database
            try {
                UpdateRegistration runner = new UpdateRegistration();
                runner.execute(user);
                int option = runner.get();
                if (option == 0) {
                    //Save register in the app
                    user.save(this);
                    // Feedback: update profile has been completed
                    Toast.makeText(this, R.string.changes_saved, Toast.LENGTH_LONG).show();
                } else if (option == 1) {
                    Toast.makeText(this, R.string.repeated_email2, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, R.string.update_error, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, R.string.update_error, Toast.LENGTH_LONG).show();
            }

            Variables.hideKeyboard(this);
            finish();
        }
    }

    /**
     * requests focus on a {@link EditText} allowing to see its title too. It is used to set focus
     * on the first question with error.
     *
     * @param et    field we want to set focus in.
     * @param tv_id id of the {@link TextView} which is the title of et.
     */
    private void focusFirstError(EditText et, int tv_id) {
        et.clearFocus(); // requestRectangle does not work properly if et is focused
        et.requestFocus();
        TextView title = (TextView) findViewById(tv_id);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) title.getLayoutParams();
        Rect rect = new Rect(0, -lp.topMargin, title.getWidth(), title.getHeight());
        title.requestRectangleOnScreen(rect);
    }

    /**
     * Eliminate the error on terms and conditions {@link TextView} if the terms and conditions
     * {@link CheckBox} is clicked.
     *
     * @param view
     * @see TextView#setError(CharSequence)
     */
    public void onCheckboxClicked(View view) {
        TextView terms_error = (TextView) findViewById(R.id.terms_text);
        terms_error.setError(null);
    }

    /**
     * {@link ClickableSpan} that allows a part of the terms and conditions String in the view to
     * show a new view with the terms and conditions text. The new view is created with
     * {@link HelpActivity}.
     *
     * @author Ana María Martínez Gómez
     */
    class MyClickableSpan extends ClickableSpan {
        /**
         * Creates a {@link HelpActivity} with the terms and conditions text when textView is
         * clicked.
         *
         * @param textView the {@link View} clicked
         */
        public void onClick(View textView) {
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            intent.putExtra("QUESTION", getString(R.string.terms2));
            intent.putExtra("HELP", getString(R.string.terms_text));
            startActivity(intent);
        }

        /**
         * Change the style of the object: blue and not underline
         *
         * @param ds TextPaint with the style of the object
         */
        public void updateDrawState(TextPaint ds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ds.setColor(getResources().getColor(R.color.colorSecondary, null));//set text color
            } else {
                ds.setColor(getResources().getColor(R.color.colorSecondary));//set text color
            }
            ds.setUnderlineText(false); // set to false to remove underline
        }
    }

    /**
     * This class is used to update the user's personal data to the MongoDB database.
     *
     * @author Ana María Martínez Gómez
     */
    private class UpdateRegistration extends AsyncTask<User, Void, Integer> {
        @Override
        protected Integer doInBackground(User... params) {
            try {
                MongoClientURI mongoClientURI = new MongoClientURI(Variables.mongo_uri);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                MongoDatabase dbMongo = mongoClient.getDatabase(mongoClientURI.getDatabase());
                MongoCollection<Document> coll = dbMongo.getCollection("users");
                User local_user = params[0];
                if (!local_user.getEmail().equals(original_email)) {
                    Document user = coll.find(eq("email", local_user.getEmail())).first();
                    if (user != null) {
                        return 1; // Repeated email
                    }
                }
                coll.updateOne(new Document("_id", new ObjectId(local_user.getId())), new Document("$set", local_user.getRegisterDocument()));
                mongoClient.close();
                return 0; //Successfully saved
            } catch (Exception e) {
                return 2; // Error
            }
        }
    }

}
