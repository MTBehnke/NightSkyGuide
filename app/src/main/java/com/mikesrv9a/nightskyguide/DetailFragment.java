// Fragment subclass that displays one dsObject's details

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;

import com.github.chrisbanes.photoview.PhotoView;

public class DetailFragment extends Fragment {

    private DSObject dsObject;  // dsObject to display

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
    private TextView skyAtlasTextView; // display dsObject's Sky Atlas 2000 pages
    private TextView psaTextViewLabel;
    private TextView oithTextViewLabel;
    private TextView skyAtlasTextViewLabel;
    private TextView catTextView;  // displays dsObject's catalogue number (e.g. NGC #)
    private TextView altTextView; // displays dsObject's altitude
    private TextView azTextView; // displays dsObject's azimuth
    private TextView riseTextView;  // displays dsObject's rise time
    private TextView setTextView;  // displays  dsObject's set time

    private FloatingActionButton addObservationFAB;  // FAB to go to add observation record
    private PhotoView constImageView; // displays dsObject's constellation image
    private Bitmap constImage;
    private Set<String> showAtlasLists;  // preference for altas lists

    // callback method implemented by MainActivity
    public interface AddObservationListener {
        // called when addedit FAB is selected
        void addObservationButtonClicked(DSObject dsObjectSelected);
    }

    AddObservationListener listener;

    // called when DetailFragment's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);  // this fragment has no menu items to display

        // get Bundle of arguments then extract the dsObject
        Bundle arguments = getArguments();
        if (arguments != null)
            dsObject = arguments.getParcelable("dsObjectArrayListItem");

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
        skyAtlasTextView = (TextView) view.findViewById(R.id.skyAtlasTextView);
        psaTextViewLabel = (TextView) view.findViewById(R.id.psaLabelTextView);
        oithTextViewLabel = (TextView) view.findViewById(R.id.oithLabelTextView);
        skyAtlasTextViewLabel = (TextView) view.findViewById(R.id.skyAtlasLabelTextView);
        catTextView = (TextView) view.findViewById(R.id.catTextView);
        altTextView = (TextView) view.findViewById(R.id.altTextView);
        azTextView = (TextView) view.findViewById(R.id.azTextView);
        riseTextView = (TextView) view.findViewById(R.id.riseTextView);
        setTextView = (TextView) view.findViewById(R.id.setTextView);


        // set the TextViews
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.applyPattern("0.0");
        setUserPreferences();
        objectIdTextView.setText(dsObject.getDsoObjectID());
        String typeAbbr = dsObject.getDsoType();
        typeTextView.setText(AstroCalc.getDSOType(typeAbbr));
        String magnitude = df.format(dsObject.getDsoMag());
        magTextView.setText(magnitude);
        sizeTextView.setText(dsObject.getDsoSize());
        distTextView.setText(dsObject.getDsoDist());
        raTextView.setText(AstroCalc.convertDDToHMS(dsObject.getDsoRA()));
        decTextView.setText(AstroCalc.convertDDToDMS(dsObject.getDsoDec()));
        String constAbbr = dsObject.getDsoConst();
        constTextView.setText(AstroCalc.getConstName(constAbbr));
        nameTextView.setText(dsObject.getDsoName());
        psaTextView.setText(dsObject.getDsoPSA());
        oithTextView.setText(dsObject.getDsoOITH());
        skyAtlasTextView.setText(dsObject.getDsoSkyAtlas());
        catTextView.setText(dsObject.getDsoCatalogue());
        String altitude = df.format(dsObject.getDsoAlt()) + "°";
        altTextView.setText(altitude);
        String azimuth = df.format(dsObject.getDsoAz()) + "°";
        azTextView.setText(azimuth);
        riseTextView.setText(dsObject.getDsoRiseTimeStr());
        setTextView.setText(dsObject.getDsoSetTimeStr());

        // display constellation image
        if (!constAbbr.equals("")) {
            String constName = "images/" + dsObject.getDsoConst() + ".gif";
            constImageView = (PhotoView) view.findViewById(R.id.constImageView);
            Bitmap bm = loadConstImage(constName);   // display constellation .gif on detail screen
            constImageView.setImageBitmap(bm);
        }

        return view;
    }

    // set AddEditFABListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddObservationListener) context;
    }

    // remove AddEditFABListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    // load constellation image
    private Bitmap loadConstImage(String filename) {
        try {
            // get input stream
            InputStream ims = getActivity().getAssets().open(filename);
            //Drawable constImg = Drawable.createFromStream(ims, null);
            Bitmap constImage = BitmapFactory.decodeStream(ims);
            ims.close();
            return constImage;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return constImage;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_observation:
                listener.addObservationButtonClicked(dsObject);
                return true;
            case R.id.app_info_details:
                Intent info = new Intent(getActivity(), AppInfoActivity.class);
                info.putExtra("appInfoKey", 2);
                startActivity(info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // show/hide atlas textviews depending upon preferences
    public void setUserPreferences() {
        Context context = getActivity();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        showAtlasLists = preferences.getStringSet("multi_pref_atlas_list", null);
        if (!showAtlasLists.contains("P")) {
            psaTextView.setVisibility(View.GONE);
            psaTextViewLabel.setVisibility(View.GONE);}
        else {
            psaTextView.setVisibility(View.VISIBLE);
            psaTextViewLabel.setVisibility(View.VISIBLE);
        }
        if (!showAtlasLists.contains("O")) {
            oithTextView.setVisibility(View.GONE);
            oithTextViewLabel.setVisibility(View.GONE);}
        else {
            oithTextView.setVisibility(View.VISIBLE);
            oithTextViewLabel.setVisibility(View.VISIBLE);
        }
        if (!showAtlasLists.contains("S")) {
            skyAtlasTextView.setVisibility(View.GONE);
            skyAtlasTextViewLabel.setVisibility(View.GONE);}
        else {
            skyAtlasTextView.setVisibility(View.VISIBLE);
            skyAtlasTextViewLabel.setVisibility(View.VISIBLE);
        }
    }
}