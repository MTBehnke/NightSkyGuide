package com.mikesrv9a.nightskyguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ObservationAddEditFragment extends Fragment {

    private DSObject dsObject;  // dsObject to display
    SQLiteDatabase observationDB;
    String dsoId;

    public interface SaveCompletedListener {
        // called when save FAB is clicked
        //void onDSObjectSelected(DSObject dsObject);
        void onObservationSaved();
    }

    SaveCompletedListener listener;

    private TextView objectIdTextView;  // displays dsObject's ID
    private TextView nameTextView; // displays dsObject's Common Name
    private TextInputLayout dateTextInputLayout; // input for date/time
    private TextInputLayout locationTextInputLayout; // input for location
    private TextInputLayout seeingTextInputLayout; // input for seeing conditions
    private TextInputLayout transparencyTextInputLayout; // input for transparency conditions
    private TextInputLayout telescopeTextInputLayout; // input for telescope
    private TextInputLayout eyepieceTextInputLayout; // input for eyepiece
    private TextInputLayout powerTextInputLayout;  // input for power/magnification
    private TextInputLayout filterTextInputLayout; // input for filter
    private TextInputLayout notesTextInputLayout; // input for notes

    private FloatingActionButton saveObservationFAB; // save observation record FAB


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);

        // get Bundle of arguments then extract the dsObject
        Bundle arguments = getArguments();
        if (arguments != null)
            dsObject = arguments.getParcelable("dsObjectArrayListItem");

        View view = inflater.inflate(R.layout.fragment_add_edit_observ, container, false);
        objectIdTextView = (TextView) view.findViewById(R.id.objectIdTextView);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        dateTextInputLayout = (TextInputLayout) view.findViewById(R.id.dateTextInputLayout);
        locationTextInputLayout = (TextInputLayout) view.findViewById(R.id.locationTextInputLayout);
        seeingTextInputLayout = (TextInputLayout) view.findViewById(R.id.seeingTextInputLayout);
        transparencyTextInputLayout = (TextInputLayout) view.findViewById(R.id.transparencyTextInputLayout);
        telescopeTextInputLayout = (TextInputLayout) view.findViewById(R.id.telescopeTextInputLayout);
        eyepieceTextInputLayout = (TextInputLayout) view.findViewById(R.id.eyepieceTextInputLayout);
        powerTextInputLayout = (TextInputLayout) view.findViewById(R.id.powerTextInputLayout);
        filterTextInputLayout = (TextInputLayout) view.findViewById(R.id.filterTextInputLayout);
        notesTextInputLayout = (TextInputLayout) view.findViewById(R.id.notesTextInputLayout);

        // set initial values of text entry fields
        objectIdTextView.setText(dsObject.getDsoObjectID());
        nameTextView.setText(dsObject.getDsoName());
        String currentDateTime = DateFormat.getInstance().format(new Date());
        dateTextInputLayout.getEditText().setText(currentDateTime);

        // set FloatingActionButton's event listener
        saveObservationFAB = (FloatingActionButton) view.findViewById(R.id.saveFloatingActionButton);
        saveObservationFAB.setOnClickListener(saveFABClicked);
        saveObservationFAB.show();

        return view;
    }

    // responds to event generated when save button clicked
    private final View.OnClickListener saveFABClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // hide the virtual keyboard
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(),0);
            addObservation();  // save observation to the database
        }
    };

    // temp code to add entries into observation database
    public void addObservation() {
        Context context = getActivity();
        observationDB = new ObservationDatabaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ObserveRecordsSchema.ObsTable.Cols.DsoID, dsObject.getDsoObjectID());
        values.put(ObserveRecordsSchema.ObsTable.Cols.ObsDate, dateTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Location, locationTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Seeing, seeingTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Transparency, transparencyTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Telescope, telescopeTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Eyepiece, eyepieceTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Power, powerTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Filter, filterTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Notes, notesTextInputLayout.getEditText().getText().toString());
        observationDB.insert(ObserveRecordsSchema.ObsTable.NAME, null, values);
        observationDB.close();
        Toast.makeText(context, "Observation Saved", Toast.LENGTH_LONG).show();
        listener.onObservationSaved();
    }

    // set AddEditFABListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (SaveCompletedListener) context;
    }

    // remove AddEditFABListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

}
