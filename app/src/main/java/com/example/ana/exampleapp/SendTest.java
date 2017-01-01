package com.example.ana.exampleapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.*;


/**
 * Package-private class to send daily tests to the server database.
 *
 * @autor Ana María Martínez Gómez
 */
class SendTest extends AsyncTask<Context, Void, Boolean> {
    private String TAG = "SendTest";
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final String[] projection = {
            FeedTestContract.FeedEntry.COLUMN_NAME_TIMESTAMP,
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_LAST,
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_TOTAL,
            FeedTestContract.FeedEntry.COLUMN_NAME_PIN_TRIES,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q1,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q2,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q3,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q4,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q5,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q6,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q7,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q8,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q9,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q10,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q11,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q12,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q13,
            FeedTestContract.FeedEntry.COLUMN_NAME_Q14,
            FeedTestContract.FeedEntry.COLUMN_LATITUDE,
            FeedTestContract.FeedEntry.COLUMN_LONGITUDE
    };

    @Override
    protected Boolean doInBackground(Context... params) {
        Context context = params[0];
        SharedPreferences settings =
                context.getSharedPreferences(Variables.PREFS_NAME, Context.MODE_PRIVATE);
        int local_tests = settings.getInt("local_tests", 0);
        Log.v(TAG, "Tests: " + String.valueOf(local_tests));
        if (local_tests > 0 && canConnect(context, settings)) {
            try {
                MongoClientURI mongoClientURI = new MongoClientURI(Variables.mongo_uri);
                MongoClient mongoClient = new MongoClient(mongoClientURI);
                MongoDatabase dbMongo = mongoClient.getDatabase(mongoClientURI.getDatabase());
                MongoCollection<Document> coll = dbMongo.getCollection("mobileTests");

                boolean isFemale = settings.getBoolean("gender", true);
                ObjectId user_id = new ObjectId(settings.getString("user_id", ""));

                Cursor c = getSQLCursor(context, local_tests);
                c.moveToLast();

                for (; local_tests > 0; local_tests--) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(FeedTestContract.dateWithTimezone(c.getString(0)));
                    String date_string = format.format(cal.getTime());
                    Log.v(TAG, date_string);
                    Date original_date = format.parse(date_string);
                    DateFormat format_day = new SimpleDateFormat("yyyy-MM-dd");
                    Date today_date = format_day.parse(date_string);
                    Log.v(TAG, today_date.toString());
                    cal.setTime(today_date);
                    cal.add(Calendar.DATE, 1);  // number of days to add
                    Date tomorrow_date = cal.getTime(); // dt is now the new date
                    Log.v(TAG, tomorrow_date.toString());

                    Document document = getDoc(c, user_id, isFemale, original_date);
                    MongoCursor<Document> it = coll.find(and(and(lt("date", tomorrow_date), gte("date", today_date)), eq("user_id", user_id))).limit(1).iterator();
                    if (it.hasNext()) {
                        // replace the entire document except for the _id field
                        coll.replaceOne(new Document("_id", it.next().getObjectId("_id")), document);
                    } else {
                        coll.insertOne(document);
                    }
                    c.moveToPrevious();
                }

                mongoClient.close();
                deleteSQLEntries(context);
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
            Variables.saveLocalTests(TAG, settings, local_tests);
        }

        boolean start = (local_tests > 0);
        Log.v(TAG, "Flag: " + (start ? "enabled" : "disabled") +
                ", Tests: " + String.valueOf(local_tests));
        return start;
    }

    /**
     * determines whether the connection can be stabilised to send daily test. I calls connection()
     * function and takes into account if it is allowed to send them with the mobile data or only
     * with wifi.
     *
     * @param context  The {@link Activity} context
     * @param settings The context settings
     * @return whether the connection can be stabilised to send daily test
     */
    private static boolean canConnect(Context context, SharedPreferences settings) {
        int connection = Variables.connection(context);
        boolean only_wifi = settings.getBoolean("only_wifi", false);
        if ((only_wifi && connection == 1) || (!only_wifi && connection >= 0))
            return true;
        return false;
    }

    private Cursor getSQLCursor(Context context, int local_tests) {
        FeedTestDbHelper mDbHelper = new FeedTestDbHelper(context);
        SQLiteDatabase readable_db = mDbHelper.getReadableDatabase();
        Cursor c = readable_db.query(
                FeedTestContract.FeedEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                FeedTestContract.FeedEntry._ID + " DESC", String.valueOf(local_tests));
        return c;
    }

    private void deleteSQLEntries(Context context) {
        FeedTestDbHelper mDbHelper = new FeedTestDbHelper(context);
        SQLiteDatabase writable_db = mDbHelper.getWritableDatabase();
        String tname = FeedTestContract.FeedEntry.TABLE_NAME;
        // LIMIT -1 is necessary if we want to use OFFSET
        writable_db.execSQL("DELETE FROM "
                + tname + " WHERE `_id` IN (SELECT `_id` FROM "
                + tname + " ORDER BY `_id` DESC LIMIT -1 OFFSET 2)");
    }

    private Document getDoc(Cursor c, ObjectId user_id, Boolean isFemale, Date date) throws java.text.ParseException {
        boolean drugs = (c.getInt(14) == 1);
        Document document = new Document()
                .append("user_id", user_id) // Check pinters
                .append("date", date) // Convert to date
                .append("speedReaction", new Document()
                        .append("last", c.getInt(1))
                        .append("total", c.getInt(2))
                        .append("tries", c.getInt(3)))
                .append("affectiveState", c.getInt(4))
                .append("motivation", c.getInt(5))
                .append("concentration", c.getInt(6))
                .append("anxiety", c.getInt(7))
                .append("irritability", c.getInt(8))
                .append("fatigue", c.getInt(9))
                .append("caffeine", c.getInt(11))
                .append("alcohol", c.getInt(12))
                .append("tobacco", c.getInt(13))
                .append("drugs", drugs)
                .append("timeBed", c.getInt(15))
                .append("timeSleep", c.getInt(16))
                .append("timeWakeUp", c.getInt(17))
                .append("latitude", c.getInt(18))
                .append("longitude", c.getInt(19));
        if (isFemale) {
            document.append("menstruation", c.getInt(10));
        }
        return document;
    }

}
