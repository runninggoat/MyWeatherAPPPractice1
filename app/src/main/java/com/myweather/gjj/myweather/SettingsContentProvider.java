package com.myweather.gjj.myweather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

public class SettingsContentProvider extends ContentProvider {

    private static String TAG = "SettingsContentProvider";
    private SQLiteDatabase sqLiteDatabase;
    private SettingsDBHelper settingsDBHelper;

    private static HashMap<String, String> fieldMap = new HashMap<>();
    static {
        fieldMap.put(StaticValues.KEY, StaticValues.KEY);
        fieldMap.put(StaticValues.VALUE, StaticValues.VALUE);
    }

    public SettingsContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "settings provider delete");
        sqLiteDatabase =  settingsDBHelper.getWritableDatabase();
        return sqLiteDatabase.delete(StaticValues.SETTINGS_TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return StaticValues.SETTINGS_TABLE_NAME;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "settings provider insert");
        sqLiteDatabase = settingsDBHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(StaticValues.SETTINGS_TABLE_NAME, StaticValues.KEY, values);
        if(id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        settingsDBHelper = new SettingsDBHelper(getContext(), StaticValues.SETTINGS_TABLE_NAME, null, 1);
        Log.i(TAG, "settings provider created.");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "settings provider query");
        sqLiteDatabase = settingsDBHelper.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(StaticValues.SETTINGS_TABLE_NAME);
        sqLiteQueryBuilder.setProjectionMap(fieldMap);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        Log.i(TAG, "settings provider update");
        sqLiteDatabase = settingsDBHelper.getWritableDatabase();
        int count = 0;
        count = sqLiteDatabase.update(StaticValues.SETTINGS_TABLE_NAME, values, selection, selectionArgs);
        return count;
    }
}
