// Fragment subclass that displays the alphabetical list of dsObjects

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DSObjectsFragment extends Fragment {


    // callback method implemented by MainActivity
    public interface DSObjectsFragmentListener {
        // called when dsObject is selected
        void onDSObjectSelected(DSObject dsObjectSelected);
    }

    Cursor data;
    DSObjectDatabaseHelper dsObjectsDB;

    public ArrayList<DSObject> dsObjectsArrayList = new ArrayList<>();

    // used to inform the MainActivity when a dsObject is selected
    DSObjectsFragmentListener listener;

    // handler to update dsoAlt and dsoAz for all DSObjects on regular interval
    Handler handler = new Handler();

    DSObjectsClickAdapter clickAdapter;

    public double userLat;
    public double userLong;

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
        dsObjectsArrayList.clear();
    }

    private Runnable updateAltAz = new Runnable() {
        public void run() {
            try {
                setUserLocation();
                for (int counter = 0; counter < dsObjectsArrayList.size(); counter++){
                    dsObjectsArrayList.get(counter).setDsoAltAz(userLat,userLong);
                }
                Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                    @Override
                    public int compare(DSObject dsObject, DSObject t1) {
                        return Double.compare(dsObject.getDsoSortAlt(), t1.getDsoSortAlt());
                    }
                });
                clickAdapter.notifyDataSetChanged();
                handler.postDelayed(this,60000);
            }
            catch (Exception e) {
                Toast.makeText(getActivity(),"error", Toast.LENGTH_SHORT).show();
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
        Context context = getActivity();
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
            int observedCol = data.getColumnIndex("observed");
            setUserLocation();
            while (!data.isAfterLast()) {
                String dsoObjectID = data.getString(objectIdCol);
                String dsoType = data.getString(typeCol);
                Double dsoMag = data.getDouble(magCol);
                String dsoSize = data.getString(sizeCol);
                String dsoDist = data.getString(distCol);
                Double dsoRA = data.getDouble(raCol);
                Double dsoDec = data.getDouble(decCol);
                String dsoConst = data.getString(constCol);
                String dsoName = data.getString(nameCol);
                String dsoPSA = data.getString(psaCol);
                String dsoOITH = data.getString(oithCol);
                Integer dsoObserved = data.getInt(observedCol);
                DSObject dsObject = new DSObject(dsoObjectID, dsoType, dsoMag, dsoSize, dsoDist,
                        dsoRA, dsoDec, dsoConst, dsoName, dsoPSA, dsoOITH, dsoObserved);
                dsObject.setDsoAltAz(userLat, userLong);
                dsObjectsArrayList.add(dsObject);
                data.moveToNext();
            }
            Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
                @Override
                public int compare(DSObject dsObject, DSObject t1) {
                    return Double.compare(dsObject.getDsoSortAlt(), t1.getDsoSortAlt());
                }
            });
        }
    }

    // display this fragment's menu items
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
                Intent info = new Intent(getActivity(),AppInfoActivity.class);
                startActivity(info);
                return true;
            case R.id.location_edit:
                Intent loc = new Intent(getActivity(),SettingsActivity.class);
                startActivity(loc);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // update list on resume - primarily if user changed location
    @Override
    public void onResume() {
        super.onResume();
        setUserLocation();
        for (int counter = 0; counter < dsObjectsArrayList.size(); counter++){
            dsObjectsArrayList.get(counter).setDsoAltAz(userLat,userLong);
        }
        Collections.sort(dsObjectsArrayList, new Comparator<DSObject>() {
            @Override
            public int compare(DSObject dsObject, DSObject t1) {
                return Double.compare(dsObject.getDsoSortAlt(), t1.getDsoSortAlt());
            }
        });
        clickAdapter.notifyDataSetChanged();
    }

    // update user latitude and longitude from preferences
    public void setUserLocation() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        userLat = Double.valueOf(preferences.getString("edit_text_pref_lat", ""));
        userLong = Double.valueOf(preferences.getString("edit_text_pref_long", ""));
    }
}