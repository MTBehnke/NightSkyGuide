package com.mikesrv9a.nightskyguide;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AppInfoActivity extends AppCompatActivity {

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
        TextView appInfo = (TextView)findViewById(R.id.app_info_text);
        String infoText = "Messier Field Guide v0.1 (beta)\n" +
                "Copyright Michael Behnke 2017\n\n" +
                "The main page displays each of the 110 Messier Object's, including:\n" +
                "∙ DSO ID\n∙ Common Name\n"+
                "∙ Constellation\n∙ DSO Type\n" +
                "∙ Magnitude (with ● if Mag 7.0 or brighter)\n∙ Apparent Size (arc-mins)\n" +
                "∙ Current Altitude (with ▲ or ▼ for rising/setting)\n∙ Current Azimuth.\n\n" +
                "The list of objects is sorted based on altitude, with setting "+
                "objects listed first.  A negative altitude indicates the object" +
                "is below the horizon.\n\n" +
                "Clicking on any object in the list will open a detail page "+
                "providing more information about the object, including an "+
                "associated constellation chart.\n\n" +
                "PSA refers to Sky & Telescope's Pocket Sky Atlas\n\n" +
                "OITH refers to Objects in the Heavens ed.5.2 by Peter Birren\n\n" +
                "The constellation charts are by the " +
                "International Astronomical Union and Sky & Telescope Magazine and "+
                "are released under the Creative Commons Attribution 3.0 Unported License.";

        appInfo.setText(infoText);
    }

}
