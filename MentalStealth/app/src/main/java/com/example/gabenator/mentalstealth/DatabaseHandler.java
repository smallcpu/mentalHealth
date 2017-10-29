package com.example.gabenator.mentalstealth;

/**
 * Created by Gabenator on 10/28/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // DB Name
    private static final String DATABASE_NAME = "TextMessage";

    // DB Table Name
    private static final String TABLE_NAME = "MessagesTable";

    // DB Columns
    private static final String DATE = "TextDate";
    private static final String CONTENT = "TextContent";
    private static final String NUMBER = "PhoneNumber";
    private static final String RATING = "TextRating";
    private static final String TYPE = "MessageType";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + DATE + " TEXT not null, "
                    + CONTENT + " TEXT not null, "
                    + NUMBER + " TEXT not null, "
                    + RATING + " TEXT not null, "
                    + TYPE + " INTEGER not null, "
                    + "PRIMARY KEY(" + DATE + "," + NUMBER + "," + CONTENT + "))";

    private SQLiteDatabase database;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase();
        Log.d(TAG, "DatabaseHandler: C'tor DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called if the DB does not exist
        Log.d(TAG, "onCreate: Mking New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Message> loadTexts() {
        ArrayList<Message> messages = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME,  // The table to query
                new String[]{DATE,CONTENT,NUMBER,RATING,TYPE}, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                DATE +" DESC"); // The sort order
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < 50; i++) {
                long textDate = Long.valueOf(cursor.getString(0)).longValue();
                String textContent = cursor.getString(1);
                String textNumber = cursor.getString(2);
                double textRating = cursor.getDouble(3);
                int textType = cursor.getInt(4);
                messages.add(new Message(textType, textContent, textDate, textNumber, textRating));
                cursor.moveToNext();
            }
            cursor.close();
        }
        Log.d(TAG, "loadStocks: DONE LOADING STOCKS DATA FROM DB");

        return messages;
    }

    public void addMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put(DATE, message.getDate_Sent());
        values.put(CONTENT, message.getContent());
        values.put(NUMBER, message.getNumber());
        values.put(RATING, message.getRating());
        values.put(TYPE, message.getType());

        long key = database.insert(TABLE_NAME, null, values);
        Log.d(TAG, "addCountry: " + key);
    }

    /*
    public void dumpLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpLog: vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpLog: " +
                        String.format("%s %-18s", SYMBOL + ":", symbol) +
                        String.format("%s %-18s", COMPANY + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");*/
    }