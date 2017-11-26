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

public class WeatherForcastContentProvider extends ContentProvider {

    private static String TAG = "WFContentProvider";
    private SQLiteDatabase sqLiteDatabase;
    private WeatherDBHelper weatherDBHelper;

    private static HashMap<String, String> fieldMap = new HashMap<>();
    static {
        fieldMap.put(Settings.SID, Settings.SID);
        fieldMap.put(Settings.START_DATE, Settings.START_DATE);
        fieldMap.put(Settings.CITY, Settings.CITY);
        fieldMap.put(Settings.JSON, Settings.JSON);
        fieldMap.put(Settings.STATE, Settings.STATE);
        fieldMap.put(Settings.URL, Settings.URL);
        fieldMap.put(Settings.MEMO, Settings.MEMO);
    }

    public WeatherForcastContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "weather provider delete");
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
        return sqLiteDatabase.delete(Settings.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return Settings.TABLE_NAME;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "weather provider insert");
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(Settings.TABLE_NAME, Settings.SID, values);
        if(id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        weatherDBHelper = new WeatherDBHelper(getContext(), Settings.TABLE_NAME, null, 1);
        Log.i(TAG, "Weather provider created.");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "weather provider query");
        sqLiteDatabase = weatherDBHelper.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(Settings.TABLE_NAME);
        sqLiteQueryBuilder.setProjectionMap(fieldMap);
        Cursor cursor = sqLiteQueryBuilder.query(sqLiteDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values,
                      String selection,
                      String[] selectionArgs) {
        Log.i(TAG, "weather provider update");
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
        int count = 0;
        count = sqLiteDatabase.update(Settings.TABLE_NAME, values, selection, selectionArgs);
        return count;
    }
}
