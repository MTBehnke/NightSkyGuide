// SQLiteAssetHelper subclass that defines the app's database of DSObjects

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DSObjectDatabaseHelper extends SQLiteAssetHelper{
    private static final String DATABASE_NAME = "DSObjects.db";
    private static final int DATABASE_VERSION = 11;

    // constructor
    public DSObjectDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(DATABASE_VERSION);
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
                "catalogue",
                "skyatlas",
                "obsprogram",
                "turnleft",
                "dblmag",
                "dblseparation",
                "dblangle",
                "dblyear"
        };
        String sqlTables = "dsObjects";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    public Cursor queryDSObject(String objId) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String searchCol = "object =?";

        String [] sqlSelect = {
                "_id",
                "object",
                "catalogue",
                "obsprogram"
        };

        String sqlTables = "dsObjects";

        String[] searchArg = new String[] {objId};

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, searchCol, searchArg, null, null, null);
        c.moveToFirst();
        return c;
    }

    // only used to refresh database (after version error) - not normal function for app
    public static void forceDatabaseReload(Context context) {
        DSObjectDatabaseHelper dbHelper = new DSObjectDatabaseHelper(context);
        dbHelper.setForcedUpgrade(DATABASE_VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setVersion(-1);
        db.close();
        dbHelper.getWritableDatabase();
    }
}
