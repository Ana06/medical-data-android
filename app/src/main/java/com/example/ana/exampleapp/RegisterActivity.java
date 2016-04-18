package com.example.ana.exampleapp;

import java.lang.String;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Build;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;

/**
 * This activity allows a user to register in the app by asking his name, email, age, gender and a
 * 4 numbers PIN. There are two different cases: when the users has got a code given by it doctor
 * and when he/she hasn't got it.
 *
 * @author Ana María Martínez Gómez
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Boolean withCode = intent.getBooleanExtra("WITH_CODE", false);
        if(withCode)
            setContentView(R.layout.register_with_code_activity);
        else {
            setRegisterForm();
        }
    }

    /**
     * Set the content view of the register form after introducing the code given by te user's
     * doctor.
     *
     * @param view  the {@link View} clicked
     */
    public void btnRegister(View view) {
        setRegisterForm();
    }

    /**
     * Set the content view of the register form. It also allows the a part of a {@link String} (the
     * terms and conditions) to be clickable.
     *
     * @see SpannableString
     * @see MyClickableSpan
     */
    private void setRegisterForm(){
        setContentView(R.layout.register_activity);
        TextView tv = (TextView) findViewById(R.id.terms_text);
        String terms1 = getString(R.string.terms1);
        String terms2 = getString(R.string.terms2);
        SpannableString ss = new SpannableString(terms1+ " " + terms2);
        ss.setSpan(new MyClickableSpan(), terms1.length(), terms1.length()+terms2.length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//0 to 7 Android is clickable
        tv.setText(ss);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    /**
     * To finish the register, saving the personal information and the PIN provided. It checks that
     * all question have been answered and the terms and conditions agreed. In that case, it saves
     * them, creates a {@link FinishRegisterActivity} and finish. Otherwise, it sets errors for all
     * questions that haven't been answered or have been answered incorrectly and set focus on the
     * first one with error.
     *
     * @param view  the clicked {@link View}.
     * @see #finish()
     * @see TextView#setError(CharSequence)
     * @see EditText#setError(CharSequence)
     */
    public void btnFinish(View view) {
        EditText first_with_error = null;

        // check name correction
        EditText name = (EditText) findViewById(R.id.name_answer);
        String name_text = name.getText().toString();
        if(name_text.equals("")){
            name.setError(getString(R.string.name_blank));
            first_with_error = name;
        }

        // check email correction
        EditText email = (EditText) findViewById(R.id.email_answer);
        String email_text = email.getText().toString();
        if(email_text.equals("")){
            email.setError(getString(R.string.email_blank));
            if(first_with_error == null) {
                first_with_error = email;
            }
        }
        else if (!email_text.matches(Variables.emailPattern)){
            email.setError(getString(R.string.email_format));
            if(first_with_error == null) {
                first_with_error = email;
            }
        }

        // check age correction
        EditText age = (EditText) findViewById(R.id.age_answer);
        if(age.getText().toString().equals("")) {
            age.setError(getString(R.string.age_blank));
            if (first_with_error == null) {
                first_with_error = age;
            }
        }

        // check PIN correction
        EditText pin = (EditText) findViewById(R.id.pin_answer);
        EditText pin2 = (EditText) findViewById(R.id.pin2_answer);
        String pin_text = pin.getText().toString();
        if (pin_text.length() != 4) {
            pin.setError(getString(R.string.pin_format));
            if (first_with_error == null) {
                first_with_error = pin;
            }
        } else {
            // PIN2 correction is only checked if PIN is correct
            String pin2_text = pin2.getText().toString();
            if (!pin_text.equals(pin2_text)) {
                pin2.setError(getString(R.string.pin2_format));
                if (first_with_error == null) {
                    first_with_error = pin2;
                }
            }
        }

        // terms accepted
        CheckBox terms = (CheckBox) findViewById(R.id.terms_check);
        if(!terms.isChecked()) {
            TextView terms_error = (TextView) findViewById(R.id.terms_text);
            terms_error.setError("");
            if (first_with_error == null) {
                first_with_error = pin2;
            }
        }

        // ¿Everything correct?
        if (first_with_error == null) {
            // Save register
            SharedPreferences settings = getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            RadioGroup radioGroup = (RadioGroup) findViewById(R.id.gender_answer);
            editor.putString("name", name_text);
            editor.putString("email", email_text);
            editor.putInt("age", Integer.valueOf(pin_text));
            // gender = false => male, gender = true => female
            editor.putBoolean("gender", radioGroup.getCheckedRadioButtonId() == R.id.radio_female);
            editor.putInt("pin", Integer.parseInt(pin_text));
            editor.commit();

            Intent intent = new Intent(this, FinishRegisterActivity.class);
            startActivity(intent);
            finish();
        } else
            first_with_error.requestFocus();
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
            Intent intent = new Intent(RegisterActivity.this, HelpActivity.class);
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
