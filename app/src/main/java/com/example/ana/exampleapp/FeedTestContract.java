package com.example.ana.exampleapp;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import android.provider.BaseColumns;

/**
 * Contract class with information to create the test database and manage it.
 *
 * @author Ana María Martínez Gómez
 * @see FeedTestDbHelper
 */
public final class FeedTestContract {
    /**
     *  Final class: to prevent someone from accidentally instantiating the class, we give it an
     *  empty constructor.
     */
    public FeedTestContract() {}

    /**
     * Given a {@link Timestamp} returns a boolean indicating if its belongs to today or not.
     *
     * @param timestamp     A {@link Timestamp}
     * @return              <code>true</code>if the timestamp belongs to today; <code>false</code>
     *                      otherwise
     * @see Calendar
     */
    public static boolean isToday(String timestamp){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return dateWithTimezone(timestamp) >= cal.getTimeInMillis();
    }

    /**
     * Given a {@link Timestamp} returns a Long that represent the timestamp in the zone time of the
     * device which is running the app.
     *
     * @param timestamp     A {@link Timestamp}
     * @return              A Long that represent the timestamp in the zone time of the device which
     *                      is running the app.
     * @see TimeZone#getDefault()
     * @see TimeZone#getRawOffset()
     * @see TimeZone#getDSTSavings()
     */
    private static Long dateWithTimezone(String timestamp){
        int gmtOffset = TimeZone.getDefault().getRawOffset();
        int gmtDaylightSavings = TimeZone.getDefault().getDSTSavings();
        return (Timestamp.valueOf(timestamp).getTime() + gmtOffset + gmtDaylightSavings);
    }

    /**
     * Inner class that defines the table contents.  By implementing the {@link BaseColumns}
     * interface, it can inherit a primary key field _ID that some Android classes such as cursor
     * adaptors will expect it to have.
    */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "test";
        public static final String COLUMN_NAME_PIN = "speedReaction"; // Integer (milliseconds)
        public static final String COLUMN_NAME_Q1 = "affectiveState"; // Values: -3,-2,-1,0,1,2,3
        public static final String COLUMN_NAME_Q2 = "motivation"; // Values: -3,-2,-1,0,1,2,3
        public static final String COLUMN_NAME_Q3 = "concentration"; // Values: 1,2,3,4,5
        public static final String COLUMN_NAME_Q4 = "anxiety"; // Values: 1,2,3,4,5
        public static final String COLUMN_NAME_Q5 = "irritability"; // Values: 1,2,3,4,5
        public static final String COLUMN_NAME_Q6 = "fatigue"; // Values: 1,2,3,4,5
        public static final String COLUMN_NAME_Q7 = "menstrualPeriod"; // Values: 0(no), 1(yes)
        public static final String COLUMN_NAME_Q8 = "alcohol"; // Integer
        public static final String COLUMN_NAME_Q9 = "tobacco"; // Integer
        public static final String COLUMN_NAME_Q10 = "drugs"; // Values: 0(no), 1(yes)
        public static final String COLUMN_NAME_Q11 = "timeBed"; // Integer (minutes)
        public static final String COLUMN_NAME_Q12 = "timeSleep"; // Integer (minutes)
        public static final String COLUMN_NAME_Q13 = "timeWakeUp"; // Integer (minutes)
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp"; // Timestamp
    }

    //To make database management easier in TestActivity
    public static final String[] QUESTION_COLUMNS_NAMES =
            new String[]{
                    FeedEntry.COLUMN_NAME_Q1,
                    FeedEntry.COLUMN_NAME_Q2,
                    FeedEntry.COLUMN_NAME_Q3,
                    FeedEntry.COLUMN_NAME_Q4,
                    FeedEntry.COLUMN_NAME_Q5,
                    FeedEntry.COLUMN_NAME_Q6,
                    FeedEntry.COLUMN_NAME_Q7,
                    FeedEntry.COLUMN_NAME_Q8,
                    FeedEntry.COLUMN_NAME_Q9,
                    FeedEntry.COLUMN_NAME_Q10,
                    FeedEntry.COLUMN_NAME_Q11,
                    FeedEntry.COLUMN_NAME_Q12,
                    FeedEntry.COLUMN_NAME_Q13
            };
}