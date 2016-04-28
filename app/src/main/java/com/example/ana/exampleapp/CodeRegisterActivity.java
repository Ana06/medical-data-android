package com.example.ana.exampleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

/**
 * This activity allows the user to introduce the code given by his/her doctor and creates a
 * {@link RegisterActivity}.
 *
 * @author Ana María Martínez Gómez
 */
public class CodeRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.register_with_code_activity);
    }

    /**
     * Creates a {@link RegisterActivity} and finishes.
     *
     * @param view  the {@link View} clicked
     * @see #finish()
     */
    public void btnRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }




}
