package com.mikesrv9a.nightskyguide;

/**
 *  SQLiteOpenHelper subclass that defines the app's database
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;

class DSObjectDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "DSObjects.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public DSObjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the dsObjects table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the dsObjeects table
        final String CREATE_DSOBJECTS_TABLE =
            "CREATE TABLE " + DSObject.TABLE_NAME + "(" +
                DSObject._ID + " integer primary key, " +
                DSObject.DSO_OBJECTID + " TEXT, " +
                DSObject.DSO_TYPE + " TEXT, " +
                DSObject.DSO_MAG + " REAL, " +
                DSObject.DSO_SIZE + " TEXT, " +
                DSObject.DSO_DIST + " TEXT, " +
                DSObject.DSO_RA + " REAL, " +
                DSObject.DSO_DEC + " REAL, " +
                DSObject.DSO_CONST + " TEXT, " +
                DSObject.DSO_NAME + " TEXT, " +
                DSObject.DSO_PSA + " TEXT, " +
                DSObject.DSO_OITH + " TEXT, " +
                DSObject.DSO_OBSERVED + " INTEGER);";
        db.execSQL(CREATE_DSOBJECTS_TABLE);  // create the table
    }

// normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
