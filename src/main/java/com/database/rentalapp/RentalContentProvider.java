package com.database.rentalapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class RentalContentProvider extends ContentProvider {
    private SQLiteDatabase mRentalDB = null;
    private Context mContext = null;

    public RentalContentProvider() {

        Log.i("Sainath", "Constructor of Rental Database");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if(mRentalDB != null)
            return mRentalDB.delete("POST_TABLE", selection, selectionArgs);

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        Log.i("Sainath", "On insert of Rental Database");
        if(mRentalDB != null)
            mRentalDB.insert("POST_TABLE", null, values);

        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        Log.i("Sainath", "On Create of Rental Database");
        mRentalDB = new RentalDatabaseHelper(getContext()).getRentalDatabase();
        Log.i("Sainath", "On Created of Rental Database");
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if(mRentalDB != null)
            return mRentalDB.query("POST_TABLE", projection, selection, selectionArgs, null, null, sortOrder);

        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if(mRentalDB != null)
            return mRentalDB.update("POST_TABLE", values, selection, selectionArgs);

        return 0;
    }
}
