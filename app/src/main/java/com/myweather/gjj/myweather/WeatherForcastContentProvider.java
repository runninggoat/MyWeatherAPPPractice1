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
        fieldMap.put(StaticValues.SID, StaticValues.SID);
        fieldMap.put(StaticValues.START_DATE, StaticValues.START_DATE);
        fieldMap.put(StaticValues.CITY, StaticValues.CITY);
        fieldMap.put(StaticValues.JSON, StaticValues.JSON);
        fieldMap.put(StaticValues.STATE, StaticValues.STATE);
        fieldMap.put(StaticValues.URL, StaticValues.URL);
        fieldMap.put(StaticValues.MEMO, StaticValues.MEMO);
    }

    public WeatherForcastContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, "weather provider delete");
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
        return sqLiteDatabase.delete(StaticValues.WEATHER_CONTENT_TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return StaticValues.WEATHER_CONTENT_TABLE_NAME;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.i(TAG, "weather provider insert");
        sqLiteDatabase = weatherDBHelper.getWritableDatabase();
        long id = sqLiteDatabase.insert(StaticValues.WEATHER_CONTENT_TABLE_NAME, StaticValues.SID, values);
        if(id < 0) {
            throw new SQLiteException("Unable to insert " + values + " for " + uri);
        }
        Uri newUri = ContentUris.withAppendedId(uri, id);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        weatherDBHelper = new WeatherDBHelper(getContext(), StaticValues.WEATHER_CONTENT_TABLE_NAME, null, 1);
        Log.i(TAG, "Weather provider created.");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "weather provider query");
        sqLiteDatabase = weatherDBHelper.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(StaticValues.WEATHER_CONTENT_TABLE_NAME);
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
        count = sqLiteDatabase.update(StaticValues.WEATHER_CONTENT_TABLE_NAME, values, selection, selectionArgs);
        return count;
    }
}
