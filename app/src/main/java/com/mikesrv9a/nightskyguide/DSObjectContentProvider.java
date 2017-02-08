// ContentProvider subclass for manipulating the app's database

package com.mikesrv9a.nightskyguide;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.mikesrv9a.nightskyguide.DatabaseDescription;
import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;
import com.mikesrv9a.nightskyguide.DSObjectDatabaseHelper;

public class DSObjectContentProvider extends ContentProvider {
    // used to access the database
    private DSObjectDatabaseHelper dbHelper;

    // UriMatcher helps ContentProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    // constants used with UriMatcher to determine operation to perform
    private static final int ONE_DSOBJECT = 1;  // manipulate one contact
    private static final int DSOBJECTS = 2; // manipulate dsObjects table

    // static block to configure this ContentProvider's UriMatcher
    static {
        // Uri for dsObject with the specified id (#)
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DSObject.TABLE_NAME + "/#", ONE_DSOBJECT);

        // Uri for dsObjects table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DSObject.TABLE_NAME, DSOBJECTS);
    }

    // called when the DSObjectContentProvider is created
    @Override
    public boolean onCreate() {
        // create the DSObjectDatabaseHelper
        dbHelper = new DSObjectDatabaseHelper(getContext());
        return true;  // ContentProvider successfully created
    }

    // required method:  Not used in the app, so return null
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // query the database
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {

        // create SQLiteQueryBuilder for querying dsObjects table
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DSObject.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_DSOBJECT: // dsObject with specified id will be selected
                queryBuilder.appendWhere(
                        DSObject._ID + "=" + uri.getLastPathSegment());
                break;
            case DSOBJECTS:  // all dsObjects will be selected
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_query_uri) + uri);
        }

        // execute the query to select one or all dsObjects
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);

        // configure to watch for content changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /*  These three added as required methods:  Not used in the app, so return null/0  */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0; }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0; }

}

/*  *** Suspend all database add/edit/delete capabilities
    // insert a new dsObject in the database
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newContactUri = null;

        switch (uriMatcher.match(uri)) {
            case DSOBJECTS:
                // insert the new dsdObject -- success yields new dsObjects row id
                long rowId = dbHelper.getWritableDatabase().insert(
                        DSObject.TABLE_NAME, null, values);

                // if the dsObject was inserted, create an appropriate Uri
                // otherwise throw an exception
                if (rowId > 0) { // SQLite row IDs start at 1
                    newContactUri = DSObject.buildDSObjectUri(rowId);

                    // notify observers that the database changed
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.insert_failed) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }

        return newContactUri;
    }

    // update an existing dsObject in the database
    @Override
    public int update(Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        int numberOfRowsUpdated;  // 1 if update successful, 0 otherwise

        switch (uriMatcher.match(uri)) {
            case ONE_DSOBJECT:
                // get from the uri the id of dsObject to update
                String id = uri.getLastPathSegment();

                //update the dsObject
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        DSObject.TABLE_NAME, values, DSObject._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }

        // if changes were made, notify observers that the database changed
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }

    // delete an existing dsObject from the database
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;

        switch (uriMatcher.match(uri)) {
            case ONE_DSOBJECT:
                // get from the uri the id of dsObject to update
                String id = uri.getLastPathSegment();

                // delete the dsObject
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        DSObject.TABLE_NAME, DSObject._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }

        // notify observers that the database changes
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
*/