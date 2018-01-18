package com.mikesrv9a.nightskyguide;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

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
        ImageView imageView = (ImageView)findViewById(R.id.app_info_image);

        switch (appInfoKey) {
            case 1:
                try {
                    imageView.setImageBitmap(BitmapFactory.decodeStream(this.getAssets().open("images/DSObject Key.png")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                infoText = "\n\nDSO Observing Tips\n\n" +
                        "Sorting by Altitude - Setting First will sort the list starting with the objects lowest in the western sky (setting), " +
                        "to those highest in the sky, then continuing to those lowest in the eastern sky (rising). For observing events " +
                        "or programs, such as a Messier Marathon, this sort order can help you observe objects before they set below the horizon.\n\n" +
                        "If casual observing, sorting by Altitude - Highest First will sort the list highest to lowest.  Generally objects that " +
                        "are low in the sky, at altitudes of 20° or less, can be difficult to observe. " +
                        "Objects higher in the sky provide better views because you are looking through less atmosphere.\n\n" +
                        "Brighter objects, those with lower magnitude values, are generally easier to observe. DSOs with magnitudes of 7 and lower are " +
                        "indicated with a solid circle symbol adjacent to the magnitude to help identify brighter targets.\n\n" +
                        "With some telescopes, particularly dobsonians, finding objects that are near the zenith (straight up) can be difficult. " +
                        "For objects that are at an altitude of 80° or higher it may be easier to observe lower objects for a little while. " +
                        "As the Earth turns under the night sky, those higher objects will soon be a little lower.\n";
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
                        "Sky Atlas 2000 refers to Sky Atlas 2000.0 by Tirion & Sinnott\n\n" +
                        "Clicking on the + icon will allow you to create a record of your observations, including " +
                        "information generally required for various observing programs, such as those sponsored " +
                        "by the Astronomical League.\n\n";
                break;
            case 3:
                infoText = "Astronomical Seeing:\n\n" +
                        "1 - Severely Disturbed Skies:\nEven low power views are uselessly shaky.\n\n" +
                        "2 - Poor:\nLow power images are pretty steady, but medium powers are not.\n\n" +
                        "3 - Good:\nYou can use about half the useful magnification of your scope. High powers produce " +
                        "fidgety planets.\n\n" +
                        "4 - Excellent:\nMedium powers are crisp and stable. High powers are good, but a little soft.\n\n" +
                        "5 - Superb:\nAny power eyepiece produces a good, crisp image.\n\n\n" +
                        "Transparency Scale:\n\n" +
                        "0 - Do Not Observe:\nCompletely cloudy or precipitating.\n\n" +
                        "1 - Very Poor:\nMostly cloudy.\n\n" +
                        "2 - Poor:\nPartly cloudy or heavy haze. 1 or 2 Little Dipper stars visible.\n\n" +
                        "3 - Somewhat Clear:\nCirrus or moderate haze. 3 or 4 Little Dipper stars visible.\n\n" +
                        "4 - Partly Clear:\nSlight haze. 4 or 5 Little Dipper stars visible.\n\n" +
                        "5 - Clear:\nNo clouds. Milky Way visible with averted vision. 6 Little Dipper stars visible.\n\n" +
                        "6 - Very Clear:\nMilky Way and M31 visible. 7 Little Dipper stars visible.\n\n" +
                        "7 - Extremely Clear:\nM33 and/or M81 visible.";
                break;
            case 4:
                infoText = "Night Sky Guide v1.0\n" +
                        "Copyright 2018 Shiny Objects, LLC\n\n\n" +
                        "Privacy Policy:\n\nWe don’t collect, transmit or store your data, period.\n\n" +
                        "All data used by this app, including location information as enabled by ‘Use GPS/Network Location’ or entered by you, " +
                        "is stored locally on your device.  If you choose to ‘Export Log to CSV’, this data will be written to a file stored " +
                        "locally on your device.\n\n\n" +
                        "Software License:\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\"); " +
                        "you may not use this file except in compliance with the License. " +
                        "You may obtain a copy of the License at\n\n" +
                        "http://www.apache.org/licenses/LICENSE-2.0\n\n" +
                        "Unless required by applicable law or agreed to in writing, software " +
                        "distributed under the License is distributed on an \"AS IS\" BASIS, " +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. " +
                        "See the License for the specific language governing permissions and " +
                        "limitations under the License.\n\n\n" +
                        "Open source code available at:\n" +
                        "https://github.com/MTBehnke/NightSkyGuide\n\n\n"+
                        "Attributions:\n\n" +
                        "The constellation charts are from the International Astronomical " +
                        "Union and Sky & Telescope Magazine and are released under the Creative " +
                        "Commons Attribution 3.0 Unported License and have been modified to have " +
                        "a dark background.  A copy of this license " +
                        "is available at https://creativecommons.org/licenses/by/3.0/\n\n\n" +
                        "This application includes the following open source software:\n\n" +
                        "opencv v.3.10\nLicensed under the Apache License version 2.0.\n\n" +
                        "Joda-Time v2.3\nCopyright ©2002-2017 Joda.org\nLicensed under the Apache License version 2.0.\n\n" +
                        "PhotoView v2.1.3\nCopyright 2017 Chris Banes\nLicensed under the Apache License version 2.0.\n\n"+
                        "The Apache License version 2.0 is an open source software license released " +
                        "by the Apache Software Foundation.  A copy of this license is available at " +
                        "http://www.apache.org/licenses/LICENSE-2.0.";
        }

        appInfo.setText(infoText);
    }

}
