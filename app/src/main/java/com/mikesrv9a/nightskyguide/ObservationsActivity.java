package com.mikesrv9a.nightskyguide;


import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.List;

public class ObservationsActivity extends AppCompatActivity implements ObservationsFragment.ObservationsFragmentListener {

    private ObservationsFragment observationsFragment; // displays dsObject list
    final int REQUEST_STORAGE = 3;


    // display ObservationsFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            // create ObservationsFragment
            observationsFragment = new ObservationsFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentObservationsContainer, observationsFragment);
            transaction.commit();  // display ObservationsFragment
        }
    }

    //display ObservationDetailFragment for selected observation
    @Override
    public void onObservationSelected(Observation observationSelected) {
        displayObservation(observationSelected, R.id.fragmentObservationsContainer);
    }

    // display a dsObject
    private void displayObservation(Observation observationSelected, int viewID) {
        ObservationDetailFragment observationDetailFragment = new ObservationDetailFragment();

        // specify dsObject as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("observationArrayListItem", observationSelected);
        observationDetailFragment.setArguments(arguments);


        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, observationDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

    /*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        //Toast.makeText(this, "Result", Toast.LENGTH_LONG);
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    } */

}