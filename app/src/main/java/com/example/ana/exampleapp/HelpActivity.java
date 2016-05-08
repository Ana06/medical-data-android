package com.example.ana.exampleapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;


/**
 * It set a help view with a given question and help.
 *
 * @author Ana María Martínez Gómez
 */
public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.help_activity);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(intent.getStringExtra("QUESTION"));
        TextView help_description = (TextView) findViewById(R.id.help_description);
        help_description.setText(intent.getStringExtra("HELP"));
    }

}