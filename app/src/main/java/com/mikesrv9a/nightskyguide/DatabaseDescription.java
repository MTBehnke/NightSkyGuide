package com.mikesrv9a.nightskyguide;

/**
 * Describes the table names and column names for this app's database,
 * and other informatiion required by the ContentProvider
 */

import android.content.ContentUris;
import android.provider.BaseColumns;
import android.net.Uri;

public class DatabaseDescription {
    // ContentProvider's name:  typically a package name
    public static final String AUTHORITY = "com.mikesrv9a.nightskyguide";

    // base URI used to interact with the ContentProvider
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // nested class defines contents of the dsObjects table
    public static final class DSObject implements BaseColumns {
        // table's name
        public static final String TABLE_NAME = "dsObjects";

        // Uri for the dsObjects table
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        // column names for dsObjects table's columns
        public static final String DSO_OBJECTID = "object";
        public static final String DSO_TYPE = "type";
        public static final String DSO_MAG = "mag";
        public static final String DSO_SIZE = "size";
        public static final String DSO_DIST = "distance";
        public static final String DSO_RA = "ra";
        public static final String DSO_DEC = "dec";
        public static final String DSO_CONST = "const";
        public static final String DSO_NAME = "name";
        public static final String DSO_PSA = "psa";
        public static final String DSO_OITH = "oith";
        public static final String DSO_OBSERVED = "observed";

        // creates a Uri for a specific dsObject
        public static Uri buildDSObjectUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}




