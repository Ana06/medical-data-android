package com.example.ana.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


/**
 * It set a view with the following information about the app: name, icon, version, developer name,
 * and a link to terms and conditions (by creating a {@link HelpActivity}). It is not allowed to
 * remove the author(s) name(s), but you can add new authors.
 *
 * @author Ana María Martínez Gómez
 */
public class InformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_activity);
    }

    /**
     * Creates a {@link HelpActivity} with the terms and conditions text when view is clicked.
     *
     * @param view the {@link View} clicked
     */
    public void seeTerms(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra("QUESTION", getString(R.string.terms2));
        intent.putExtra("HELP", getString(R.string.terms_text));
        startActivity(intent);
    }

}