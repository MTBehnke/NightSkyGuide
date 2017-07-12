package com.mikesrv9a.nightskyguide;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
    SharedPreferences preferences;
    DecimalFormat df = new DecimalFormat("#.0000");

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

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
        String location = getLocation();
        locationTextInputLayout.getEditText().setText(location);
        String seeing = preferences.getString("last_seeing", "");
        seeingTextInputLayout.getEditText().setText(seeing);
        String transparency = preferences.getString("last_transparency", "");
        transparencyTextInputLayout.getEditText().setText(transparency);
        String telescope = preferences.getString("last_telescope", "");
        telescopeTextInputLayout.getEditText().setText(telescope);
        String eyepiece = preferences.getString("last_eyepiece", "");
        eyepieceTextInputLayout.getEditText().setText(eyepiece);
        String power = preferences.getString("last_power", "");
        powerTextInputLayout.getEditText().setText(power);
        String filter = preferences.getString("last_filter", "");
        filterTextInputLayout.getEditText().setText(filter);


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
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("last_seeing", seeingTextInputLayout.getEditText().getText().toString());
        edit.putString("last_transparency", transparencyTextInputLayout.getEditText().getText().toString());
        edit.putString("last_telescope", telescopeTextInputLayout.getEditText().getText().toString());
        edit.putString("last_eyepiece", eyepieceTextInputLayout.getEditText().getText().toString());
        edit.putString("last_power", powerTextInputLayout.getEditText().getText().toString());
        edit.putString("last_filter", filterTextInputLayout.getEditText().getText().toString());
        edit.apply();
    }

    public String getLocation() {
        String location = "";
        if (preferences.getBoolean("use_device_location",false)) {
            Double gpsLat = Double.parseDouble(preferences.getString("last_gps_lat", getString(R.string.default_latitude)));
            Double gpsLong = Double.parseDouble(preferences.getString("last_gps_long", getString(R.string.default_longitude)));
            location = AstroCalc.convertLatToDMS(gpsLat) + " ,  " + AstroCalc.convertLongToDMS(gpsLong);
            // location = "Latitude:  " + df.format(gpsLat) + "   /   Longitude:  " + df.format(gpsLong);
            }
        else {
            String locationNum = preferences.getString("viewing_location", "1");
            switch (locationNum) {
                case "2":
                    location = preferences.getString("pref_location2", "");
                    break;
                case "3":
                    location = preferences.getString("pref_location3", "");
                    break;
                case "4":
                    location = preferences.getString("pref_location4", "");
                    break;
                case "5":
                    location = preferences.getString("pref_location5", "");
                    break;
                case "1":
                    location = preferences.getString("pref_location1", "");
                    break;
            }
        }
        return location;
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
            case R.id.app_info_add_edit:
                Intent info = new Intent(getActivity(), AppInfoActivity.class);
                info.putExtra("appInfoKey", 3);
                startActivity(info);
                return true;
            /*case R.id.location_edit:
                Intent loc = new Intent(getActivity(), LocationActivity.class);
                startActivity(loc);
                return true;
            case R.id.settings_edit:
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
