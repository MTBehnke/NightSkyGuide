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
                infoText = "Rise, transit and set times are shown for the current time zone of your phone or tablet, " +
                        "and are based on the latitude and longitude designated on the Settings page. " +
                        "Coordinate and time calculations should generally be less than 1° and a few minutes, which should " +
                        "suffice for finding objects in the night sky but may vary slightly from published sources.\n\n" +
                        "The constellation charts will provide a general location of many of the DSOs, however " +
                        "not all DSOs listed are shown in these charts and " +
                        "it is likely that you will need a sky atlas to find many of these objects.  The page numbers " +
                        "for three common atlases are listed on the detail pages.\n\n" +
                        "PSA refers to Sky & Telescope's Pocket Sky Atlas\n\n" +
                        "OITH refers to Objects in the Heavens ed.5.2 by Peter Birren\n\n" +
                        "Sky Atlas 2000 refers to Sky Atlas 2000.0 by Tirion & Sinnott\n\n" +
                        "Clicking on the + icon will allow you to create a record of your observations, including " +
                        "information generally required for various observing programs, such as those sponsored " +
                        "by the Astronomical League.\n";
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
                        "7 - Extremely Clear:\nM33 and/or M81 visible.\n\n\n" +
                        "Additional guidance on observing from the Astronomical League can be found in the \"App Info\" " +
                        "page accessible at the top of the Observation Log page.\n";
                break;
            case 4:
                infoText = "Night Sky Guide v2.0\n" +
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
                        "This application makes use of the SIMBAD database, operated at "+
                        "CDS, Strasbourg, France.\n\n" +
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
                break;
            case 5:
                infoText = "The Observation Log lists the observation records you have saved and can be exported to a CSV file " +
                        "stored on your Android device.  DSO's which are included in one of the Astronomical League's " +
                        "Observing Program's are indicated with a \"AL:\" followed by the program designation.\n\n" +
                        "Please visit the Astronomical League's website for detailed requirements of each program.\n\n" +
                        "The following guidance on observing has been provided by the Astronomical League:\n\n" +
                        "How To Observe\n\n" +
                        "The reason for the object description requirement is to educate the observer to be a more detail oriented observer; " +
                        "to actually \"observe\" the object and not just \"see\" it. In the end, to become a better observer. You cannot learn " +
                        "to \"observe\" from a book. It can only be mastered with eyeball to eyepiece.\n" +
                        "\n" +
                        "The description should tell me what makes that object different from all the rest. This is possible in any size of " +
                        "optical instrument. You can't say that M70 looks like M13 even though both are globular clusters. You wouldn't describe " +
                        "the Great Orion Nebula M42 the same as the Crab Nebula M1, even if they both are nebula and appear as fuzzy clouds in " +
                        "the eyepiece. M31 looks nothing like M65 even though both are spiral galaxies seen at a fairly similar angle.\n" +
                        "\n" +
                        "The intent of the requirement of the object descriptions is to have you pick out details to the best of your ability. " +
                        "These details are what make the object unique.\n" +
                        "\n" +
                        "Things like:\n" +
                        "-\tIs the object round, oval, or irregular shaped?\n" +
                        "-\tIf the object is oval shaped, how stretched out, or oval, is it; ie. 2 times longer than wide, 4 times longer than wide, " +
                        "even more? Is it basically just a little streak?\n" +
                        "-\tdoes the galaxy or nebula have sharp edges or does it fade gradually away to nothing? If it fades away to nothing, " +
                        "does averted vision seem to increase its size?\n" +
                        "-\tdoes the galaxy have a brighter core area, or is it an even brightness across the entire surface? Is the brighter " +
                        "core a large area, or does it come to a stellar point?\n" +
                        "-\tIn globulars, is the central area large and full, or very pointed and stellar-like?\n" +
                        "-\tin open clusters, are all the stars the same magnitude? Can you guess at the number of stars visible?\n" +
                        "-\tDoes the open cluster stand out well against the starry background, or does it blend in making it difficult to " +
                        "determine the edges?\n" +
                        "-\tin nebula, are there any denser or lighter areas? are there any stars involved in the nebula?\n" +
                        "-\twhat else is in the field of view that is interesting? other deep sky objects? a nice double star? any colorful stars? " +
                        "is the field of view densely packed with stars?, did a satellite just pass through?, etc.\n" +
                        "-\tand of course, any other thoughts you have while viewing the object that might make it personal to this observation. " +
                        "After all, this is YOUR observing log.\n" +
                        "\n" +
                        "Yes, it seems like a lot of work and very tedious, but after a dozen or so observations, it becomes second nature to ask " +
                        "yourself these things. What you end up doing is training your eye to see detail in the objects. And after doing this, " +
                        "each object does indeed become unique in its own way.\n" +
                        " \n" +
                        "Also, remember that sky conditions are not the same as Seeing and Transparency.\n" +
                        "\n" +
                        "Seeing is how steady the atmosphere is. A good indicator of this is the twinkling stars. If stars are rock solid near " +
                        "the horizon, the Seeing is excellent. If the stars overhead are twinkling, then the Seeing is horrible. Seeing is what " +
                        "makes stars appear bloated and shimmery at higher magnifications. Seeing is also what makes very close double stars easy " +
                        "or difficult to separate.\n" +
                        "\n" +
                        "Transparency is how dark the sky actually is. Looking at nebula, galaxies, and planetary nebula are all affected by " +
                        "transparency. When you look at one of these objects, it appears gray. If the background sky is also gray " +
                        "(because of light pollution, water vapor, etc.), there is very little contrast between the object and the background " +
                        "sky and the object is difficult to see. If the background sky is black, you are looking at a gray object against a " +
                        "black sky and the contrast between the two is higher making it easier to see the object and pick out details. " +
                        "Most people determine how dark the sky really is by looking at the zenith and finding the faintest star they can pick " +
                        "out with their naked eye. They then look up the magnitude of that star and use that as their transparency rating.\n";
        }

        appInfo.setText(infoText);
    }

}
