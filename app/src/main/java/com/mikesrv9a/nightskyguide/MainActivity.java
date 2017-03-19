package com.mikesrv9a.nightskyguide;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class MainActivity extends AppCompatActivity
    implements DSObjectsFragment.DSObjectsFragmentListener {

    private DSObjectsFragment dsObjectsFragment; // displays dsObject list

    // display DSObjectFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // create DSObjectsFragment
            dsObjectsFragment = new DSObjectsFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, dsObjectsFragment);
            transaction.commit();  // display DSObjectsFragment
        }
    }

    // display DSObjectFragment for selected dsObject
    @Override
    public void onDSObjectSelected(DSObject dsObjectSelected) {
        displayDSObject(dsObjectSelected, R.id.fragmentContainer);
    }

    // display a dsObject
    private void displayDSObject(DSObject dsObjectSelected, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify dsObject as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("dsObjectArrayListItem", dsObjectSelected);
        detailFragment.setArguments(arguments);


        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

}