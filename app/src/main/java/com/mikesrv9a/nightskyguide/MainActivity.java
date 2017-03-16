package com.mikesrv9a.nightskyguide;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

        // NOTE - tablet layout not working ***
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
    public void onDSObjectSelected(DSObject dsObjectSelected) {
        if (findViewById(R.id.fragmentContainer) != null)  // phone
            displayDSObject(dsObjectSelected, R.id.fragmentContainer);
        else {  // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();
        }
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