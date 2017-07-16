// SQLiteOpenHelper subclass that creates the app's database of observations

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.mikesrv9a.nightskyguide.ObserveRecordsSchema.ObsTable;

public class ObservationDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "Observations.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public ObservationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase obsDatabase) {
        obsDatabase.execSQL("create table " + ObsTable.NAME + "(" +
        "_id integer primary key autoincrement, " +
        ObsTable.Cols.DsoID + ", " +
        ObsTable.Cols.ObsDate + ", " +
        ObsTable.Cols.Location + ", " +
        ObsTable.Cols.Seeing + ", " +
        ObsTable.Cols.Transparency + ", " +
        ObsTable.Cols.Telescope + ", " +
        ObsTable.Cols.Eyepiece + ", " +
        ObsTable.Cols.Power + ", " +
        ObsTable.Cols.Filter + ", " +
        ObsTable.Cols.Notes + ")"
        );
        Log.d(String.valueOf(obsDatabase), "onActivityCreated: ");
    }

    public Cursor getObservations() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String [] sqlSelect = {
                "_id",
                ObsTable.Cols.DsoID,
                ObsTable.Cols.ObsDate,
                ObsTable.Cols.Location,
                ObsTable.Cols.Seeing,
                ObsTable.Cols.Transparency,
                ObsTable.Cols.Telescope,
                ObsTable.Cols.Eyepiece,
                ObsTable.Cols.Power,
                ObsTable.Cols.Filter,
                ObsTable.Cols.Notes
        };

        qb.setTables(ObsTable.NAME);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);
        c.moveToFirst();
        return c;
    }

    @Override
    public void onUpgrade(SQLiteDatabase obsDatabase, int oldVersion, int newVersion) {}

}
