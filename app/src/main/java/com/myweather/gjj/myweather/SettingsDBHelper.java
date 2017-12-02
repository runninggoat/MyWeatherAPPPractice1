package com.myweather.gjj.myweather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by 15018 on 2017/12/1.
 */

public class SettingsDBHelper extends SQLiteOpenHelper {

    private final String TAG = "SettingsDBHelper";

    public SettingsDBHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate settings database...");
        sqLiteDatabase.execSQL(StaticValues.CREATE_SETTINGS_DB_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          int i, int i1) {
        Log.d(TAG, "onUpgrade settings database...");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + StaticValues.SETTINGS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
