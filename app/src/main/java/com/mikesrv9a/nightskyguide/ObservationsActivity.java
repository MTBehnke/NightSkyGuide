package com.mikesrv9a.nightskyguide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class ObservationsActivity extends AppCompatActivity implements ObservationsFragment.ObservationsFragmentListener,
        ObservationDetailFragment.DeleteCompletedListener, ObservationDetailFragment.EditObservationListener, ObservationEditFragment.SaveCompletedListener {

    private ObservationsFragment observationsFragment;
    private ObservationDetailFragment observationDetailFragment;
    private ObservationEditFragment observationEditFragment;

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
        observationDetailFragment = new ObservationDetailFragment();

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

    // return to DetailFragment after observation record saved
    @Override
    public void onObservationDeleted() {
        getSupportFragmentManager().popBackStack();
    }

    // display ObservationEditFragment for selected dsObject
    public void editObservationButtonClicked(Observation observationSelected) {
        displayObservEdit(observationSelected, R.id.fragmentObservationsContainer);
    }

    // display edit fragment
    private void displayObservEdit(Observation observationSelected, int viewID) {
        observationEditFragment = new ObservationEditFragment();

        // specify dsObject as an argument to the edit Fragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("observationArrayListItem", observationSelected);
        observationEditFragment.setArguments(arguments);


        // use a FragmentTransaction to display the edit Fragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, observationEditFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes edit Fragment to display
    }

    // return to DetailFragment after observation record saved
    @Override
    public void onObservationSaved() {
        Intent refresh = new Intent(this, ObservationsActivity.class);
        startActivity(refresh);
        this.finish();
    }

}