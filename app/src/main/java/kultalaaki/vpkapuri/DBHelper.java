package kultalaaki.vpkapuri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "halytyksetArkisto.db";
    private static final String TABLE_NAME = "Halytykset_table";
    static final String COL_1= "_id";
    static final String TUNNUS = "tunnus";
    static final String LUOKKA = "luokka";
    static final String VIESTI = "halyviesti";
    static final String KOMMENTTI = "kommentti";
    private static final String[] ALL_KEYS = new String[] {COL_1, TUNNUS, LUOKKA, VIESTI, KOMMENTTI};

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + TUNNUS + " TEXT,"
                + LUOKKA + " TEXT," + VIESTI + " TEXT," + KOMMENTTI + " TEXT" + ")");
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

    boolean insertData(String tunnus, String luokka, String viesti, String kommentti) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TUNNUS, tunnus);
        contentValues.put(LUOKKA, luokka);
        contentValues.put(VIESTI, viesti);
        contentValues.put(KOMMENTTI, kommentti);
        /*long result = */db.insert(TABLE_NAME, null, contentValues);
        db.close();
        //if(result == -1)
          //  return false;
        //else
        return true;

    }

    /*Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        //db.rawQuery("select * from " + TABLE_NAME, null);
        return db.rawQuery("select * from " + TABLE_NAME, null);
    }*/

    Cursor halyID(String id) {
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

    boolean lisaaKommentti(String id, String tunnus, String luokka, String viesti, String kommentti) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KOMMENTTI, kommentti);
        contentValues.put(TUNNUS, tunnus);
        contentValues.put(LUOKKA, luokka);
        contentValues.put(VIESTI, viesti);
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
    Cursor haeViimeisinLisays() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.query(TABLE_NAME, ALL_KEYS, null, null, null, null, null);
        c.moveToLast();
        return c;
    }

}
