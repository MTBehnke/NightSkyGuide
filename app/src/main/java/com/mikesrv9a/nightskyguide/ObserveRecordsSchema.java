// Defines Observation Record Database Schema
// Class only exists to define the String constants used in the database table
// Name of table in database:  ObsTable.NAME
// Column references:  ObsTable.Col.string name from below

package com.mikesrv9a.nightskyguide;

public class ObserveRecordsSchema {
    public static final class ObsTable {
        public static final String NAME = "observations";    // table name

        public static final class Cols {    // database column names
            public static final String DsoID = "dsoid";
            public static final String ObsDate = "date";   // date-time
            public static final String Location = "location";
            public static final String Seeing = "seeing";
            public static final String Transparency = "transparency";
            public static final String Telescope = "telescope";
            public static final String Eyepiece = "eyepiece";
            public static final String Power = "power";
            public static final String Filter = "filter";
            public static final String Notes = "notes";
            public static final String Catalogue = "catalogue";
            public static final String Program = "program";
        }
    }
}
