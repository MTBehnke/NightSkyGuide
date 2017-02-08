// Fragment subclass that displays the alphabetical list of dsObjects

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;

public class DSObjectsFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // callback method implemented by MainActivity
    public interface DSObjectsFragmentListener {
        // called when dsObject is selected
        void onDSObjectSelected(Uri dsObjectUri);

        // called when add button is pressed
        void onAddDSObject();
    }

    private static final int DSOBJECTS_LOADER = 0;  // identifies Loader

    // used to inform the MainActivity when a dsObject is selected
    private DSObjectsFragmentListener listener;

    private DSObjectsAdapter dsObjectsAdapter;  // adapter for recyclerView

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
        dsObjectsAdapter = new DSObjectsAdapter(
                new DSObjectsAdapter.DSObjectClickListener() {
                    @Override
                    public void onClick(Uri dsObjectUri) {
                        listener.onDSObjectSelected(dsObjectUri);
                    }
                }
        );
        recyclerView.setAdapter(dsObjectsAdapter);

        // attach a custom ItemDecorator to draw dividers between list items
        recyclerView.addItemDecoration(new ItemDivider(getContext()));

        // improves performance if RecyclerView's layout size never changes
        recyclerView.setHasFixedSize(true);

        // get the FloatingActionButton and configure its listener
        FloatingActionButton addButton =
                (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // displays the AddEditFragment when FAB is touched
                    @Override
                    public void onClick(View view) {
                        listener.onAddDSObject();
                    }
                }
        );

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
                    DSObject.CONTENT_URI,  // Uri of dsObjects table
                    null,  // null projection returns all columns
                    null,  // null selection returns all rows
                    null,  // no selection arguments
                    DSObject.DSO_OBJECTID + " COLLATE NOCASE ASC");  // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dsObjectsAdapter.swapCursor(data);
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dsObjectsAdapter.swapCursor(null);
    }
}
