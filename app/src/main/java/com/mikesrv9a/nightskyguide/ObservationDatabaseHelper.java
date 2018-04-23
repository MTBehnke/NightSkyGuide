// SQLiteOpenHelper subclass that creates the app's database of observations

package com.mikesrv9a.nightskyguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.mikesrv9a.nightskyguide.ObserveRecordsSchema.ObsTable;

public class ObservationDatabaseHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "Observations.db";
    private static final int DATABASE_VERSION = 2;

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
    public void onUpgrade(SQLiteDatabase obsDatabase, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion >= 2) {

            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            String [] sqlSelect = {
                    "_id",
                    ObsTable.Cols.DsoID
            };

            qb.setTables(ObsTable.NAME);
            Cursor data = qb.query(obsDatabase, sqlSelect, null, null, null, null, null);

            int obsIdCol = data.getColumnIndex("_id");
            int objectIdCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.DsoID);

            int recordNum;
            String obsObjectID;
            ContentValues values;
            String value;
            if (data.getCount() > 0) {
                data.moveToFirst();
                while (!data.isAfterLast()) {
                    recordNum = data.getInt(obsIdCol);
                    obsObjectID = data.getString(objectIdCol);
                    values = new ContentValues();
                    value = null;
                    switch (obsObjectID) {
                        case "C1":
                            value = "NGC 188";
                            break;
                        case "C2":
                            value = "NGC 40";
                            break;
                        case "C3":
                            value = "NGC 4236";
                            break;
                        case "C4":
                            value = "NGC 7023";
                            break;
                        case "C5":
                            value = "IC 342";
                            break;
                        case "C6":
                            value = "NGC 6543";
                            break;
                        case "C7":
                            value = "NGC 2403";
                            break;
                        case "C8":
                            value = "NGC 559";
                            break;
                        case "C9":
                            value = "Sh2-155";
                            break;
                        case "C10":
                            value = "NGC 663";
                            break;
                        case "C11":
                            value = "NGC 7635";
                            break;
                        case "C12":
                            value = "NGC 6946";
                            break;
                        case "C13":
                            value = "NGC 457";
                            break;
                        case "C14":
                            value = "NGC 869";
                            break;
                        case "C15":
                            value = "NGC 6826";
                            break;
                        case "C16":
                            value = "NGC 7243";
                            break;
                        case "C17":
                            value = "NGC 147";
                            break;
                        case "C18":
                            value = "NGC 185";
                            break;
                        case "C19":
                            value = "IC 5146";
                            break;
                        case "C20":
                            value = "NGC 7000";
                            break;
                        case "C21":
                            value = "NGC 4449";
                            break;
                        case "C22":
                            value = "NGC 7662";
                            break;
                        case "C23":
                            value = "NGC 891";
                            break;
                        case "C24":
                            value = "NGC 1275";
                            break;
                        case "C25":
                            value = "NGC 2419";
                            break;
                        case "C26":
                            value = "NGC 4244";
                            break;
                        case "C27":
                            value = "NGC 6888";
                            break;
                        case "C28":
                            value = "NGC 752";
                            break;
                        case "C29":
                            value = "NGC 5005";
                            break;
                        case "C30":
                            value = "NGC 7331";
                            break;
                        case "C31":
                            value = "IC 405";
                            break;
                        case "C32":
                            value = "NGC 4631";
                            break;
                        case "C33":
                            value = "NGC 6992";
                            break;
                        case "C34":
                            value = "NGC 6960";
                            break;
                        case "C35":
                            value = "NGC 4889";
                            break;
                        case "C36":
                            value = "NGC 4559";
                            break;
                        case "C37":
                            value = "NGC 6885";
                            break;
                        case "C38":
                            value = "NGC 4565";
                            break;
                        case "C39":
                            value = "NGC 2392";
                            break;
                        case "C40":
                            value = "NGC 3626";
                            break;
                        case "C41":
                            value = "Cr 50";
                            break;
                        case "C42":
                            value = "NGC 7006";
                            break;
                        case "C43":
                            value = "NGC 7814";
                            break;
                        case "C44":
                            value = "NGC 7479";
                            break;
                        case "C45":
                            value = "NGC 5248";
                            break;
                        case "C46":
                            value = "NGC 2261";
                            break;
                        case "C47":
                            value = "NGC 6934";
                            break;
                        case "C48":
                            value = "NGC 2775";
                            break;
                        case "C49":
                            value = "NGC 2237";
                            break;
                        case "C50":
                            value = "NGC 2244";
                            break;
                        case "C51":
                            value = "IC 1613";
                            break;
                        case "C52":
                            value = "NGC 4697";
                            break;
                        case "C53":
                            value = "NGC 3115";
                            break;
                        case "C54":
                            value = "NGC 2506";
                            break;
                        case "C55":
                            value = "NGC 7009";
                            break;
                        case "C56":
                            value = "NGC 246";
                            break;
                        case "C57":
                            value = "NGC 6822";
                            break;
                        case "C58":
                            value = "NGC 2360";
                            break;
                        case "C59":
                            value = "NGC 3242";
                            break;
                        case "C60":
                            value = "NGC 4038";
                            break;
                        case "C61":
                            value = "NGC 4039";
                            break;
                        case "C62":
                            value = "NGC 247";
                            break;
                        case "C63":
                            value = "NGC 7293";
                            break;
                        case "C64":
                            value = "NGC 2362";
                            break;
                        case "C65":
                            value = "NGC 253";
                            break;
                        case "C66":
                            value = "NGC 5694";
                            break;
                        case "C67":
                            value = "NGC 1097";
                            break;
                        case "C68":
                            value = "NGC 6729";
                            break;
                        case "C69":
                            value = "NGC 6302";
                            break;
                        case "C70":
                            value = "NGC 300";
                            break;
                        case "C71":
                            value = "NGC 2477";
                            break;
                        case "C72":
                            value = "NGC 55";
                            break;
                        case "C73":
                            value = "NGC 1851";
                            break;
                        case "C74":
                            value = "NGC 3132";
                            break;
                        case "C75":
                            value = "NGC 6124";
                            break;
                        case "C76":
                            value = "NGC 6231";
                            break;
                        case "C77":
                            value = "NGC 5128";
                            break;
                        case "C78":
                            value = "NGC 6541";
                            break;
                        case "C79":
                            value = "NGC 3201";
                            break;
                        case "C80":
                            value = "NGC 5139";
                            break;
                        case "C81":
                            value = "NGC 6352";
                            break;
                        case "C82":
                            value = "NGC 6193";
                            break;
                        case "C83":
                            value = "NGC 4945";
                            break;
                        case "C84":
                            value = "NGC 5286";
                            break;
                        case "C85":
                            value = "IC 2391";
                            break;
                        case "C86":
                            value = "NGC 6397";
                            break;
                        case "C87":
                            value = "NGC 1261";
                            break;
                        case "C88":
                            value = "NGC 5823";
                            break;
                        case "C89":
                            value = "NGC 6087";
                            break;
                        case "C90":
                            value = "NGC 2867";
                            break;
                        case "C91":
                            value = "NGC 3532";
                            break;
                        case "C92":
                            value = "NGC 3372";
                            break;
                        case "C93":
                            value = "NGC 6752";
                            break;
                        case "C94":
                            value = "NGC 4755";
                            break;
                        case "C95":
                            value = "NGC 6025";
                            break;
                        case "C96":
                            value = "NGC 2516";
                            break;
                        case "C97":
                            value = "NGC 3766";
                            break;
                        case "C98":
                            value = "NGC 4609";
                            break;
                        case "C100":
                            value = "IC 2944";
                            break;
                        case "C101":
                            value = "NGC 6744";
                            break;
                        case "C102":
                            value = "IC 2602";
                            break;
                        case "C103":
                            value = "NGC 2070";
                            break;
                        case "C104":
                            value = "NGC 362";
                            break;
                        case "C105":
                            value = "NGC 4833";
                            break;
                        case "C106":
                            value = "NGC 104";
                            break;
                        case "C107":
                            value = "NGC 6101";
                            break;
                        case "C108":
                            value = "NGC 4372";
                            break;
                        case "C109":
                            value = "NGC 3195";
                            break;
                    }
                    if (value != null) {
                        values.put(ObserveRecordsSchema.ObsTable.Cols.DsoID, value);
                        obsDatabase.update(ObserveRecordsSchema.ObsTable.NAME, values, "_id=" + recordNum, null);
                    }

                    data.moveToNext();
                }
            }
        }
    }

    public void deleteObservation(int recordId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(ObsTable.NAME,"_id=" + recordId, null);
    }

}
