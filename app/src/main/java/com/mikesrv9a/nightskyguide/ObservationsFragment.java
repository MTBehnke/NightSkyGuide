package com.mikesrv9a.nightskyguide;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ObservationsFragment extends Fragment {

    // callback method implemented by MainActivity
    public interface ObservationsFragmentListener {
        // called when observation is selected
        void onObservationSelected(Observation ObservationSelected);
    }

    Cursor data;
    ObservationDatabaseHelper observationsDB;

    public ArrayList<Observation> observationsArrayList = new ArrayList<>();  // observations to display in recyclerview - could be filtered
    public ArrayList<Observation> allObservationsArrayList = new ArrayList<>();  // all observations from database

    // used to inform ObservationActivity when an observation is selected
    ObservationsFragmentListener listener;

    ObservationsClickAdapter clickAdapter;

    Context context;

    // configures this fragment's GUI
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);  // fragment has menu items to display

        // inflate GUI and get reference to the RecyclerView
        View view = inflater.inflate(
                R.layout.fragment_observations, container, false);
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.observationsRecyclerView);

        // recyclerView should display items in a vertical list
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        // create recyclerView's adapter and item click listener
        clickAdapter = new ObservationsClickAdapter(observationsArrayList);
        clickAdapter.setOnEntryClickListener(new ObservationsClickAdapter.onEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                // stuff that will happen when a list item is clicked
                Observation ObservationSelected = observationsArrayList.get(position);
                listener.onObservationSelected(ObservationSelected);
            }
        });
        recyclerView.setAdapter(clickAdapter);

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes (temp disabled)
        //recyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allObservationsArrayList.clear();
        observationsArrayList.clear();
        observationsDB.close();
    }

    // set ObservationsFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ObservationsFragmentListener) context;
    }

    // remove ObservationsFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // load database and create dsObjectsArrayList
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        // Get observation data from database
        observationsDB = new ObservationDatabaseHelper(context);
        //observationDB.forceDatabaseReload(context);           // *** ELIMINATE FROM FINAL VERSION
        data = observationsDB.getObservations();

        //  creates Observation objects and adds to observationsArrayList
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            int objectIdCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.DsoID);
            int dateCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.ObsDate);
            int locationCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Location);
            int seeingCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Seeing);
            int transparencyCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Transparency);
            int telescopeCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Telescope);
            int eyepieceCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Eyepiece);
            int powerCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Power);
            int filterCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Filter);
            int notesCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Notes);
            while (!data.isAfterLast()) {
                String obsObjectID = data.getString(objectIdCol);
                String obsDate = data.getString(dateCol);
                String obsLocation = data.getString(locationCol);
                String obsSeeing = data.getString(seeingCol);
                String obsTransparency = data.getString(transparencyCol);
                String obsTelescope = data.getString(telescopeCol);
                String obsEyepiece = data.getString(eyepieceCol);
                String obsPower = data.getString(powerCol);
                String obsFilter = data.getString(filterCol);
                String obsNotes = data.getString(notesCol);

                // creates Observation objects
                Observation observation = new Observation(obsObjectID, obsDate, obsLocation, obsSeeing, obsTransparency,
                        obsTelescope, obsEyepiece, obsPower, obsFilter, obsNotes);
                allObservationsArrayList.add(observation);

                data.moveToNext();
            }
        }
    }

    /* display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dsobjects_menu, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info:
                Intent info = new Intent(getActivity(), AppInfoActivity.class);
                info.putExtra("appInfoKey", 1);
                startActivity(info);
                return true;
            case R.id.location_edit:
                Intent loc = new Intent(getActivity(), LocationActivity.class);
                startActivity(loc);
                return true;
            case R.id.settings_edit:
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }  */

    // update list on resume - primarily if user changed location or settings
    @Override
    public void onResume() {
        super.onResume();
        //setUserPreferences();
        updateArrayList();
    }

    /* stop runnable on pause
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateAltAz);
    } */

    /* update user latitude and longitude from preferences
    public void setUserPreferences() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("use_device_location", false)) {
            userLat = Double.parseDouble(preferences.getString("last_gps_lat", getString(R.string.default_latitude)));
            userLong = Double.parseDouble(preferences.getString("last_gps_long", getString(R.string.default_longitude)));}
        else {
            userLat = Double.parseDouble(preferences.getString("edit_text_pref_lat", getString(R.string.default_latitude)));
            userLong = Double.parseDouble(preferences.getString("edit_text_pref_long", getString(R.string.default_longitude)));}
        //Toast.makeText(getContext(), userLat + " / " + userLong, Toast.LENGTH_LONG).show();
        showObserved = preferences.getBoolean("pref_show_observed", false);
        showBelowHoriz = preferences.getBoolean("pref_show_below_horiz", false);
        sortPreference = preferences.getString("pref_sort_by", "1");
    }  */

    // update and sort arraylist for recyclerview based on user preferences
    public void updateArrayList() {
        observationsArrayList.clear();

        for (int counter = 0; counter < allObservationsArrayList.size(); counter++) {
            observationsArrayList.add(allObservationsArrayList.get(counter));
        }

        Collections.sort(observationsArrayList, new Comparator<Observation>() {
            @Override
            public int compare(Observation observation, Observation t1) {
                return Integer.valueOf(observation.obsDsoID.substring(1)).compareTo(Integer.valueOf(t1.obsDsoID.substring(1)));
            }
        });

        /*
        if (sortPreference.equals("1")) {
            Collections.sort(observationsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Double.compare(dsObject.getDsoSortAlt(), t1.getDsoSortAlt());
                }
            });
        }
        else if (sortPreference.equals("2")) {
            Collections.sort(observationsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Integer.valueOf(dsObject.dsoObjectID.substring(1)).compareTo(Integer.valueOf(t1.dsoObjectID.substring(1)));
                }
            });
        }
        else {
            Collections.sort(observationsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return dsObject.getDsoConst().compareTo(t1.getDsoConst());
                }
            });
        } */
        clickAdapter.notifyDataSetChanged();
    }
}