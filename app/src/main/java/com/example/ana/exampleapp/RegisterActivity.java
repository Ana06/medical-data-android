package com.example.ana.exampleapp;

import java.lang.String;

import android.support.v7.app.AppCompatActivity;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;


import org.bson.Document;

/**
 * This activity allows a user to register in the app by asking his name, email, age, gender and a
 * 6 numbers PIN.
 *
 * @author Ana María Martínez Gómez
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        //allow a part of the terms and conditions text to be clickable.
        TextView tv = (TextView) findViewById(R.id.terms_text);
        String terms1 = getString(R.string.terms1);
        String terms2 = getString(R.string.terms2);
        SpannableString ss = new SpannableString(terms1 + " " + terms2);
        ss.setSpan(new MyClickableSpan(), terms1.length(), terms1.length() + terms2.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//0 to 7 Android is clickable
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());

        DatePicker birthDate = (DatePicker) findViewById(R.id.age_answer);
        birthDate.updateDate(1985, 9, 4);
    }

    /**
     * To finish the register, saving the personal information and the PIN provided. It checks that
     * all question have been answered and the terms and conditions agreed. In that case, it saves
     * them, creates a {@link FinishRegisterActivity} and finish. Otherwise, it sets errors for all
     * questions that haven't been answered or have been answered incorrectly and set focus on the
     * first one with error. It also checks that there is internet connection before trying to
     * connect with the database.
     *
     * @param view the clicked {@link View}.
     * @see #finish()
     * @see TextView#setError(CharSequence)
     * @see EditText#setError(CharSequence)
     */
    public void btnFinish(View view) {
        String email_text;
        String name_text;
        int pin_number = 0;
        int birth_day; // Range: 0-30
        int birth_month; // Range: 0-11
        int birth_year;
        boolean gender;
        boolean error = false;

        // check name correction
        EditText name = (EditText) findViewById(R.id.name_answer);
        name_text = name.getText().toString();
        if (name_text.equals("")) {
            name.setError(getString(R.string.name_blank));
            focusFirstError(name, R.id.name);
            error = true;
        }

        // check email correction
        EditText email = (EditText) findViewById(R.id.email_answer);
        email_text = email.getText().toString();
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
        EditText pin2 = (EditText) findViewById(R.id.pin2_answer);
        String pin_text = pin.getText().toString();
        if (pin_text.length() != 6) {
            pin.setError(getString(R.string.pin_format));
            if (!error) {
                focusFirstError(pin, R.id.pin);
                error = true;
            }
        } else {
            // PIN2 correction is only checked if PIN is correct
            String pin2_text = pin2.getText().toString();
            if (!pin_text.equals(pin2_text)) {
                pin2.setError(getString(R.string.pin2_format));
                if (!error) {
                    focusFirstError(pin2, R.id.pin2);
                    error = true;
                }
            } else
                pin_number = Integer.parseInt(pin_text);
        }

        // terms accepted
        CheckBox terms = (CheckBox) findViewById(R.id.terms_check);
        if (!terms.isChecked()) {
            TextView terms_error = (TextView) findViewById(R.id.terms_text);
            terms_error.setError("");
            if (!error) {
                Variables.hideKeyboard(this);
                error = true;
            }
        }

        // ¿Everything correct?
        if (!error) {
            if (Variables.connection(this) < 0)
                Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
            else {
                DatePicker birthDate = (DatePicker) findViewById(R.id.age_answer);
                birth_day = birthDate.getDayOfMonth();
                birth_month = birthDate.getMonth();
                birth_year = birthDate.getYear();
                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
                // gender = false => male, gender = true => female
                gender = radioGroup.getCheckedRadioButtonId() == R.id.radio_female;


                //Save register in the server database
                try {
                    User user = new User(email_text, name_text, pin_number, birth_day, birth_month, birth_year, gender);
                    SendRegistration runner = new SendRegistration();
                    runner.execute(user);
                    int option = runner.get();
                    if (option == 0) {
                        //Save register in the app
                        user.save(this);
                        // Feedback: register has been completed
                        Intent intent = new Intent(this, FinishRegisterActivity.class);
                        startActivity(intent);
                    } else if (option == 1) {
                        Toast.makeText(this, R.string.repeated_email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, R.string.register_error, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, R.string.register_error, Toast.LENGTH_LONG).show();
                }
                Variables.hideKeyboard(this);
                finish();
            }
        }
    }

    /**
     * requests focus on a {@link EditText} allowing to see its title too. It is used to set focus on the
     * first question with error.
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
            Intent intent = new Intent(RegisterActivity.this, HelpActivity.class);
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
                ds.setColor(getResources().getColor(R.color.colorAccent, null));//set text color
            } else {
                ds.setColor(getResources().getColor(R.color.colorAccent));//set text color
            }
            ds.setUnderlineText(false); // set to false to remove underline
        }
    }

    /**
     * This class is used to send the user's personal data to the MongoDB database after signing up.
     *
     * @author Ana María Martínez Gómez
     */
    private class SendRegistration extends AsyncTask<User, Void, Integer> {
        @Override
        protected Integer doInBackground(User... params) {
            try {
                MongoClientURI mongoClientURI = new MongoClientURI(Variables.mongo_uri);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                MongoDatabase dbMongo = mongoClient.getDatabase(mongoClientURI.getDatabase());
                MongoCollection<Document> coll = dbMongo.getCollection("users");
                User local_user = params[0];
                if (coll.find(eq("email", local_user.getEmail())).first() != null) {
                    mongoClient.close();
                    return 1; // Repeated email
                }
                Document document = local_user.getRegisterDocument();
                coll.insertOne(document);
                local_user.setId(document.getObjectId("_id").toString());
                mongoClient.close();
                return 0; //Successfully saved
            } catch (Exception e) {
                return 2; // Error
            }
        }
    }

}
