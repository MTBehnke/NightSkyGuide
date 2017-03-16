// SQLiteAssetHelper subclass that defines the app's database

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DSObjectDatabaseHelper extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "DSObjects.db";
    private static final int DATABASE_VERSION = 2;

    // constructor
    public DSObjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Cursor getDSObjects() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {
                "_id",
                "object",
                "type",
                "mag",
                "size",
                "distance",
                "ra",
                "dec",
                "const",
                "name",
                "psa",
                "oith",
                "observed"
        };
        String sqlTables = "dsObjects";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    public static void forceDatabaseReload(Context context) {
        DSObjectDatabaseHelper dbHelper = new DSObjectDatabaseHelper(context);
        dbHelper.setForcedUpgrade(DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setVersion(-1);
        db.close();
        db = dbHelper.getWritableDatabase();
    }

    /*
    // creates the dsObjects table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the dsObjects table
        final String CREATE_DSOBJECTSDB_TABLE =
            "CREATE TABLE " + DSObjectDB.TABLE_NAME + "(" +
                DSObjectDB._ID + " integer primary key, " +
                DSObjectDB.DSO_OBJECTID + " TEXT, " +
                DSObjectDB.DSO_TYPE + " TEXT, " +
                DSObjectDB.DSO_MAG + " REAL, " +
                DSObjectDB.DSO_SIZE + " TEXT, " +
                DSObjectDB.DSO_DIST + " TEXT, " +
                DSObjectDB.DSO_RA + " REAL, " +
                DSObjectDB.DSO_DEC + " REAL, " +
                DSObjectDB.DSO_CONST + " TEXT, " +
                DSObjectDB.DSO_NAME + " TEXT, " +
                DSObjectDB.DSO_PSA + " TEXT, " +
                DSObjectDB.DSO_OITH + " TEXT, " +
                DSObjectDB.DSO_OBSERVED + " INTEGER);";
        db.execSQL(CREATE_DSOBJECTSDB_TABLE);  // create the table
    }

// normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    */
}
