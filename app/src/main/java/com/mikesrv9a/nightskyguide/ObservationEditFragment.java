package com.mikesrv9a.nightskyguide;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class ObservationEditFragment extends Fragment  {

    private Observation observation;  // observation to display
    SQLiteDatabase observationDB;

    public interface SaveCompletedListener {
        // called when save FAB is clicked
        void onObservationSaved();
    }

    SaveCompletedListener listener;

    private TextView objectIdTextView;  // displays dsObject's ID
    private TextInputLayout dateTextInputLayout; // input for date
    private TextInputEditText dateEditText;  // actual edit text portion of inputlayout
    private TextInputLayout timeTextInputLayout; // input for time
    private TextInputEditText timeEditText;  // actual edit text portion of inputlayout
    private TextInputLayout locationTextInputLayout; // input for location
    private TextInputLayout seeingTextInputLayout; // input for seeing conditions
    private TextInputLayout transparencyTextInputLayout; // input for transparency conditions
    private TextInputLayout telescopeTextInputLayout; // input for telescope
    private TextInputLayout eyepieceTextInputLayout; // input for eyepiece
    private TextInputLayout powerTextInputLayout;  // input for power/magnification
    private TextInputLayout filterTextInputLayout; // input for filter
    private TextInputLayout notesTextInputLayout; // input for notes

    DecimalFormat df = new DecimalFormat("#.0000");
    DateTime calendar;
    DateTime newDate;
    LocalTime newTime;
    DateTimeFormatter dateFormat = DateTimeFormat.forPattern("M/d/yy");
    DateTimeFormatter timeFormat = DateTimeFormat.forPattern("h:mm a");
    DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("M/d/yy h:mm a");

    @Override
    public View onCreateView(
            LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)  {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        // get Bundle of arguments then extract the observation
        Bundle arguments = getArguments();
        if (arguments != null)
            observation = arguments.getParcelable("observationArrayListItem");                 //   ************

        View view = inflater.inflate(R.layout.fragment_add_edit_observ, container, false);
        objectIdTextView = (TextView) view.findViewById(R.id.objectIdTextView);
        //nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        dateTextInputLayout = (TextInputLayout) view.findViewById(R.id.dateTextInputLayout);
        dateEditText = (TextInputEditText) view.findViewById(R.id.editDateText);
        timeTextInputLayout = (TextInputLayout) view.findViewById(R.id.timeTextInputLayout);
        timeEditText = (TextInputEditText) view.findViewById(R.id.editTimeText);
        locationTextInputLayout = (TextInputLayout) view.findViewById(R.id.locationTextInputLayout);
        seeingTextInputLayout = (TextInputLayout) view.findViewById(R.id.seeingTextInputLayout);
        transparencyTextInputLayout = (TextInputLayout) view.findViewById(R.id.transparencyTextInputLayout);
        telescopeTextInputLayout = (TextInputLayout) view.findViewById(R.id.telescopeTextInputLayout);
        eyepieceTextInputLayout = (TextInputLayout) view.findViewById(R.id.eyepieceTextInputLayout);
        powerTextInputLayout = (TextInputLayout) view.findViewById(R.id.powerTextInputLayout);
        filterTextInputLayout = (TextInputLayout) view.findViewById(R.id.filterTextInputLayout);
        notesTextInputLayout = (TextInputLayout) view.findViewById(R.id.notesTextInputLayout);

        // set initial values of text entry fields
        try {
            calendar = dateTimeFormat.parseDateTime(observation.getObsDate());
        }
        catch (IllegalArgumentException e) {
            calendar = new DateTime();
        }
        objectIdTextView.setText(observation.getObsDsoID());
        String currentDate = calendar.toString(dateFormat);
        dateTextInputLayout.getEditText().setText(currentDate);
        String currentTime = calendar.toString(timeFormat);
        timeTextInputLayout.getEditText().setText(currentTime);
        String location = observation.getObsLocation();
        locationTextInputLayout.getEditText().setText(location);
        String seeing = observation.getObsSeeing();
        seeingTextInputLayout.getEditText().setText(seeing);
        String transparency = observation.getObsTransparency();
        transparencyTextInputLayout.getEditText().setText(transparency);
        String telescope = observation.getObsTelescope();
        telescopeTextInputLayout.getEditText().setText(telescope);
        String eyepiece = observation.getObsEyepiece();
        eyepieceTextInputLayout.getEditText().setText(eyepiece);
        String power = observation.getObsPower();
        powerTextInputLayout.getEditText().setText(power);
        String filter = observation.getObsFilter();
        filterTextInputLayout.getEditText().setText(filter);
        String notes = observation.getObsNotes();
        notesTextInputLayout.getEditText().setText(notes);


        dateEditText.setOnClickListener(dateClicked);
        timeEditText.setOnClickListener(timeClicked);

        return view;
    }

    // responds to event generated when save button clicked
    private final View.OnClickListener saveFABClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // hide the virtual keyboard
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getView().getWindowToken(),0);
            saveObservation();  // save observation to the database
        }
    };

    private final View.OnClickListener dateClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calendar = dateFormat.parseDateTime(dateTextInputLayout.getEditText().getText().toString());
            DatePickerDialog datePicker = new DatePickerDialog(getContext(), dateListener, calendar.getYear(), calendar.getMonthOfYear(), calendar.getDayOfMonth());
            datePicker.show();
        };
    };

    // set date and time onfocuschange listeners
    final OnDateSetListener dateListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker dateListener, int y, int m, int d) {
            newDate = new DateTime().withDate(y, m+1, d);      // need to add one to month value
            String currentDate = newDate.toString(dateFormat);
            dateTextInputLayout.getEditText().setText(currentDate);
        }
    };

    private final View.OnClickListener timeClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            calendar = timeFormat.parseDateTime(timeTextInputLayout.getEditText().getText().toString());
            TimePickerDialog timePicker = new TimePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, timeListener, calendar.getHourOfDay(), calendar.getMinuteOfHour(), false);
            timePicker.show();
        };
    };

    // set date and time onfocuschange listeners
    final TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timeListener, int h, int m) {
            newTime = new LocalTime(h, m);
            String currentTime = newTime.toString(timeFormat);
            timeTextInputLayout.getEditText().setText(currentTime);
        }
    };

    // add entries into observation database
    public void saveObservation() {
        Integer recordNum = observation.getObsDBid();
        String saveDate = dateTextInputLayout.getEditText().getText().toString() + " " + timeTextInputLayout.getEditText().getText().toString();
        Context context = getActivity();
        observationDB = new ObservationDatabaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ObserveRecordsSchema.ObsTable.Cols.DsoID, observation.getObsDsoID());
        values.put(ObserveRecordsSchema.ObsTable.Cols.ObsDate, saveDate);
        values.put(ObserveRecordsSchema.ObsTable.Cols.Location, locationTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Seeing, seeingTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Transparency, transparencyTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Telescope, telescopeTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Eyepiece, eyepieceTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Power, powerTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Filter, filterTextInputLayout.getEditText().getText().toString());
        values.put(ObserveRecordsSchema.ObsTable.Cols.Notes, notesTextInputLayout.getEditText().getText().toString());
        observationDB.update(ObserveRecordsSchema.ObsTable.NAME, values, "_id=" + recordNum, null);
        observationDB.close();
        Toast.makeText(context, "Observation Changes Saved", Toast.LENGTH_LONG).show();
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

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_add_edit_observ_menu, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveObservation();
                return true;
            case R.id.app_info_add_edit:
                Intent info = new Intent(getActivity(), AppInfoActivity.class);
                info.putExtra("appInfoKey", 3);
                startActivity(info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
