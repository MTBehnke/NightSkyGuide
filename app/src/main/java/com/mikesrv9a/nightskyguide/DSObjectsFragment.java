// Fragment subclass that displays the alphabetical list of dsObjects

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObjectDB;

import java.util.ArrayList;

public class DSObjectsFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // callback method implemented by MainActivity
    public interface DSObjectsFragmentListener {
        // called when dsObject is selected
        void onDSObjectSelected(Uri dsObjectUri);
    }

    private static final int DSOBJECTS_LOADER = 0;  // identifies Loader

    public ArrayList<DSObject> dsObjectsArrayList = new ArrayList<>();

    DSObjectsAdapter dsObjectsAdapter;  // adapter for recyclerView

    // used to inform the MainActivity when a dsObject is selected
    private DSObjectsFragmentListener listener;

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
        DSObjectsAdapter adapter = new DSObjectsAdapter(dsObjectsArrayList);
        recyclerView.setAdapter(adapter);

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes
        //recyclerView.setHasFixedSize(true);

        return view;
    }

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

    // initialize a Loader when this fragment's activity is created
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DSOBJECTS_LOADER, null, this);
    }

    // called from MainActivity when other Fragment's update database
    public void updateDSObjectList() {
        dsObjectsAdapter.notifyDataSetChanged();
    }

    // called by LoaderManager to create Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case DSOBJECTS_LOADER:
                return new CursorLoader(getActivity(),
                        DSObjectDB.CONTENT_URI,  // Uri of dsObjects table
                        null,  // null projection returns all columns
                        null,  // null selection returns all rows
                        null,  // no selection arguments
                        DSObjectDB.DSO_OBJECTID + " COLLATE NOCASE ASC");  // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
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
                dsObjectsArrayList.add(dsObject);
                data.moveToNext();
            }
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
