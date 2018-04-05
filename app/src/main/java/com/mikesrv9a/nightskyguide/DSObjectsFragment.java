// Fragment subclass that displays the alphabetical list of dsObjects

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class DSObjectsFragment extends Fragment {


    // callback method implemented by MainActivity
    public interface DSObjectsFragmentListener {
        // called when dsObject is selected
        void onDSObjectSelected(DSObject dsObjectSelected);
    }

    Cursor data;
    Cursor observations;
    DSObjectDatabaseHelper dsObjectsDB;
    SQLiteDatabase observationDB;

    public ArrayList<DSObject> dsObjectsArrayList = new ArrayList<>();  // dsObjects to display in recyclerview
    public ArrayList<DSObject> allDsObjectsArrayList = new ArrayList<>();  // all dsObjects from database
    public ArrayList<String> observedList = new ArrayList<>();

    // used to inform the MainActivity when a dsObject is selected
    DSObjectsFragmentListener listener;

    // handler to update dsoAlt and dsoAz for all DSObjects on regular interval
    Handler handler = new Handler();

    DSObjectsClickAdapter clickAdapter;

    Context context;

    // user preferences
    public double userLat;
    public double userLong;
    public boolean showObserved;
    public boolean showBelowHoriz;
    public int maxMagnitude;
    public String sortPreference;
    public Set<String> showObjectLists;

    // configures this fragment's GUI
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);  // fragment has menu items to display

        // inflate GUI and get reference to the RecyclerView
        View view = inflater.inflate(
                R.layout.fragment_dsobjects, container, false);
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.recyclerView);

        // recyclerView should display items in a vertical list
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity().getBaseContext()));

        // create recyclerView's adapter and item click listener
        clickAdapter = new DSObjectsClickAdapter(dsObjectsArrayList);
        clickAdapter.setOnEntryClickListener(new DSObjectsClickAdapter.onEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                // stuff that will happen when a list item is clicked
                DSObject DSObjectSelected = dsObjectsArrayList.get(position);
                listener.onDSObjectSelected(DSObjectSelected);
            }
        });
        recyclerView.setAdapter(clickAdapter);

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes (temp disabled)
        //recyclerView.setHasFixedSize(true);

        // initiate handler to update dsoAlt and dsoAz for all DSObjects
        handler.post(updateAltAz);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //handler.removeCallbacks(updateAltAz);
        dsObjectsDB.close();
        data.close();
        allDsObjectsArrayList.clear();
        dsObjectsArrayList.clear();
        observations.close();
        observationDB.close();
        observedList.clear();
        // need to add some error checking if attempt to close null cursor *********?????????
    }

    private Runnable updateAltAz = new Runnable() {
        public void run() {
            try {
                setUserPreferences();
                updateArrayList();
                clickAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 60000);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    };

    // set DSObjectsFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DSObjectsFragmentListener) context;
    }

    // remove DSOBjectsFragmentListener when Fragment detached
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

        // Get Observation data from database
        getObservationData();

        // Get DSObject data from database
        dsObjectsDB = new DSObjectDatabaseHelper(context);
        //dsObjectsDB.forceDatabaseReload(context);           // *** ELIMINATE FROM FINAL VERSION
        data = dsObjectsDB.getDSObjects();

        //  creates DSObject objects and adds to dsObjectArrayList
        if (data != null && data.getCount() > 0) {
            data.moveToFirst();
            int objectIdCol = data.getColumnIndex("object");
            int typeCol = data.getColumnIndex("type");
            int magCol = data.getColumnIndex("mag");
            int sizeCol = data.getColumnIndex("size");
            int distCol = data.getColumnIndex("distance");
            int raCol = data.getColumnIndex("ra");
            int decCol = data.getColumnIndex("dec");
            int constCol = data.getColumnIndex("const");
            int nameCol = data.getColumnIndex("name");
            int psaCol = data.getColumnIndex("psa");
            int oithCol = data.getColumnIndex("oith");
            int skyatlasCol = data.getColumnIndex("skyatlas");
            int catCol = data.getColumnIndex("catalogue");
            int progCol = data.getColumnIndex("obsprogram");
            setUserPreferences();  // need user's latitude and longitude
            while (!data.isAfterLast()) {
                String dsoObjectID = data.getString(objectIdCol);
                String dsoType = data.getString(typeCol);
                Double dsoMag = (data.isNull(magCol)) ? null : data.getDouble(magCol);
                String dsoSize = data.getString(sizeCol);
                String dsoDist = data.getString(distCol);
                Double dsoRA = data.getDouble(raCol);
                Double dsoDec = data.getDouble(decCol);
                String dsoConst = data.getString(constCol);
                String dsoName = data.getString(nameCol);
                String dsoPSA = data.getString(psaCol);
                String dsoOITH = data.getString(oithCol);
                String dsoSkyAtlas = data.getString(skyatlasCol);
                String dsoCatalogue = data.getString(catCol);
                String dsoObsProg = data.getString(progCol);

                //Checks observations to determine whether DSO has been observed
                Integer dsoObserved = 0;
                if (observedList.contains(dsoObjectID)) {
                    dsoObserved = 1;
                }

                // creates DSObjects
                DSObject dsObject = new DSObject(dsoObjectID, dsoType, dsoMag, dsoSize, dsoDist,
                        dsoRA, dsoDec, dsoConst, dsoName, dsoPSA, dsoOITH, dsoSkyAtlas, dsoCatalogue, dsoObsProg, dsoObserved);
                dsObject.setDsoAltAz(userLat, userLong);
                allDsObjectsArrayList.add(dsObject);

                data.moveToNext();
            }

            // add planets as DSObjects  (1:Mercury thru 7: Neptune, skip 0:Earth)
            for (int planet = 1; planet < 8; planet++) {
                   DSObject dsObject = new DSObject(AstroCalc.planetName[planet],"PL",0.0,"","",null,null,
                           "",null,"","","","","",0);
                   dsObject.setPlanetCoords(planet);
                   dsObject.setDsoAltAz(userLat, userLong);
                   allDsObjectsArrayList.add(dsObject);
            }
        }
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_dsobjects_menu, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.dso_search).getActionView();
        searchView.setOnQueryTextListener(new OnSearchTextListener());
    }

    private class OnSearchTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            String query = s.toLowerCase();

            ArrayList<DSObject> filteredList = new ArrayList<>();
            for (DSObject object : allDsObjectsArrayList) {
                if (object.dsoObjectID.toLowerCase().contains(query)) {
                    filteredList.add(object);
                }
                else if (object.dsoName != null && object.dsoName.toLowerCase().contains(query)) {
                    filteredList.add(object);
                }
            }
            clickAdapter.replaceData(filteredList);
            return true;
        }
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
            case R.id.observations_fragment:
                Intent obs = new Intent(getActivity(), ObservationsActivity.class);
                startActivity(obs);
                return true;
            case R.id.settings_edit:
                Intent settings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settings);
                return true;
            /*case R.id.app_credits:
                Intent credits = new Intent(getActivity(), AppInfoActivity.class);
                credits.putExtra("appInfoKey", 4);
                startActivity(credits);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // update list on resume - primarily if user changed location or settings
    @Override
    public void onResume() {
        super.onResume();
        setUserPreferences();
        getObservationData();
        updateObservationInfo();
        updateArrayList();
        handler.postDelayed(updateAltAz, 60000);
    }

    // stop runnable on pause
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateAltAz);
    }

    // update user latitude and longitude from preferences
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
        maxMagnitude = Integer.valueOf(preferences.getString("pref_max_magnitude", "255"));
        sortPreference = preferences.getString("pref_sort_by", "1");
        showObjectLists = preferences.getStringSet("multi_pref_object_list", null);
        String selected = null;
        if (showObjectLists != null) {    // should never be null, included to prevent error flagging
            selected = showObjectLists.toString();
        }
    }

    // update and sort arraylist for recyclerview based on user preferences
    public void updateArrayList() {
        dsObjectsArrayList.clear();
        for (int counter = 0; counter < allDsObjectsArrayList.size(); counter++) {
            allDsObjectsArrayList.get(counter).setDsoAltAz(userLat, userLong);
            Double dsoAlt = allDsObjectsArrayList.get(counter).getDsoAlt();
            if ((allDsObjectsArrayList.get(counter).getDsoObserved() == 1 && showObserved == false) || (dsoAlt < 0 && showBelowHoriz == false)) {
            } // do nothing
            else if (allDsObjectsArrayList.get(counter).getDsoType().equals("DN")) {  // prevents error for null DsoMag in next else if
                if (maxMagnitude == 255) { dsObjectsArrayList.add(allDsObjectsArrayList.get(counter)); }
                else {} //do nothing
                }
            else if (allDsObjectsArrayList.get(counter).getDsoMag() > maxMagnitude) {
                } // do nothing
            else if (allDsObjectsArrayList.get(counter).dsoType.equals("PL") && !showObjectLists.contains("P")) {
                } // do nothing
            else if (!allDsObjectsArrayList.get(counter).dsoType.equals("PL") && allDsObjectsArrayList.get(counter).dsoObjectID.startsWith("M") && !showObjectLists.contains("M")) {
                } // do nothing
            else if (!allDsObjectsArrayList.get(counter).dsoType.equals("PL") && allDsObjectsArrayList.get(counter).dsoObjectID.startsWith("C") && !showObjectLists.contains("C")) {
                } // do nothing
            else {
                dsObjectsArrayList.add(allDsObjectsArrayList.get(counter));    // passes all filters - add to recyclerview
            }
        }
        if (sortPreference.equals("1")) {    // sort based on altitude - setting, then rising
            Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Double.compare(dsObject.getDsoSortAlt(), t1.getDsoSortAlt());
                }
            });
        }
        else if (sortPreference.equals("2")) {    // sort based on altitude - highest to lowest
            Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Double.compare(dsObject.getDsoAlt(), t1.getDsoAlt());
                }
            });
            Collections.reverse(dsObjectsArrayList);
        }
        else if (sortPreference.equals("3")) {    // sort based on azimuth
            Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Double.compare(dsObject.getDsoAz(), t1.getDsoAz());
                }
            });
        }
        else if (sortPreference.equals("4")) {     // sort based on object ID
            Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    //return Integer.valueOf(dsObject.dsoObjectID.substring(1)).compareTo(Integer.valueOf(t1.dsoObjectID.substring(1)));
                    return Integer.compare(dsObject.getObjectIdSort(), t1.getObjectIdSort());
                }
            });
        }
        else {      // sort by constellation
                Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                    @Override
                    public int compare(DSObject dsObject, DSObject t1) {
                        return dsObject.getDsoConst().compareTo(t1.getDsoConst());
                    }
                });
        }
        clickAdapter.notifyDataSetChanged();
    }

    public void getObservationData() {
        observationDB = new ObservationDatabaseHelper(context).getWritableDatabase();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] sqlSelect = {
                " _id",
                ObserveRecordsSchema.ObsTable.Cols.DsoID,
                ObserveRecordsSchema.ObsTable.Cols.ObsDate,
                ObserveRecordsSchema.ObsTable.Cols.Location,
                ObserveRecordsSchema.ObsTable.Cols.Seeing,
                ObserveRecordsSchema.ObsTable.Cols.Transparency,
                ObserveRecordsSchema.ObsTable.Cols.Telescope,
                ObserveRecordsSchema.ObsTable.Cols.Eyepiece,
                ObserveRecordsSchema.ObsTable.Cols.Power,
                ObserveRecordsSchema.ObsTable.Cols.Filter,
                ObserveRecordsSchema.ObsTable.Cols.Notes,
        };
        String sqlTables = ObserveRecordsSchema.ObsTable.NAME;
        qb.setTables(sqlTables);
        observations = qb.query(observationDB, sqlSelect, null, null, null, null, null);   // cursor of observations
        observations.moveToFirst();

        // create ObservedList arraylist
        if (observations != null && observations.getCount() > 0) {
            observedList.clear();
            observations.moveToFirst();
            int dsoIDCol = observations.getColumnIndex("dsoid");
            while (!observations.isAfterLast()) {
                String dsoObs = observations.getString(dsoIDCol);
                observedList.add(dsoObs);
                observations.moveToNext();
            }
        }
    }

    public void updateObservationInfo() {
        for (int counter = 0; counter < allDsObjectsArrayList.size(); counter++) {
            Integer dsoObserved = 0;
            String dsObjectID = allDsObjectsArrayList.get(counter).getDsoObjectID();
            if (observedList.contains(dsObjectID)) {dsoObserved = 1;}
            allDsObjectsArrayList.get(counter).setDsoObserved(dsoObserved);
        }
    }

}