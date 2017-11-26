package com.myweather.gjj.myweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 15018 on 2017/11/8.
 */

public class WeatherDBHelper extends SQLiteOpenHelper {

    private final String TAG = "WeatherDBHelper";

    public WeatherDBHelper(Context context, String name,
                           SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate database...");
        sqLiteDatabase.execSQL(Settings.CREATE_DB_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          int i, int i1) {
        Log.d(TAG, "onUpgrade database...");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Settings.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
