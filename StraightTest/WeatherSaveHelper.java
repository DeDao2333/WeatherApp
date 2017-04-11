package com.example.java.weatherapp.StraightTest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 59575 on 2017/1/6.
 */

public class WeatherSaveHelper extends SQLiteOpenHelper {
    public static final String WEATHER_INFO = "create table weather_info(" +
            "id integer primary key autoincrement," +
            "city text," +
            "cityid text," +
            "temp1 text," +
            "temp2 text," +
            "weather text," +
            "ptime text)";
    public static final String PROVINCES_CREATE = "create table provinces_list(" +
            "id integer primary key autoincrement," +
            "province text," +
            "province_id text)";
    public static final String CITY_CREATE = "create table city_list(" +
            "id integer primary key autoincrement," +
            "province_id text," +
            "city text," +
            "city_id text)";
    public static final String COUNTY_CREATE = "create table county_list(" +
            "id integer primary key autoincrement," +
            "city_id text," +
            "county text," +
            "county_id text)";
    private Context mcontext;
    public WeatherSaveHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mcontext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WEATHER_INFO);
        db.execSQL(PROVINCES_CREATE);
        db.execSQL(CITY_CREATE);
        db.execSQL(COUNTY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(PROVINCES_CREATE);
    }
}
