// Fragment subclass that displays one dsObject's details

package com.mikesrv9a.nightskyguide;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

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
    private TextView altTextView; // displays dsObject's altitude
    private TextView azTextView; // displays dsObject's azimuth
    private TextView riseTextView;  // displays dsObject's rise time
    private TextView setTextView;  // displays  dsObject's set time

    private ImageView constImageView; // displays dsObject's constellation image
    private Bitmap constImage;


    // called when DetailFragment's view needs to be created
    @Override
    public View onCreateView(
        LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(false);  // this fragment has no menu items to display

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
        altTextView = (TextView) view.findViewById(R.id.altTextView);
        azTextView = (TextView) view.findViewById(R.id.azTextView);
        riseTextView = (TextView) view.findViewById(R.id.riseTextView);
        setTextView = (TextView) view.findViewById(R.id.setTextView);


        // set the TextViews
        objectIdTextView.setText(dsObject.getDsoObjectID());
        String typeAbbr = dsObject.getDsoType();
        typeTextView.setText(AstroCalc.getDSOType(typeAbbr));
        magTextView.setText(Double.toString(dsObject.getDsoMag()));
        sizeTextView.setText(dsObject.getDsoSize());
        distTextView.setText(dsObject.getDsoDist());
        raTextView.setText(AstroCalc.convertDDToHMS(dsObject.getDsoRA()));
        decTextView.setText(AstroCalc.convertDDToDMS(dsObject.getDsoDec()));
        String constAbbr = dsObject.getDsoConst();
        constTextView.setText(AstroCalc.getConstName(constAbbr));
        nameTextView.setText(dsObject.getDsoName());
        psaTextView.setText(dsObject.getDsoPSA());
        oithTextView.setText(dsObject.getDsoOITH());
        altTextView.setText((Integer.toString((int)Math.round(dsObject.getDsoAlt()))) + "°");
        azTextView.setText((Integer.toString((int)Math.round(dsObject.getDsoAz()))) + "°");
        riseTextView.setText(dsObject.getDsoRiseTimeStr());
        setTextView.setText(dsObject.getDsoSetTimeStr());

        // display constellation image
        String constName = "images/" + dsObject.getDsoConst() + ".gif";
        constImageView = (ImageView) view.findViewById(R.id.constImageView);
        Bitmap bm = loadConstImage(constName);   // display constellation .gif on detail screen
        constImageView.setImageBitmap(bm);

        return view;
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
            Toast.makeText(getActivity(),"error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return constImage;
    }

}