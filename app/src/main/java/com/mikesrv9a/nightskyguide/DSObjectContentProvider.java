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
import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObjectDB;
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
                DSObjectDB.TABLE_NAME + "/#", ONE_DSOBJECT);

        // Uri for dsObjects table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DSObjectDB.TABLE_NAME, DSOBJECTS);
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
        queryBuilder.setTables(DSObjectDB.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_DSOBJECT: // dsObject with specified id will be selected
                queryBuilder.appendWhere(
                        DSObjectDB._ID + "=" + uri.getLastPathSegment());
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