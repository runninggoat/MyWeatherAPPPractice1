package com.myweather.gjj.myweather;

/**
 * Created by 15018 on 2017/11/8.
 */

public class Settings {

    public static final String TABLE_NAME = "weather_forcast";
    public static final String SID = "sid";
    public static final String START_DATE = "start_date";
    public static final String CITY = "city";
    public static final String JSON = "json";
    public static final String STATE = "state";
    public static final String URL = "url";
    public static final String MEMO = "memo";

    public static final String CREATE_DB_SQL = "CREATE TABLE "+TABLE_NAME+" (sid  integer primary key autoincrement, start_date text not null, city text, json text, state text not null, url text, memo text);";

    public static final String AUTHORITY = "weather_forcast";
}
