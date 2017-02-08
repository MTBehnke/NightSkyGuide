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

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;

public class DetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

/*  *** Suspend all database add/edit/delete capabilities
    // callback methods implemented by MainActivity
    public interface DetailFragmentListener {
        void onDSObjectDeleted(); // called when a dsObject is deleted

        // pass Uri of dsObject to edit to the DetailFragmentListener
        void onEditDSObject(Uri dsObjectUri);
    }
*/

    private static final int DSOBJECT_LOADER = 0;  // identifies the Loader

/*  *** Suspend all database add/edit/delete capabilities
    private DetailFragmentListener listener; // MainActivity
*/

    private Uri dsObjectUri;  // Uri of selected dsObject

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

/*  *** Suspend all database add/edit/delete capabilities
    // set DetailFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }

    // remove DetailFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
*/

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
            dsObjectUri = arguments.getParcelable(MainActivity.DSOBJECT_URI);

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
        getLoaderManager().initLoader(DSOBJECT_LOADER, null, this);
        return view;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

/*  *** Suspend all database add/edit/delete capabilities
    // handle menu item selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                listener.onEditDSObject(dsObjectUri); // pass Uri to listener
                return true;
            case R.id.action_delete:
                deleteDSObject();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // delete a dsObject
    private void deleteDSObject() {
        // use FragmentManager to display the confirmDelete DialogFragment
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }

    // DialogFragment to confirm deletion of dsObject
    private final DialogFragment confirmDelete =
            new DialogFragment() {
            // create an AlertDialog and return it
            @Override
            public Dialog onCreateDialog(Bundle bundle) {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.confirm_title);
                builder.setMessage(R.string.confirm_message);

                // provide an OK button that simply dismisses the dialog
                builder.setPositiveButton(R.string.button_delete,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog, int button) {

                                // use Activity's ContentResolver to invoke
                                // delete on the DSObjectContentProvider
                                getActivity().getContentResolver().delete(
                                        dsObjectUri, null, null);
                                listener.onDSObjectDeleted(); // notify Listener
                            }
                        }
                );

                builder.setNegativeButton(R.string.button_cancel, null);
                return builder.create();  // return the AlertDialog
            }
        };
*/

    // called by LoaderManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        CursorLoader cursorLoader;

        switch (id) {
            case DSOBJECT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                    dsObjectUri, // Uri of dsObject to display
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
            int objectIdIndex = data.getColumnIndex(DSObject.DSO_OBJECTID);
            int typeIndex = data.getColumnIndex(DSObject.DSO_TYPE);
            int magIndex = data.getColumnIndex(DSObject.DSO_MAG);
            int sizeIndex = data.getColumnIndex(DSObject.DSO_SIZE);
            int distIndex = data.getColumnIndex(DSObject.DSO_DIST);
            int raIndex = data.getColumnIndex(DSObject.DSO_RA);
            int decIndex = data.getColumnIndex(DSObject.DSO_DEC);
            int constIndex = data.getColumnIndex(DSObject.DSO_CONST);
            int nameIndex = data.getColumnIndex(DSObject.DSO_NAME);
            int psaIndex = data.getColumnIndex(DSObject.DSO_PSA);
            int oithIndex = data.getColumnIndex(DSObject.DSO_OITH);
            int observedIndex = data.getColumnIndex(DSObject.DSO_OBSERVED);

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