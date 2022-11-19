/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.misc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBTimer extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timers.db";
    private static final String TABLE_NAME = "Timers_table";
    public static final String COL_1 = "_id";
    public static final String NAME = "nimi";
    public static final String STARTTIME = "alkuaika";
    public static final String STOPTIME = "lopetusaika";
    public static final String MA = "ma";
    public static final String TI = "ti";
    public static final String KE = "ke";
    public static final String TO = "tor";
    public static final String PE = "pe";
    public static final String LA = "la";
    public static final String SU = "su";
    public static final String SELECTOR = "selectState";
    public static final String ISITON = "isiton";
    private static final String[] ALL_KEYS = new String[]{COL_1, NAME, STARTTIME, STOPTIME, MA, TI, KE, TO, PE, LA, SU, SELECTOR, ISITON};

    public DBTimer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase dbtime) {
        dbtime.execSQL("create table " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + STARTTIME + " TEXT," + STOPTIME + " TEXT," + MA + " TEXT," + TI + " TEXT," + KE + " TEXT," + TO + " TEXT," + PE + " TEXT," + LA + " TEXT," + SU + " TEXT, " + SELECTOR + " TEXT," + ISITON + " TEXT" + ")");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(String name, String startTime, String stopTime, String ma, String ti, String ke, String to, String pe, String la, String su, String selector, String isiton) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(STARTTIME, startTime);
        contentValues.put(STOPTIME, stopTime);
        contentValues.put(MA, ma);
        contentValues.put(TI, ti);
        contentValues.put(KE, ke);
        contentValues.put(TO, to);
        contentValues.put(PE, pe);
        contentValues.put(LA, la);
        contentValues.put(SU, su);
        contentValues.put(SELECTOR, selector);
        contentValues.put(ISITON, isiton);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result;

    }

    public Cursor timerID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + COL_1 + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }

    public void deleteRow(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_1 + "=" + id, null);
    }

    public void tallennaMuutokset(String id, String name, String startTime, String stopTime, String ma, String ti, String ke, String to, String pe, String la, String su, String selector, String isiton) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(STARTTIME, startTime);
        contentValues.put(STOPTIME, stopTime);
        contentValues.put(MA, ma);
        contentValues.put(TI, ti);
        contentValues.put(KE, ke);
        contentValues.put(TO, to);
        contentValues.put(PE, pe);
        contentValues.put(LA, la);
        contentValues.put(SU, su);
        contentValues.put(SELECTOR, selector);
        contentValues.put(ISITON, isiton);
        db.update(TABLE_NAME, contentValues, "_id = ?", new String[]{id});
    }

    public Cursor getAllRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(true, TABLE_NAME, ALL_KEYS, null, null, null, null, COL_1 + " DESC", null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

}
