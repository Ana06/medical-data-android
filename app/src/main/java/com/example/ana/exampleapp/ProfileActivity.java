package com.example.ana.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity allows a user to register in the app by asking his name, email, age, gender and a
 * 4 numbers PIN.
 *
 * @author Ana María Martínez Gómez
 */
public class ProfileActivity extends AppCompatActivity {
    String name;
    String email;
    int age;
    boolean gender;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        name = settings.getString("name", "");
        EditText et = (EditText) findViewById(R.id.name_answer);
        et.setText(name);
        email = settings.getString("email", "");
        et = (EditText) findViewById(R.id.email_answer);
        et.setText(email);
        age = settings.getInt("age", -1);
        et = (EditText) findViewById(R.id.age_answer);
        et.setText(String.valueOf(age));
        // gender = false => male, gender = true => female
        gender = settings.getBoolean("gender", true);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
        int radio;
        if (gender)
            radio = R.id.radio_female;
        else
            radio = R.id.radio_male;
        radioGroup.check(radio);
    }

    /**
     * To finish the register, updating the personal information. It checks that all question have
     * an answer and that the PIN is corrected. In that case, it saves them, creates a and finishes.
     * Otherwise, it sets errors for all questions that haven't been answered or have been answered
     * incorrectly and set focus on the first one with error.
     *
     * @param view  the clicked {@link View}.
     * @see #finish()
     * @see TextView#setError(CharSequence)
     * @see EditText#setError(CharSequence)
     */
    public void btnFinish(View view) {
        boolean error = false;

        // check name correction
        EditText name = (EditText) findViewById(R.id.name_answer);
        String name_text = name.getText().toString();
        if(name_text.equals("")){
            name.setError(getString(R.string.name_blank));
            focusFirstError(name, R.id.name);
            error = true;
        }

        // check email correction
        EditText email = (EditText) findViewById(R.id.email_answer);
        String email_text = email.getText().toString();
        if(email_text.equals("")){
            email.setError(getString(R.string.email_blank));
            if(!error) {
                focusFirstError(email, R.id.email);
                error = true;
            }
        }
        else if (!email_text.matches(Variables.emailPattern)){
            email.setError(getString(R.string.email_format));
            if(!error) {
                focusFirstError(email, R.id.email);
                error = true;
            }
        }

        // check age correction
        EditText age = (EditText) findViewById(R.id.age_answer);
        if(age.getText().toString().equals("")) {
            age.setError(getString(R.string.age_blank));
            if(!error) {
                focusFirstError(age, R.id.age);
                error = true;
            }
        }

        // check PIN correction
        EditText pin = (EditText) findViewById(R.id.pin_answer);
        String pin_text = pin.getText().toString();
        String original_pin = String.valueOf(settings.getInt("pin", -1));
        if (!pin_text.equals(original_pin)) {
            pin.setError(getString(R.string.incorrect_pin));
            if(!error) {
                focusFirstError(pin, R.id.pin);
                error = true;
            }
        }

        // ¿Everything correct?
        if (!error) {
            // Save register
            SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("name", name_text);
            editor.putString("email", email_text);
            editor.putInt("age", Integer.valueOf(pin_text));
            // gender = false => male, gender = true => female
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
            editor.putBoolean("gender", radioGroup.getCheckedRadioButtonId() == R.id.radio_female);
            editor.commit();

            Variables.hideKeyboard(this);
            Toast.makeText(this, R.string.changes_saved, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * requests focus on a {@link EditText} allowing to see its title too. It is used to set focus on the
     * first question with error.
     *
     * @param et        field we want to set focus in.
     * @param tv_id     id of the {@link TextView} which is the title of et.
     */
    private void focusFirstError(EditText et, int tv_id){
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
         * @param textView  the {@link View} clicked
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
         * @param ds    TextPaint with the style of the object
         */
        public void updateDrawState(TextPaint ds) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ds.setColor(getResources().getColor(R.color.colorSecondary,null));//set text color
            } else {
                ds.setColor(getResources().getColor(R.color.colorSecondary));//set text color
            }
            ds.setUnderlineText(false); // set to false to remove underline
        }
    }

}
