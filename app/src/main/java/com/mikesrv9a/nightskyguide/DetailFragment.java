// Fragment subclass that displays one dsObject's details

package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.github.chrisbanes.photoview.PhotoView;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DetailFragment extends Fragment {

    private DSObject dsObject;  // dsObject to display

    private TextView psaTextView; // displays dsObject's PSA pages
    private TextView oithTextView; // displays dsObject's OITH pages
    private TextView skyAtlasTextView; // display dsObject's Sky Atlas 2000 pages
    private TextView turnLeftTextView; // display dsObject's Turn Left At Orion pages
    private TextView psaTextViewLabel;
    private TextView oithTextViewLabel;
    private TextView skyAtlasTextViewLabel;
    private TextView turnLeftTextViewLabel;

    private Bitmap constImage;
    private Bitmap telrad;

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
        setHasOptionsMenu(true);

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
        turnLeftTextView = view.findViewById(R.id.turnLeftTextView);
        psaTextViewLabel = view.findViewById(R.id.psaLabelTextView);
        oithTextViewLabel = view.findViewById(R.id.oithLabelTextView);
        skyAtlasTextViewLabel = view.findViewById(R.id.skyAtlasLabelTextView);
        turnLeftTextViewLabel = view.findViewById(R.id.turnLeftLabelTextView);
        TextView catTextView = view.findViewById(R.id.catTextView);
        TextView altTextView = view.findViewById(R.id.altTextView);
        TextView azTextView = view.findViewById(R.id.azTextView);
        TextView riseTextView = view.findViewById(R.id.riseTextView);
        TextView setTextViewLabel = view.findViewById(R.id.setLabelTextView);
        TextView setTextView = view.findViewById(R.id.setTextView);
        TextView transitTextViewLabel = view.findViewById(R.id.transitLabelTextView);
        TextView transitTextView = view.findViewById(R.id.tranitTextView);


        // set the TextViews
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        DecimalFormat df = (DecimalFormat) numberFormat;
        df.applyPattern("0.0");
        setUserPreferences();
        objectIdTextView.setText(dsObject.getDsoObjectID());
        String typeAbbr = dsObject.getDsoType();
        typeTextView.setText(AstroCalc.getDSOType(typeAbbr));
        if (dsObject.getDsoMag() != 0) {
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
        turnLeftTextView.setText(dsObject.getDsoTurnLeft());
        catTextView.setText(dsObject.getDsoCatalogue());
        String altitude = df.format(dsObject.getDsoAlt()) + "°";
        altTextView.setText(altitude);
        String azimuth = df.format(dsObject.getDsoAz()) + "°";
        azTextView.setText(azimuth);

        DateTimeFormatter dtf = DateTimeFormat.shortTime().withZone(DateTimeZone.getDefault()).
                withLocale(Locale.getDefault());
        String dsoRiseTimeStr;
        String dsoSetTimeStr;
        if (dsObject.getDsoRiseTime() == null) {
            dsoRiseTimeStr = "This DSO never rises";
            dsoSetTimeStr = "at this latitude";
            transitTextViewLabel.setVisibility(View.GONE);
            transitTextView.setVisibility(View.GONE);
        } else if (dsObject.getDsoSetTime() == null) {
            dsoRiseTimeStr = "Circumpolar: never";
            dsoSetTimeStr = "sets below horizon";
            transitTextViewLabel.setVisibility(View.VISIBLE);
            transitTextView.setVisibility(View.VISIBLE);
            transitTextView.setText(dsObject.getDsoTransitTime().toString(dtf));
        } else {        // switch Transit Time and Set Time positions to display in order
            dsoRiseTimeStr = dsObject.getDsoRiseTime().toString(dtf);
            dsoSetTimeStr = dsObject.getDsoTransitTime().toString(dtf);
            transitTextViewLabel.setVisibility(View.VISIBLE);
            transitTextView.setVisibility(View.VISIBLE);
            transitTextView.setText(dsObject.getDsoSetTime().toString(dtf));
            setTextViewLabel.setText(getString(R.string.label_transit));
            transitTextViewLabel.setText(getString(R.string.label_set));
        }
        riseTextView.setText(dsoRiseTimeStr);
        setTextView.setText(dsoSetTimeStr);


        // display constellation image
        if (!constAbbr.equals("")) {
            String constName = "images/" + dsObject.getDsoConst() + ".gif";
            PhotoView constImageView = view.findViewById(R.id.constImageView);
            Bitmap bm = loadConstImage(constName);   // display constellation .gif on detail screen
            Bitmap tr = telradImage();
            constImageView.setImageBitmap(combineTwoBitmaps(bm,tr));
            //constImageView.setImageBitmap(combineTwoBitmaps(bm,tr));

            /*
            // Change constellation image to night mode version
            int colorCode = Color.argb(255,255,0,0);  // transparency and red = 255, green and blue = 0
            constImageView.setColorFilter(colorCode, PorterDuff.Mode.MULTIPLY);  */
        }

        //setNightMode(view);

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

    private Bitmap telradImage() {
        try {
            // get input stream
            String filename = "images/Telrad.gif";
            InputStream ims = getActivity().getAssets().open(filename);
            telrad = BitmapFactory.decodeStream(ims);
            ims.close();
            return telrad;
        } catch (Exception e) {
            Toast.makeText(getActivity(), "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return telrad;
    }

    private Bitmap combineTwoBitmaps(Bitmap background, Bitmap foreground) {
        float objectLoc[] = AstroCalc.constImageConv(dsObject.dsoConst, dsObject.getDsoRA(), dsObject.getDsoDec());
        Matrix matrix = new Matrix();
        float targetX = objectLoc[0] - 82;
        float targetY = objectLoc[1] - 82;
        matrix.setTranslate(targetX, targetY);
        //matrix.postRotate(45,objectLoc[0],objectLoc[1]);
        matrix.postScale(objectLoc[2],objectLoc[2],objectLoc[0],objectLoc[1]);
        Bitmap combinedBitmap = Bitmap.createBitmap(background.getWidth(), background.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawBitmap(foreground, matrix, paint);
        return combinedBitmap;
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);

        /* Night mode work in progress
        for(int count = 0; count < menu.size(); count++) {
            Drawable drawable = menu.getItem(count).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(Color.argb(255,255,0,0),PorterDuff.Mode.MULTIPLY);
            }
        } */
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
        if (showAtlasLists == null) {
            String[] defaultList = {"P", "O", "S"};
            showAtlasLists = new HashSet<>(Arrays.asList(defaultList));
        }
        if (!showAtlasLists.contains("P")) {
            psaTextView.setVisibility(View.GONE);
            psaTextViewLabel.setVisibility(View.GONE);
        } else {
            psaTextView.setVisibility(View.VISIBLE);
            psaTextViewLabel.setVisibility(View.VISIBLE);
        }
        if (!showAtlasLists.contains("O")) {
            oithTextView.setVisibility(View.GONE);
            oithTextViewLabel.setVisibility(View.GONE);
        } else {
            oithTextView.setVisibility(View.VISIBLE);
            oithTextViewLabel.setVisibility(View.VISIBLE);
        }
        if (!showAtlasLists.contains("S")) {
            skyAtlasTextView.setVisibility(View.GONE);
            skyAtlasTextViewLabel.setVisibility(View.GONE);
        } else {
            skyAtlasTextView.setVisibility(View.VISIBLE);
            skyAtlasTextViewLabel.setVisibility(View.VISIBLE);
        }
        if (!showAtlasLists.contains("T")) {
            turnLeftTextView.setVisibility(View.GONE);
            turnLeftTextViewLabel.setVisibility(View.GONE);
        } else {
            turnLeftTextView.setVisibility(View.VISIBLE);
            turnLeftTextViewLabel.setVisibility(View.VISIBLE);
        }
    }

    /* Night Mode work in progress
    // Change colors for night mode
    public void setNightMode(View view) {
        // set background color of container (content_main) to black
        FrameLayout contentView = getActivity().findViewById(R.id.fragmentContainer);
        contentView.setBackgroundColor(0xFF000000);

        // set toolbar colors
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(0xFF200000);
        toolbar.setTitleTextColor(0xFFAA0000);

        // sets background color of fragment_details view to black
        view.setBackgroundColor(0xFF000000);

        // set all textviews to dim red
        ViewGroup viewGroup = (ViewGroup)view;
        redText(viewGroup);

        // misc color changes
        View section1 = view.findViewById(R.id.Section1);
        section1.setBackgroundColor(0xFF200000);
        View divLine = view.findViewById(R.id.line);
        divLine.setBackgroundColor(0xFFAA0000);
        divLine = view.findViewById(R.id.line2);
        divLine.setBackgroundColor(0xFFAA0000);
    }

    // Change all textviews to dim red - recursive
    public void redText(ViewGroup viewGroup) {
        for (int count=0; count < viewGroup.getChildCount(); count++) {
            View view = viewGroup.getChildAt(count);
            if(view instanceof TextView) {
                ((TextView)view).setTextColor(0xFFAA0000);
            }
            else if (view instanceof ViewGroup) {
                redText((ViewGroup)view);
            }
        }
    }  */

}