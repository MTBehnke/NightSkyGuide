// Fragment subclass that displays one dsObject's details

package com.mikesrv9a.nightskyguide;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObjectDB;

public class DetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DSOBJECTDB_LOADER = 0;  // identifies the Loader

    private Uri dsObjectDBUri;  // Uri of selected dsObject

    private TextView objectIdTextView;  // displays dsObject's ID
    private TextView typeTextView; // displays dsObject's type
    private TextView magTextView; // displays dsObject's mag
    private TextView sizeTextView; // displays dsObject's size
    private TextView distTextView; // displays dsObject's distance
    private TextView raTextView; // displays dsObject's RA
    private TextView decTextView; // displays dsObject's Dec
    private TextView constTextView; // displays dsObject's Constellation
    private TextView nameTextView; // displays dsObject's Common Name
    private TextView psaTextView; // displays dsObject's PSA pages
    private TextView oithTextView; // displays dsObject's OITH pages
    private TextView observedTextView; // displays dsObject's Observed status

    // called when DetailFragmentListener's view needs to be created
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);  // this fragment has menu items to display

        // get Bundle of arguments then extract the dsObject's Uri
        Bundle arguments = getArguments();

        if (arguments != null)
            dsObjectDBUri = arguments.getParcelable(MainActivity.DSOBJECTDB_URI);

        // inflate DetailFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_details, container, false);

        // get the EditTexts
        objectIdTextView = (TextView) view.findViewById(R.id.objectIdTextView);
        typeTextView = (TextView) view.findViewById(R.id.typeTextView);
        magTextView = (TextView) view.findViewById(R.id.magTextView);
        sizeTextView = (TextView) view.findViewById(R.id.sizeTextView);
        distTextView = (TextView) view.findViewById(R.id.distTextView);
        raTextView = (TextView) view.findViewById(R.id.raTextView);
        decTextView = (TextView) view.findViewById(R.id.decTextView);
        constTextView = (TextView) view.findViewById(R.id.constTextView);
        nameTextView = (TextView) view.findViewById(R.id.nameTextView);
        psaTextView = (TextView) view.findViewById(R.id.psaTextView);
        oithTextView = (TextView) view.findViewById(R.id.oithTextView);
        observedTextView = (TextView) view.findViewById(R.id.observedTextView);

        // load the dsObject
        getLoaderManager().initLoader(DSOBJECTDB_LOADER, null, this);
        return view;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        CursorLoader cursorLoader;

        switch (id) {
            case DSOBJECTDB_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                    dsObjectDBUri, // Uri of dsObject to display
                    null, // null projection returns all rows
                    null, // null selection returns all columns
                    null, // no selection arguments
                    null);  // sort order
                break;
            default:
                cursorLoader = null;
                break;
        }

        return cursorLoader;
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the dsObject exists in the database, display its data
        if (data != null && data.moveToFirst()) {
            // get the column index for each data item
            int objectIdIndex = data.getColumnIndex(DSObjectDB.DSO_OBJECTID);
            int typeIndex = data.getColumnIndex(DSObjectDB.DSO_TYPE);
            int magIndex = data.getColumnIndex(DSObjectDB.DSO_MAG);
            int sizeIndex = data.getColumnIndex(DSObjectDB.DSO_SIZE);
            int distIndex = data.getColumnIndex(DSObjectDB.DSO_DIST);
            int raIndex = data.getColumnIndex(DSObjectDB.DSO_RA);
            int decIndex = data.getColumnIndex(DSObjectDB.DSO_DEC);
            int constIndex = data.getColumnIndex(DSObjectDB.DSO_CONST);
            int nameIndex = data.getColumnIndex(DSObjectDB.DSO_NAME);
            int psaIndex = data.getColumnIndex(DSObjectDB.DSO_PSA);
            int oithIndex = data.getColumnIndex(DSObjectDB.DSO_OITH);
            int observedIndex = data.getColumnIndex(DSObjectDB.DSO_OBSERVED);

            // fill TextViews with the retrieved data
            objectIdTextView.setText(data.getString(objectIdIndex));
            typeTextView.setText(data.getString(typeIndex));
            magTextView.setText(Double.toString(data.getDouble(magIndex)));
            sizeTextView.setText(data.getString(sizeIndex));
            distTextView.setText(data.getString(distIndex));
            raTextView.setText(Double.toString(data.getDouble(raIndex)));
            decTextView.setText(Double.toString(data.getDouble(decIndex)));
            constTextView.setText(data.getString(constIndex));
            nameTextView.setText(data.getString(nameIndex));
            psaTextView.setText(data.getString(psaIndex));
            oithTextView.setText(data.getString(oithIndex));
            observedTextView.setText(Integer.toString(data.getInt(observedIndex)));

        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}