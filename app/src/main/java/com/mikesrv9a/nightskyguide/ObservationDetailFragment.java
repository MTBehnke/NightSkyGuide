// Fragment subclass that displays one observation's details

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import androidx.fragment.app.Fragment;
//import android.support.v7.app.AlertDialog;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ObservationDetailFragment extends Fragment {

    private Observation observation;

    private TextView objectIdTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView seeingTextView;
    private TextView transparencyTextView;
    private TextView telescopeTextView;
    private TextView eyepieceTextView;
    private TextView powerTextView;
    private TextView filterTextView;
    private TextView notesTextView;

    public interface DeleteCompletedListener {
        // called when save FAB is clicked
        void onObservationDeleted();
    }

    public interface EditObservationListener {
        // called when edit menu icon is clicked
        void editObservationButtonClicked(Observation observation);
    }

    DeleteCompletedListener deleteCompletedListener;
    EditObservationListener editObservationListener;

    // called when DetailFragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);  // this fragment has no menu items to display

        // get Bundle of arguments then extract the observation
        Bundle arguments = getArguments();
        if (arguments != null)
            observation = arguments.getParcelable("observationArrayListItem");

        // inflate ObservationFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_observ_details, container, false);

        // get the EditTexts
        objectIdTextView = (TextView) view.findViewById(R.id.objectIdObsTextView);
        dateTextView = (TextView) view.findViewById(R.id.obsDateText);
        locationTextView = (TextView) view.findViewById(R.id.obsLocationText);
        seeingTextView = (TextView) view.findViewById(R.id.obsSeeingText);
        transparencyTextView = (TextView) view.findViewById(R.id.obsTransparencyText);
        telescopeTextView = (TextView) view.findViewById(R.id.obsTelescopeText);
        eyepieceTextView = (TextView) view.findViewById(R.id.obsEyepieceText);
        powerTextView = (TextView) view.findViewById(R.id.obsPowerText);
        filterTextView = (TextView) view.findViewById(R.id.obsFilterText);
        notesTextView = (TextView) view.findViewById(R.id.obsNotesText);


        // set the TextViews
        objectIdTextView.setText(observation.getObsCatalogue());
        dateTextView.setText(observation.getObsDate());
        locationTextView.setText(observation.getObsLocation());
        seeingTextView.setText(observation.getObsSeeing());
        transparencyTextView.setText(observation.getObsTransparency());
        telescopeTextView.setText(observation.getObsTelescope());
        eyepieceTextView.setText(observation.getObsEyepiece());
        powerTextView.setText(observation.getObsPower());
        filterTextView.setText(observation.getObsFilter());
        notesTextView.setText(observation.getObsNotes());

        return view;
    }

    // set AddEditFABListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        deleteCompletedListener = (DeleteCompletedListener) context;
        editObservationListener = (EditObservationListener) context;
    }

    // remove AddEditFABListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        deleteCompletedListener = null;
        editObservationListener = null;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_observ_detail_menu, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_observation:
                editObservationListener.editObservationButtonClicked(observation);
                return true;
            case R.id.delete_observation:
                displayAlertDialog("Permanently delete this observation?");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                deleteObsRecord();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void deleteObsRecord() {
        Integer recordId = observation.getObsDBid();
        ObservationDatabaseHelper observationDatabaseHelper = new ObservationDatabaseHelper(getContext());
        observationDatabaseHelper.deleteObservation(recordId);
        deleteCompletedListener.onObservationDeleted();
    }

}

