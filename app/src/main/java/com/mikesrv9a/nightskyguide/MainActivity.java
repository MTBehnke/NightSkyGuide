package com.mikesrv9a.nightskyguide;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.lang.String;

public class MainActivity extends AppCompatActivity
    implements DSObjectsFragment.DSObjectsFragmentListener {

    // key for storing a dsObject's Uri in a Bundle passed to a fragment
    public static final String DSOBJECTDB_URI = "dsobject_uri";

    private DSObjectsFragment dsObjectsFragment; // displays dsObject list

    // display DSObjectFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if layout contains fragmentContainer, the phone layout is in use;
        // create and display a DSObjectsFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create DSObjectsFragment
            dsObjectsFragment = new DSObjectsFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, dsObjectsFragment);
            transaction.commit();  // display DSObjectsFragment
        } else {
            dsObjectsFragment =
                    (DSObjectsFragment) getSupportFragmentManager().
                            findFragmentById(R.id.dsObjectsFragment);
        }
    }

    // display DSObjectFragment for selected dsObject
    @Override
    public void onDSObjectSelected(Uri dsObjectUri) {
        if (findViewById(R.id.fragmentContainer) != null)  // phone
            displayDSObject(dsObjectUri, R.id.fragmentContainer);
        else {  // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();
        }
    }

    // display a dsObject
    private void displayDSObject(Uri dsObjectUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify dsObject's Uri as an argument to the DSObjectFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(DSOBJECTDB_URI, dsObjectUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

}