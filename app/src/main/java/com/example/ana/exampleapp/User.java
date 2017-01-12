package com.example.ana.exampleapp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.bson.Document;


/**
 * Class to handle the sign in or sign up. It contains all the necessary data from users and the
 * functions needed to interact properly with this data.
 *
 * @author Ana María Martínez Gómez
 */
public class User {
    private String email_text;
    private String name_text;
    private int pin_number;
    private int birth_day; // Range: 0-30
    private int birth_month; // Range: 0-11
    private int birth_year;
    private boolean gender;
    private String user_id;

    /**
     * Constructor used to create a user when signing in. Only the email and pin are needed to sign
     * in.
     *
     * @param email_text user's email
     * @param pin_number pin
     */
    public User(String email_text, int pin_number) {
        this.email_text = email_text;
        this.pin_number = pin_number;
    }

    /**
     * Constructor used to create a user when signing up. The email, name, birth date, birth year and
     * gender are needed to sign up.
     *
     * @param email_text  user's email
     * @param name_text   user's name
     * @param pin_number  pin
     * @param birth_day   user's birth day. Range: 0-30 (the first day of the month is represented
     *                    by 0)
     * @param birth_month user's birth month. Range: 0-11 (January is represented by 0 and December
     *                    by 11)
     * @param birth_year  user's birth year.
     * @param gender      user's gender. Female is represented by <code>true</code> and Male by
     *                    <code>false</code>.
     */
    public User(String email_text, String name_text, int pin_number, int birth_day, int birth_month, int birth_year, boolean gender) {
        this.email_text = email_text;
        this.name_text = name_text;
        this.pin_number = pin_number;
        this.birth_day = birth_day;
        this.birth_month = birth_month;
        this.birth_year = birth_year;
        this.gender = gender;
    }

    /**
     * Constructor used to edit user's information. All the information is needed to make an update.
     *
     * @param email_text  user's email
     * @param name_text   user's name
     * @param pin_number  pin
     * @param birth_day   user's birth day. Range: 0-30 (the first day of the month is represented
     *                    by 0)
     * @param birth_month user's birth month. Range: 0-11 (January is represented by 0 and December
     *                    by 11)
     * @param birth_year  user's birth year.
     * @param gender      user's gender. Female is represented by <code>true</code> and Male by
     *                    <code>false</code>.
     * @param user_id     user's id
     */
    public User(String email_text, String name_text, int pin_number, int birth_day, int birth_month, int birth_year, boolean gender, String user_id) {
        this.email_text = email_text;
        this.name_text = name_text;
        this.pin_number = pin_number;
        this.birth_day = birth_day;
        this.birth_month = birth_month;
        this.birth_year = birth_year;
        this.gender = gender;
        this.user_id = user_id;
    }

    /**
     * Function used to add the information get from the database while signing in.
     *
     * @param name_text   user's name
     * @param birth_day   user's birth day. Range: 0-30 (the first day of the month is represented
     *                    by 0)
     * @param birth_month user's birth month. Range: 0-11 (January is represented by 0 and December
     *                    by 11)
     * @param birth_year  user's birth year.
     * @param gender      user's gender. Female is represented by <code>true</code> and Male by
     *                    <code>false</code>.
     * @param user_id     user's id
     */
    public void completeSignIn(String name_text, int birth_day, int birth_month, int birth_year, boolean gender, String user_id) {
        this.name_text = name_text;
        this.birth_day = birth_day;
        this.birth_month = birth_month;
        this.birth_year = birth_year;
        this.gender = gender;
        this.user_id = user_id;
    }

    /**
     * Set user's id. Used while signing up after saving the user in the database.
     *
     * @param user_id user's id
     */
    public void setId(String user_id) {
        this.user_id = user_id;
    }

    /**
     * Get the user's email
     *
     * @return user's email
     */
    public String getEmail() {
        return this.email_text;
    }

    /**
     * Get the user's pin
     *
     * @return user's pin
     */
    public int getPin() {
        return this.pin_number;
    }

    /**
     * Get the user's id
     *
     * @return user's id
     */
    public String getId() {
        return this.user_id;
    }

    /**
     * Get a document with all the data needed to send the user to the database.
     *
     * @return document with the following user's information: email, name, birth date, gender and
     * pin.
     */
    public Document getRegisterDocument() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = String.valueOf(birth_year) + "-" + String.valueOf(birth_month + 1) + "-" +
                String.valueOf(birth_day + 1);
        Document document =
                new Document()
                        .append("email", email_text)
                        .append("name", name_text)
                        .append("birthDate", format.parse(date))
                        .append("gender", gender)
                        .append("pin", pin_number);
        return document;
    }


    /**
     * Save the user's information in the shared preferences. I is used after having signed up or
     * signed in.
     */
    public void save(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", name_text);
        editor.putString("email", email_text);
        editor.putInt("birthDay", birth_day);
        editor.putInt("birthMonth", birth_month);
        editor.putInt("birthYear", birth_year);
        editor.putBoolean("gender", gender);
        editor.putInt("pin", pin_number);
        editor.putString("user_id", user_id);

        editor.commit();
    }

}
