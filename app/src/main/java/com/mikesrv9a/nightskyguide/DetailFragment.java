// Fragment subclass that displays one dsObject's details

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.github.chrisbanes.photoview.PhotoView;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DetailFragment extends Fragment {

    private DSObject dsObject;  // dsObject to display

    private TextView psaTextView; // displays dsObject's PSA pages
    private TextView oithTextView; // displays dsObject's OITH pages
    private TextView skyAtlasTextView; // display dsObject's Sky Atlas 2000 pages
    private TextView psaTextViewLabel;
    private TextView oithTextViewLabel;
    private TextView skyAtlasTextViewLabel;

    private Bitmap constImage;

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
        TextView objectIdTextView = view.findViewById(R.id.objectIdTextView);
        TextView typeTextView = view.findViewById(R.id.typeTextView);
        TextView magTextView = view.findViewById(R.id.magTextView);
        TextView sizeTextView = view.findViewById(R.id.sizeTextView);
        TextView distTextView = view.findViewById(R.id.distTextView);
        TextView raTextView = view.findViewById(R.id.raTextView);
        TextView decTextView = view.findViewById(R.id.decTextView);
        TextView constTextView = view.findViewById(R.id.constTextView);
        TextView nameTextView = view.findViewById(R.id.nameTextView);
        psaTextView = view.findViewById(R.id.psaTextView);
        oithTextView = view.findViewById(R.id.oithTextView);
        skyAtlasTextView = view.findViewById(R.id.skyAtlasTextView);
        psaTextViewLabel = view.findViewById(R.id.psaLabelTextView);
        oithTextViewLabel = view.findViewById(R.id.oithLabelTextView);
        skyAtlasTextViewLabel = view.findViewById(R.id.skyAtlasLabelTextView);
        TextView catTextView = view.findViewById(R.id.catTextView);
        TextView altTextView = view.findViewById(R.id.altTextView);
        TextView azTextView = view.findViewById(R.id.azTextView);
        TextView riseTextView = view.findViewById(R.id.riseTextView);
        TextView setTextView = view.findViewById(R.id.setTextView);


        // set the TextViews
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.applyPattern("0.0");
        setUserPreferences();
        objectIdTextView.setText(dsObject.getDsoObjectID());
        String typeAbbr = dsObject.getDsoType();
        typeTextView.setText(AstroCalc.getDSOType(typeAbbr));
        if (dsObject.getDsoMag() == null) {
            magTextView.setVisibility(View.GONE);
            view.findViewById(R.id.magLabelTextView).setVisibility(View.GONE);
        } else {
            String magnitude = df.format(dsObject.getDsoMag());
            magTextView.setText(magnitude);
        }
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

        DateTimeFormatter dtf = DateTimeFormat.shortDateTime().withZone(DateTimeZone.getDefault()).
                withLocale(Locale.getDefault());
        String dsoSetTimeStr;
        String dsoRiseTimeStr;
        if (dsObject.getDsoRiseTime() == null) {
            dsoRiseTimeStr="This DSO never rises";
            dsoSetTimeStr="at this latitude";
        } else if (dsObject.getDsoSetTime() == null) {
            dsoRiseTimeStr="Circumpolar: never";
            dsoSetTimeStr="sets below horizon";
        } else {
            dsoRiseTimeStr = dsObject.getDsoRiseTime().toString(dtf);
            dsoSetTimeStr = dsObject.getDsoSetTime().toString(dtf);
        }
        riseTextView.setText(dsoRiseTimeStr);
        setTextView.setText(dsoSetTimeStr);

        // display constellation image
        if (!constAbbr.equals("")) {
            String constName = "images/" + dsObject.getDsoConst() + ".gif";
            PhotoView constImageView = view.findViewById(R.id.constImageView);
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
        Set<String> showAtlasLists = preferences.getStringSet("multi_pref_atlas_list", null);
        if(showAtlasLists ==null){
            String[] defaultList = {"P","O","S"};
            showAtlasLists = new HashSet<>(Arrays.asList(defaultList));
        }
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