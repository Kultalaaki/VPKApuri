/*
 * Created by Kultala Aki on 7.7.2019 12:26
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.5.2019 18:34
 */

package kultalaaki.vpkapuri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBTimer extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "timers.db";
    private static final String TABLE_NAME = "Timers_table";
    static final String COL_1 = "_id";
    static final String NAME = "nimi";
    static final String STARTTIME = "alkuaika";
    static final String STOPTIME = "lopetusaika";
    static final String MA = "ma";
    static final String TI = "ti";
    static final String KE = "ke";
    static final String TO = "tor";
    static final String PE = "pe";
    static final String LA = "la";
    static final String SU = "su";
    static final String SELECTOR = "selectState";
    static final String ISITON = "isiton";
    private static final String[] ALL_KEYS = new String[] {COL_1, NAME, STARTTIME, STOPTIME, MA, TI, KE, TO, PE, LA, SU, SELECTOR, ISITON};

    DBTimer(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase dbtime) {
        dbtime.execSQL("create table " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + NAME + " TEXT,"
                + STARTTIME + " TEXT," + STOPTIME + " TEXT," + MA + " TEXT," + TI + " TEXT," + KE + " TEXT," + TO + " TEXT," + PE + " TEXT," + LA + " TEXT," + SU + " TEXT, " + SELECTOR + " TEXT," + ISITON + " TEXT" + ")");
    }

    /* varakopio
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + TUNNUS + " TEXT,"
                + LUOKKA + " TEXT," + VIESTI + " TEXT," + KOMMENTTI + " TEXT" + ")");
    }
     */
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //onCreate(db);
    }

    long insertData(String name, String startTime, String stopTime, String ma, String ti, String ke, String to, String pe, String la, String su, String selector, String isiton) {
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

    /*Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        //db.rawQuery("select * from " + TABLE_NAME, null);
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }*/

    Cursor timerID(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + COL_1 + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return c;
    }

    void deleteRow(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COL_1+"="+id, null);
        //db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'Halytykset_table'");
    }

    boolean tyhjennaTietokanta () {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.delete(TABLE_NAME, "Halytykset_table", null);
        db.delete(TABLE_NAME, null, null);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'Halytykset_table'");
        return true;
    }

    boolean tallennaMuutokset(String id, String name, String startTime, String stopTime, String ma, String ti, String ke, String to, String pe, String la, String su, String selector, String isiton) {
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
        db.update(TABLE_NAME, contentValues, "_id = ?", new String[] { id });
        return true;
    }

    Cursor getAllRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        //SQLiteDatabase dbr = this.getWritableDatabase();
        //db.execSQL("UPDATE SQLITE_SEQUENCE SET seq = 0 WHERE name = 'Halytykset_table'");
        //String where = null;
        Cursor c = db.query(true, TABLE_NAME, ALL_KEYS, null, null, null, null, COL_1+" DESC", null);
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /*Cursor hakuTunnuksella(String search_text) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String where = null;
        Cursor c = db.query(TABLE_NAME, ALL_KEYS,
                TUNNUS + " LIKE '%"+search_text+"%'",
                null, null, null,COL_1+" DESC");
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }

    Cursor hakuLuokalla(String search_text) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String where = null;
        Cursor c = db.query(TABLE_NAME, ALL_KEYS,
                LUOKKA + " LIKE '%"+search_text+"%'",
                null, null, null,COL_1+" DESC");
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }

    Cursor hakuTekstista(String search_text) {
        SQLiteDatabase db = this.getReadableDatabase();
        //String where = null;
        Cursor c = db.query(TABLE_NAME, ALL_KEYS,
                VIESTI + " LIKE '%"+search_text+"%' OR "
                        + KOMMENTTI + " LIKE '%"+search_text+"%'",
                null, null, null,COL_1+" DESC");
        if(c != null) {
            c.moveToFirst();
        }
        return c;
    }*/

    //Testi haetaan viimeisin entry tietokannasta
    /*Cursor haeViimeisinLisays() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, ALL_KEYS, null, null, null, null, null);
        c.moveToLast();
        return c;
    }*/

}
