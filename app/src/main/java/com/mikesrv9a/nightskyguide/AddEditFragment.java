/*  *** Suspend all database add/edit/delete capabilities

// Fragment for adding a new DSObject or editing an existing one

package com.mikesrv9a.nightskyguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;

public class AddEditFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    // defines callback method implemented by MainActivity
    public interface AddEditFragmentListener {
        // called when dsObject is saved
        void onAddEditCompleted(Uri dsObjectUri);
    }

    // constant used to identify the Loader
    private static final int DSOBJECT_LOADER = 0;

    private AddEditFragmentListener listener; // MainActivity
    private Uri dsObjectUri;  // URI of selected dsObject
    private boolean addingNewDSObject = true;  // adding (true) or editing

    // EditTexts for dsObject information
    private TextInputLayout objectIdTextInputLayout;
    private TextInputLayout typeTextInputLayout;
    private TextInputLayout magTextInputLayout;
    private TextInputLayout sizeTextInputLayout;
    private TextInputLayout distTextInputLayout;
    private TextInputLayout raTextInputLayout;
    private TextInputLayout decTextInputLayout;
    private TextInputLayout constTextInputLayout;
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout psaTextInputLayout;
    private TextInputLayout oithTextInputLayout;
    private TextInputLayout observedTextInputLayout;
    private FloatingActionButton saveDSObjectFAB;

    private CoordinatorLayout coordinatorLayout;  // used with SnackBars

    // set AddEditFragmentListener when Fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }

    // remove AddEditFragmentListener when Fragment detaches
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);  // fragment has menu items to display

        // inflate the GUI and get references to EditTexts
        View view =
                inflater.inflate(R.layout.fragment_add_edit, container, false);
        objectIdTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.objectIdTextInputLayout);
        objectIdTextInputLayout.getEditText().addTextChangedListener(
                objectIdChangedListener);
        typeTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.typeTextInputLayout);
        magTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.magTextInputLayout);
        sizeTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.sizeTextInputLayout);
        distTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.distTextInputLayout);
        raTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.raTextInputLayout);
        decTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.decTextInputLayout);
        constTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.constTextInputLayout);
        nameTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        psaTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.psaTextInputLayout);
        oithTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.oithTextInputLayout);
        observedTextInputLayout =
                (TextInputLayout) view.findViewById(R.id.observedTextInputLayout);

        // set FloatingActionButton's event listener
        saveDSObjectFAB = (FloatingActionButton) view.findViewById(
                R.id.saveFloatingActionButton);
        saveDSObjectFAB.setOnClickListener(saveDSObjectButtonClicked);
        updateSaveButtonFAB();

        // used to display SnackBars with brief messages
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(
                R.id.coordinatorLayout);

        Bundle arguments = getArguments(); // null if creating new DSObject

        if (arguments != null) {
            addingNewDSObject = false;
            dsObjectUri = arguments.getParcelable(MainActivity.DSOBJECT_URI);
        }

        // if editing an existing DSObject, created Loader to get the DSObject
        if (dsObjectUri != null)
            getLoaderManager().initLoader(DSOBJECT_LOADER, null, this);

        return view;
    }

    // detects when the text in the nameTextInputLayout's EditText changes
    // to hide or show saveButtonFAB
    private final TextWatcher objectIdChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
              int after) {}

        // called when the text in nameTextInputLayout changes
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // shows saveButtonFAB only if the name is not empty
    private void updateSaveButtonFAB() {
        String input =
            objectIdTextInputLayout.getEditText().getText().toString();

        // if there is a name for the dsObject, show the FloatingActionButton
        if (input.trim().length() != 0)
            saveDSObjectFAB.show();
        else
            saveDSObjectFAB.hide();
    }

    // responds to event generated when user saves a dsObject
    private final View.OnClickListener saveDSObjectButtonClicked =
        new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // hide the virtual keyboard
                ((InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);
                saveDSObject(); // save dsObject to the database
            }
        };

    // saves contact information to the database
    private void saveDSObject() {
        // create ContentValues object containing dsObject's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(DSObject.DSO_OBJECTID,
            objectIdTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_TYPE,
            typeTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_MAG,
            Double.parseDouble(magTextInputLayout.getEditText().getText().toString()));
        contentValues.put(DSObject.DSO_SIZE,
            sizeTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_DIST,
            distTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_RA,
            Double.parseDouble(raTextInputLayout.getEditText().getText().toString()));
        contentValues.put(DSObject.DSO_DEC,
            Double.parseDouble(decTextInputLayout.getEditText().getText().toString()));
        contentValues.put(DSObject.DSO_CONST,
            constTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_NAME,
            nameTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_PSA,
            psaTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_OITH,
            oithTextInputLayout.getEditText().getText().toString());
        contentValues.put(DSObject.DSO_OBSERVED,
            Integer.parseInt(observedTextInputLayout.getEditText().getText().toString()));

        if (addingNewDSObject) {
            // use Activity's ContentResolver to invoke
            // insert on the DSObjectContentProvider
            Uri newDSObjectUri = getActivity().getContentResolver().insert(
                DSObject.CONTENT_URI, contentValues);

            if (newDSObjectUri != null) {
                Snackbar.make(coordinatorLayout,
                    R.string.dso_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newDSObjectUri);
            }

            else {
                Snackbar.make(coordinatorLayout,
                        R.string.dso_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // use Activity's ContentResolver to invoke
            // insert on the DSObjectContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                dsObjectUri, contentValues, null, null);

            if (updatedRows > 0) {
                listener.onAddEditCompleted(dsObjectUri);
                Snackbar.make(coordinatorLayout,
                        R.string.dso_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.dso_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    // called by LoadManager to create a Loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropriate CursorLoader based on the id argument;
        // only one Loader in this fragment, so the switch is unnecessary
        switch (id) {
            case DSOBJECT_LOADER:
                return new CursorLoader(getActivity(),
                    dsObjectUri, // Uri of dsObject to display
                    null, // null projection returns all columns
                    null, // null selection returns all rows
                    null, // no selection arguments
                    null); // sort order
            default:
                return null;
        }
    }

    // called by LoaderManager when loading completes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if the contact exists in the database, display its data
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

            // fill EditTexts with the retrieved data
            objectIdTextInputLayout.getEditText().setText(
                    data.getString(objectIdIndex));
            typeTextInputLayout.getEditText().setText(
                    data.getString(typeIndex));
            magTextInputLayout.getEditText().setText(
                    data.getString(magIndex));
            sizeTextInputLayout.getEditText().setText(
                    data.getString(sizeIndex));
            distTextInputLayout.getEditText().setText(
                    data.getString(distIndex));
            raTextInputLayout.getEditText().setText(
                    data.getString(raIndex));
            decTextInputLayout.getEditText().setText(
                    data.getString(decIndex));
            constTextInputLayout.getEditText().setText(
                    data.getString(constIndex));
            nameTextInputLayout.getEditText().setText(
                    data.getString(nameIndex));
            psaTextInputLayout.getEditText().setText(
                    data.getString(psaIndex));
            oithTextInputLayout.getEditText().setText(
                    data.getString(oithIndex));
            observedTextInputLayout.getEditText().setText(
                    data.getString(observedIndex));

            updateSaveButtonFAB();
        }
    }

    // called by LoaderManager when the Loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}

*/