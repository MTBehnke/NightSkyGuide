package com.mikesrv9a.nightskyguide;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class ObservationsFragment extends Fragment {

    // callback method implemented by MainActivity
    public interface ObservationsFragmentListener {
        // called when observation is selected
        void onObservationSelected(Observation ObservationSelected);
    }

    Cursor data;
    ObservationDatabaseHelper observationsDB;
    int obsDbIdCol = 0;
    int objectIdCol;
    int dateCol;
    int locationCol;
    int seeingCol;
    int transparencyCol;
    int telescopeCol;
    int eyepieceCol;
    int powerCol;
    int filterCol;
    int notesCol;
    int catalogueCol;
    int programCol;

    final int REQUEST_STORAGE = 3;

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
        setHasOptionsMenu(true);  // fragment has menu items to display

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
            objectIdCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.DsoID);
            dateCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.ObsDate);
            locationCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Location);
            seeingCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Seeing);
            transparencyCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Transparency);
            telescopeCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Telescope);
            eyepieceCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Eyepiece);
            powerCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Power);
            filterCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Filter);
            notesCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Notes);
            catalogueCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Catalogue);
            programCol = data.getColumnIndex(ObserveRecordsSchema.ObsTable.Cols.Program);
            while (!data.isAfterLast()) {
                Integer obsDbId = data.getInt(obsDbIdCol);  // database record #, column 0
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
                String obsCatalogue = data.getString(catalogueCol);
                String obsProgram = data.getString(programCol);

                // creates Observation objects
                Observation observation = new Observation(obsDbId, obsObjectID, obsDate, obsLocation, obsSeeing, obsTransparency,
                        obsTelescope, obsEyepiece, obsPower, obsFilter, obsNotes, obsCatalogue, obsProgram);
                allObservationsArrayList.add(observation);

                data.moveToNext();
            }
        }
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_observations_menu, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.export_csv:
                checkPermissions();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // update list on resume - primarily if user changed location or settings
    @Override
    public void onResume() {
        super.onResume();
        updateArrayList();
    }

    // update and sort arraylist for recyclerview based on user preferences
    public void updateArrayList() {
        observationsArrayList.clear();

        for (int counter = 0; counter < allObservationsArrayList.size(); counter++) {
            observationsArrayList.add(allObservationsArrayList.get(counter));
        }

        /*Collections.sort(observationsArrayList, new Comparator<Observation>() {
            @Override
            public int compare(Observation observation, Observation t1) {
                return Integer.valueOf(observation.obsDsoID.substring(1)).compareTo(Integer.valueOf(t1.obsDsoID.substring(1)));
            }
        });*/

        clickAdapter.notifyDataSetChanged();
    }

    // export observations to CSV file
    public void exportObsCSV() {
        // first check permissions and if not enabled exit

        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(getActivity(), "External storage unavailable", Toast.LENGTH_LONG).show();
            return;
        }
        String exportDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "ObservationLog.csv";
        String filePath = exportDir + File.separator + fileName;
        File existCheck = new File(filePath);
        try
        {
            if (existCheck.exists()) {
                int version = 0;
                do {
                    version++;
                    fileName = "ObservationLog (" + version + ").csv";
                    filePath = exportDir + File.separator + fileName;
                    existCheck = new File(filePath);
                } while (existCheck.exists());
            }
            FileOutputStream file = new FileOutputStream(filePath);
            file.write(0xef);
            file.write(0xbb);
            file.write(0xbf);
            CSVWriter csvWrite = new CSVWriter(new OutputStreamWriter(file));
            String arrHeaderStr[] = {getString(R.string.hint_objectID), getString(R.string.hint_date), getString(R.string.hint_location),
                    getString(R.string.hint_seeing), getString(R.string.hint_transparency), getString(R.string.hint_telescope),
                    getString(R.string.hint_eyepiece), getString(R.string.hint_power), getString(R.string.hint_filter), getString(R.string.hint_notes), getString(R.string.hint_catalogue), getString(R.string.hint_obs_program)};
            csvWrite.writeNext(arrHeaderStr);
            data.moveToFirst();
            while (!data.isAfterLast()) {
                String arrStr[] = {data.getString(objectIdCol), data.getString(dateCol), data.getString(locationCol), data.getString(seeingCol),
                        data.getString(transparencyCol), data.getString(telescopeCol), data.getString(eyepieceCol), data.getString(powerCol),
                        data.getString(filterCol), data.getString(notesCol), data.getString(catalogueCol), data.getString(programCol)};
                csvWrite.writeNext(arrStr);
                data.moveToNext();
            }
            csvWrite.close();
            Toast.makeText(getActivity(), "File saved to:\n" + filePath, Toast.LENGTH_LONG).show();
        }
        catch (Exception exception) {
            Toast.makeText(getActivity(), "Error writing file", Toast.LENGTH_LONG).show();
        }
    }

    // check if Manifest (application) location permissions enabled and if not, request permissions
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        }
        else {
            displayAlertDialog("Save Observation Log to CSV?");
        }
    }

    // Manifest (application) location permissions result: either user allowed or denied/cancelled permissions request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //switch (requestCode) {
            //case REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission granted
                    displayAlertDialog("Save Observation Log to CSV?");
                } else {
                    // permission denied - you shall not pass!!!
            }
    }

    private void displayAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                exportObsCSV();
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

}