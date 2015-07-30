package com.database.rentalapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sainath on 22-07-2015.
 */
public class RentalDatabaseHelper extends SQLiteOpenHelper {

    private final static String POST_TABLE = "CREATE TABLE IF NOT EXISTS POST_TABLE (_ID INTEGER PRIMARY KEY AUTOINCREMENT ," +
            "POST_TITLE TEXT NOT NULL, POST_DESCRIPTION TEXT NOT NULL, POST_PRICE INTEGER NOT NULL, POST_CATEGORY TEXT NOT NULL," +
            "POST_LOCATION TEXT) ";

    public RentalDatabaseHelper(Context context) {
        super(context, "RentalPost", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(POST_TABLE);
        Log.i("Sainath", "On Create of Rental DB Helper");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public SQLiteDatabase getRentalDatabase() {
        return this.getWritableDatabase();
    }
}