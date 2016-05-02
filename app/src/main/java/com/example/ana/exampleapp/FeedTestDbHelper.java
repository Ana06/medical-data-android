package com.example.ana.exampleapp;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * SQL Lite Helper for the test database.
 *
 * @author Ana María Martínez Gómez
 * @see FeedTestContract
 */
public class FeedTestDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "FeedTest.db";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TIMESTAMP_TYPE = " TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedTestContract.FeedEntry.TABLE_NAME + " (" +
            FeedTestContract.FeedEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_LAST + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_TOTAL + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_TRIES + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q1 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q2 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q3 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q4 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q5 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q6 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q7 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q8 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q9 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q10 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q11 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q12 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_Q13 + INTEGER_TYPE + COMMA_SEP +
            FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP + TIMESTAMP_TYPE +
            " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedTestContract.FeedEntry.TABLE_NAME;

    /**
     * Class constructor.
     *
     * @param context   Its context
     */
    public FeedTestDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}