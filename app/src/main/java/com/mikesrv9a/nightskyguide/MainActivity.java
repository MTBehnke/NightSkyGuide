package com.mikesrv9a.nightskyguide;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity
    implements DSObjectsFragment.DSObjectsFragmentListener, DetailFragment.AddEditFABListener, ObservationAddFragment.SaveCompletedListener {

    private DSObjectsFragment dsObjectsFragment; // displays dsObject list

    // For location services
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mCurrentLocation;
    Context context;
    final int REQUEST_CHECK_SETTINGS = 1;
    final int REQUEST_LOCATION = 2;
    public Boolean locUpdates;
    public Boolean useGPS;  // pref: use_device_location
    //private Boolean hasLocPermissions;
    SharedPreferences preferences;

    // display DSObjectFragment when MainActivity first loads
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // set default values in the app's SharedPreferences if never changed
        // note - does not reset preferences back to default values if previously changed
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // start location services, including permissions checks, etc.
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        useGPS = preferences.getBoolean("use_device_location", false);
        locUpdates = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        if (savedInstanceState == null) {
            // create DSObjectsFragment
            dsObjectsFragment = new DSObjectsFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, dsObjectsFragment);
            transaction.commit();  // display DSObjectsFragment
        }
    }

    @Override
    protected void onResume () {
        super.onResume();
        // check if user has GPS/Network on and set location summary as required
        if (useGPS && !locUpdates) {
            checkPermissions();
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        if (locUpdates) {
            stopLocationUpdates();
        }
    }

    // display DSObjectFragment for selected dsObject
    @Override
    public void onDSObjectSelected(DSObject dsObjectSelected) {
        displayDSObject(dsObjectSelected, R.id.fragmentContainer);
    }

    // display a dsObject
    private void displayDSObject(DSObject dsObjectSelected, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify dsObject as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("dsObjectArrayListItem", dsObjectSelected);
        detailFragment.setArguments(arguments);


        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes DetailFragment to display
    }

    // display ObservationAddFragment for selected dsObject
    public void addObservationButtonClicked(DSObject dsObjectSelected) {
        displayObservAddEdit(dsObjectSelected, R.id.fragmentContainer);
    }

    // display addedit fragment
    private void displayObservAddEdit(DSObject dsObjectSelected, int viewID) {
        ObservationAddFragment observationFragment = new ObservationAddFragment();

        // specify dsObject as an argument to the addedit Fragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("dsObjectArrayListItem", dsObjectSelected);
        observationFragment.setArguments(arguments);


        // use a FragmentTransaction to display the addedit Fragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, observationFragment);
        transaction.addToBackStack(null);
        transaction.commit();  // causes addedit Fragment to display
    }

    // return to DetailFragment after observation record saved
    @Override
    public void onObservationSaved() {
        getSupportFragmentManager().popBackStack();
    }

    // Start Fused Location services
    protected void createLocationRequest() {
        // create the locatioon request and set parameters
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10*60*1000);  // preferred update rate
        mLocationRequest.setFastestInterval(5*60*1000);  // fastest rate app can handle updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        // get current locations settings of user's device
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    // check if location permissions enabled and if not, request permissions
    public void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    startLocationUpdates();
                } else {
                    // permission denied
                    useGPS = false;
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putBoolean("use_device_location", false);
                    edit.apply();
                }
            }
        }
    }

    private void startLocationUpdates() {
        // if settings are satisfied initialize location requests
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locUpdates = true;
                // All location settings are satisfied.
                //noinspection MissingPermission - this comment needs to stay here to stop inspection on next line
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
        })
                // if settings need to be changed prompt user
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // location settings are not satisfied, but this can be fixed by showing the user a dialog.
                                try {
                                    // show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // location settings are not satisfied, however no way to fix the settings so don't show dialog.
                                Toast.makeText(MainActivity.this, "Location Services Unavailable", Toast.LENGTH_LONG).show();
                                useGPS = false;
                                SharedPreferences.Editor edit = preferences.edit();
                                edit.putBoolean("use_device_location", false);
                                edit.apply();
                                break;
                        }
                    }
                });
    }

    // Get results from user dialog prompt to turn on location services for app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                        } else {
                            // Start location updates
                            // Note - in emulator location appears to be null if no other app is using GPS at time.
                            // So if just turning on device's location services getLastLocation will likely not return anything
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null /*looper*/);
                            locUpdates = true;
                         }
                        break;
                    case Activity.RESULT_CANCELED:
                        // user does not want to update setting.
                        useGPS = false;
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putBoolean("use_device_location", false);
                        edit.apply();
                        break;
                }
                break;
        }
    }

    // stop location updates
    private void stopLocationUpdates() {
        locUpdates = false;
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                 }
        });
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            mCurrentLocation = locationResult.getLastLocation();
            setLatLong(mCurrentLocation);
            //Toast.makeText(MainActivity.this, "GPS Update", Toast.LENGTH_SHORT).show();
            }
        };
    }

    // Set new latitude and longitude based on location results
    public void setLatLong(Location location) {
        double lastLat = location.getLatitude();
        double lastLong = location.getLongitude();
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("last_gps_lat", String.valueOf(lastLat));
        edit.putString("last_gps_long", String.valueOf(lastLong));
        edit.apply();
    }

}