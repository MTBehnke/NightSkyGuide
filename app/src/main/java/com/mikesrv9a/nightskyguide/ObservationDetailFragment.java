// Fragment subclass that displays one observation's details

package com.mikesrv9a.nightskyguide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ObservationDetailFragment extends Fragment {

    private Observation observation;

    private TextView objectIdTextView;
    private TextView dateTextView;
    private TextView locationTextView;
    private TextView seeingTextView;
    private TextView transparencyTextView;
    private TextView telescopeTextView;
    private TextView eyepieceTextView;
    private TextView powerTextView;
    private TextView filterTextView;
    private TextView notesTextView;

    // called when DetailFragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);  // this fragment has no menu items to display

        // get Bundle of arguments then extract the observation
        Bundle arguments = getArguments();
        if (arguments != null)
            observation = arguments.getParcelable("observationArrayListItem");

        // inflate ObservationFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_observ_details, container, false);

        // get the EditTexts
        objectIdTextView = (TextView) view.findViewById(R.id.objectIdObsTextView);
        dateTextView = (TextView) view.findViewById(R.id.obsDateText);
        locationTextView = (TextView) view.findViewById(R.id.obsLocationText);
        seeingTextView = (TextView) view.findViewById(R.id.obsSeeingText);
        transparencyTextView = (TextView) view.findViewById(R.id.obsTransparencyText);
        telescopeTextView = (TextView) view.findViewById(R.id.obsTelescopeText);
        eyepieceTextView = (TextView) view.findViewById(R.id.obsEyepieceText);
        powerTextView = (TextView) view.findViewById(R.id.obsPowerText);
        filterTextView = (TextView) view.findViewById(R.id.obsFilterText);
        notesTextView = (TextView) view.findViewById(R.id.obsNotesText);


        // set the TextViews
        objectIdTextView.setText(observation.getObsDsoID());
        dateTextView.setText(observation.getObsDate());
        locationTextView.setText(observation.getObsLocation());
        seeingTextView.setText(observation.getObsSeeing());
        transparencyTextView.setText(observation.getObsTransparency());
        telescopeTextView.setText(observation.getObsTelescope());
        eyepieceTextView.setText(observation.getObsEyepiece());
        powerTextView.setText(observation.getObsPower());
        filterTextView.setText(observation.getObsFilter());
        notesTextView.setText(observation.getObsNotes());

        return view;
    }

}

