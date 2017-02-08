package com.mikesrv9a.nightskyguide;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import java.lang.String;


public class MainActivity extends AppCompatActivity
    implements DSObjectsFragment.DSObjectsFragmentListener
    /*,   *** *** Suspend all database add/edit/delete capabilities
    DetailFragment.DetailFragmentListener,
    AddEditFragment.AddEditFragmentListener*/
    {

    // key for storing a dsObject's Uri in a Bundle passed to a fragment
    public static final String DSOBJECT_URI = "dsobject_uri";

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

/*  *** Suspend all database add/edit/delete capabilities
    // display AddEditFragment to add a new dsObject
    @Override
    public void onAddDSObject() {
        if (findViewById(R.id.fragmentContainer) != null)  // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else  // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }
*/

    // display a dsObject
    private void displayDSObject(Uri dsObjectUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify dsObject's Uri as an argument to the DSObjectFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(DSOBJECT_URI, dsObjectUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

/*  *** Suspend all database add/edit/delete capabilities
    // display fragment for adding a new or editing an existing dsObject
    private void displayAddEditFragment(int viewID, Uri dsObjectUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing dsObject, provide dsObjectUri as an argument
        if (dsObjectUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DSOBJECT_URI, dsObjectUri);
            addEditFragment.setArguments(arguments);
        }

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes AddEditFragment to display
    }

    // return to dsObject list when displayed DSObject is deleted
    @Override
    public void onDSObjectDeleted() {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        ;
        dsObjectsFragment.updateDSObjectList();  // refresh dsObjects
    }

    // display the AddEditFragment to edit an existing dsObject
    @Override
    public void onEditDSObject(Uri dsObjectUri) {
        if (findViewById(R.id.fragmentContainer) != null)  // phone
            displayAddEditFragment(R.id.fragmentContainer, dsObjectUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, dsObjectUri);
    }

    // update GUI after new dsObject or updated dsObject saved
    @Override
    public void onAddEditCompleted(Uri dsObjectUri) {
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        dsObjectsFragment.updateDSObjectList();  // refresh dsObjects

        if (findViewById(R.id.fragmentContainer) == null) { // tablet
            // removes top of back back stack
            getSupportFragmentManager().popBackStack();

            // on tablet, display dsObject that was just added or edited
            displayDSObject(dsObjectUri, R.id.rightPaneContainer);
        }
    }
*/
}