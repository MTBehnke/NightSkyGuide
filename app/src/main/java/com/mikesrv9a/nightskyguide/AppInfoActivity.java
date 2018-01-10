package com.mikesrv9a.nightskyguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AppInfoActivity extends AppCompatActivity {

    String infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        Integer appInfoKey = intent.getIntExtra("appInfoKey",1);

        TextView appInfo = (TextView)findViewById(R.id.app_info_text);

        switch (appInfoKey) {
            case 1:
                infoText = "Night Sky Guide v0.2.05 (beta)\n" +
                        "Copyright 2018 Shiny Objects LLC\n\n" +
                        "The main page displays each of the 110 Messier Object's, including:\n" +
                        "∙ DSO ID (with √ if previously observed)\n∙ Common Name\n" +
                        "∙ Constellation\n∙ DSO Type\n" +
                        "∙ Magnitude (with ● if Mag 7.0 or brighter)\n∙ Apparent Size\n" +
                        "∙ Current Altitude (with ▲ or ▼ for rising/setting)\n∙ Current Azimuth.\n\n" +
                        "The list of objects is sorted based on altitude, with setting " +
                        "objects listed first.  A negative altitude indicates the object " +
                        "is below the horizon.\n\n" +
                        "The altitude for circumpolar objects are preceeded by a ○ symbol and " +
                        "objects that do not rise above the horizon are preceeded by a ø symbol. " +
                        "Some objects without either symbol may only be above the horizon during " +
                        "daylight hours and thus may not be observable at this time of year.\n\n" +
                        "Clicking on any object in the list will open a detail page " +
                        "providing more information about the object, including rise and set times " +
                        "and an associated constellation chart.";
                break;
            case 2:
                infoText = "Rise and set times are shown for the current time zone of your phone or tablet, " +
                        "and are based on the latitude and longitude designated on the Settings page. " +
                        "Coordinate and time calculations should generally be less than 1° and a few minutes, which should " +
                        "suffice for finding objects in the night sky but may vary slightly from published sources.\n\n" +
                        "The constellation charts will provide a general location of many of the DSOs, however " +
                        "not all DSOs listed are shown in these charts and " +
                        "it is likely that you will need a sky atlas to find many of these objects.  The page numbers " +
                        "for two common atlases are listed on the detail pages.\n\n" +
                        "PSA refers to Sky & Telescope's Pocket Sky Atlas\n\n" +
                        "OITH refers to Objects in the Heavens ed.5.2 by Peter Birren\n\n" +
                        "Clicking on the + icon will allow you to create a record of your observations, including " +
                        "information generally required for various observing programs, such as those sponsored " +
                        "by the Astronomical League.\n\n";
                break;
            case 3:
                infoText = "Entering observation information is at your discretion, however if you using this " +
                        "as a record for an observing program, please make sure that you record all information " +
                        "required by the sponsors.\n\n  If you are only interested in tracking what objects you have " +
                        "observed, you can save a record without entering any information. "+
                        "All DSO's that have an observation records will be indicated with a check mark on the main " +"" +
                        "screen and there is a settings option to show only DSO's that you haven't previously observed.\n\n" +
                        "The date, time and observing location will automatically be filled out.\n\n" +
                        "Aside from the observation notes field at the bottom, all other fields will automatically populated " +
                        "to match the entries from your last saved record.";

            case 4:
                infoText = "The Messier Field Guide is copyright 2018 Shiny Objects, LLC\n\n\n" +
                        "Attributions:\n\n " +
                        "The constellation charts are from the International Astronomical " +
                        "Union and Sky & Telescope Magazine and are released under the Creative " +
                        "Commons Attribution 3.0 Unported License and have been modified to have " +
                        "a dark background.  A copy of this license " +
                        "is available at https://creativecommons.org/licenses/by/3.0/\n\n\n" +
                        "This application includes the following open source software:\n\n" +
                        "opencv v.3.10\nLicensed under the Apache License version 2.0.\n\n" +
                        "Joda-Time v2.3\nLicensed under the Apache License version 2.0.\n\n" +
                        "PhotoView v2.1.3\nLicensed under the Apache License version 2.0.\n\n"+
                        "The Apache License version 2.0 is an open source software license released " +
                        "by the Apache Software Foundation.  A copy of this license is available at " +
                        "http://www.apache.org/licenses/LICENSE-2.0\n\n\n" +
                        "Note: the Night Sky Guide is not currently copyrighted as open source.";
        }

        appInfo.setText(infoText);
    }

}
