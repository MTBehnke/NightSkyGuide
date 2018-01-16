package com.mikesrv9a.nightskyguide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Set;

public class SettingsFragment extends PreferenceFragment {

    // Viewing Location:
    SwitchPreference useDeviceLocation;  // Preference "use_device_location"
    ListPreference viewingLocation;  //  Preference "viewing_location"  (if not GPS/Network)
    String[] viewLocItem;  // Array for viewingLocation ListPreference
    String[] viewLocList;  // Array for viewingLocation ListPreference

    // locationParent > Edit Locations:
    PreferenceScreen updateLocParent;  // Parent to Update Locations
    PreferenceScreen update_loc1;
    PreferenceScreen update_loc2;
    PreferenceScreen update_loc3;
    PreferenceScreen update_loc4;
    PreferenceScreen update_loc5;

    // Update Locations > Location 1-5
    EditTextPreference locationEditText1;
    EditTextPreference latEditText1;
    EditTextPreference longEditText1;
    EditTextPreference locationEditText2;
    EditTextPreference latEditText2;
    EditTextPreference longEditText2;
    EditTextPreference locationEditText3;
    EditTextPreference latEditText3;
    EditTextPreference longEditText3;
    EditTextPreference locationEditText4;
    EditTextPreference latEditText4;
    EditTextPreference longEditText4;
    EditTextPreference locationEditText5;
    EditTextPreference latEditText5;
    EditTextPreference longEditText5;

    // Display Options:
    SwitchPreference displayPrevObserved;  // Pref:  display previously observed
    SwitchPreference displayBelowHoriz;  // Pref:  display objects below horizon
    ListPreference sortByPref;  // Pref:  sort list of objects by <list>
    MultiSelectListPreference displayObjectList;  // Pref:  select object lists to display (Planets, Messier, Caldwell)
    MultiSelectListPreference displayAtlasList;  // Pref:  select atlas lists to display (PSA, OITH, Sky Atlas)
    //MultiSelectListPreference displayConstList;  // Pref:  select which constellations to display objects

    DecimalFormat df = new DecimalFormat("#.0000");

    public Context context;
    SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // load the preferences from the XML resource
        addPreferencesFromResource(R.xml.preferences);

        // locationParent > Edit Locations:
        updateLocParent = (PreferenceScreen) findPreference("update_location_ref_screen");  // Parent to Update Locations
        update_loc1 = (PreferenceScreen) findPreference("update_loc1");
        update_loc2 = (PreferenceScreen) findPreference("update_loc2");
        update_loc3 = (PreferenceScreen) findPreference("update_loc3");
        update_loc4 = (PreferenceScreen) findPreference("update_loc4");
        update_loc5 = (PreferenceScreen) findPreference("update_loc5");

        // Update Locations > Location 1-5
        locationEditText1 = (EditTextPreference) findPreference("pref_location1");
        latEditText1 = (EditTextPreference) findPreference("pref_lat1");
        longEditText1 = (EditTextPreference) findPreference("pref_long1");
        locationEditText2 = (EditTextPreference) findPreference("pref_location2");
        latEditText2 = (EditTextPreference) findPreference("pref_lat2");
        longEditText2 = (EditTextPreference) findPreference("pref_long2");
        locationEditText3 = (EditTextPreference) findPreference("pref_location3");
        latEditText3 = (EditTextPreference) findPreference("pref_lat3");
        longEditText3 = (EditTextPreference) findPreference("pref_long3");
        locationEditText4 = (EditTextPreference) findPreference("pref_location4");
        latEditText4 = (EditTextPreference) findPreference("pref_lat4");
        longEditText4 = (EditTextPreference) findPreference("pref_long4");
        locationEditText5 = (EditTextPreference) findPreference("pref_location5");
        latEditText5 = (EditTextPreference) findPreference("pref_lat5");
        longEditText5 = (EditTextPreference) findPreference("pref_long5");

        // Viewing Location:
        useDeviceLocation = (SwitchPreference) findPreference("use_device_location");
        viewingLocation = (ListPreference) findPreference("viewing_location");

        // Display Options:
        displayPrevObserved = (SwitchPreference) findPreference("pref_show_observed");  // Pref:  display previously observed
        displayBelowHoriz = (SwitchPreference) findPreference("pref_show_below_horiz");  // Pref:  display objects below horizon
        sortByPref = (ListPreference) findPreference("pref_sort_by");  // Pref:  sort list of objects by <list>
        displayObjectList = (MultiSelectListPreference) findPreference("multi_pref_object_list");   // Pref:  select object lists to display
        displayObjectList.setSummary(updateObjectList(displayObjectList.getValues().toString()));
        displayAtlasList = (MultiSelectListPreference) findPreference("multi_pref_atlas_list");  // Pref:  select atlas lists to display
        displayAtlasList.setSummary(updateAtlasList(displayAtlasList.getValues().toString()));
        //displayConstList = (MultiSelectListPreference) findPreference("multi_pref_const_month");
        //Log.i("Start: ", displayConstList.getValues().toString());

        updateLocSummaries();
        //setObjectListSummary();

        // start location services, including permissions checks, etc.
        context = getActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        // set GPS/Network SwitchPreferences
        useDeviceLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object useDeviceLoc) {
                Boolean turnGPSOn = (Boolean) useDeviceLoc;  // Use GPS/Network Location changed to on/off (before preference saved)
                if (turnGPSOn) {           // if user is trying to turn GPS on
                    ((SettingsActivity) getActivity()).useGPS = true;
                    ((SettingsActivity) getActivity()).checkPermissions();    // if checkPermissions opens dialog box then onChange completes, then onPause due to dialog box.  After dialog closed then onResume called.
                }
                else {
                    ((SettingsActivity) getActivity()).useGPS = false;
                    ((SettingsActivity) getActivity()).stopLocationUpdates();
                }
                setLocSummary();
                return true;
            }
        });

        viewingLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener(){
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                updateLocationList();
                return true;
            }
        });

        setLocationPrefListeners();  // sets Location 1 - 5 onChangePreferenceListeners

        // set Display Options SwitchPreferences
        displayPrevObserved.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });
        displayBelowHoriz.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });
        sortByPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });
        displayObjectList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected = newValue.toString();
                String objectList = updateObjectList(selected);
                if (objectList.equals("None")) {
                    Toast.makeText(getActivity(),"Must select at least one Object List", Toast.LENGTH_SHORT).show();
                    return false;
                }
                displayObjectList.setSummary(objectList);
                return true;
            }
        });
        displayAtlasList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected = newValue.toString();
                String atlasList = updateAtlasList(selected);
                displayAtlasList.setSummary(atlasList);
                return true;
            }
        });
        /*displayConstList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected = newValue.toString();
                Log.i("Const: ",selected);
                return true;
            }
        });*/
    }

    // display this fragment's menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_settings, menu);
    }

    // display selected menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_credits:
                Intent credits = new Intent(getActivity(), AppInfoActivity.class);
                credits.putExtra("appInfoKey", 4);
                startActivity(credits);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String updateObjectList(String selected) {
        int listNum = 0;
        String objectList = "";
        if (selected.contains("P")) {
            objectList="Planets";
            listNum++;}
        if (selected.contains("M")) {
            if (listNum >= 1) {objectList = objectList + ", ";}
            objectList = objectList + "Messier";
            listNum++;}
        if (selected.contains("C")) {
            if (listNum >= 1) {objectList = objectList + ", ";}
            objectList = objectList + "Caldwell";
            listNum++;}
        if (listNum == 0) {objectList = "None";}
        return objectList;
    }

    public String updateAtlasList(String selected) {
        int listNum = 0;
        String objectList = "";
        if (selected.contains("P")) {
            objectList="Pocket Sky Atlas";
            listNum++;}
        if (selected.contains("O")) {
            if (listNum >= 1) {objectList = objectList + ", ";}
            objectList = objectList + "Objects in the Heavens";
            listNum++;}
        if (selected.contains("S")) {
            if (listNum >= 1) {objectList = objectList + ", ";}
            objectList = objectList + "Sky Atlas 2000.0";
            listNum++;}
        if (listNum == 0) {objectList = "None";}
        return objectList;
    }

    void updateLocSummaries() {
        // Location 1 (required)
        locationEditText1.setSummary(locationEditText1.getText());
        latEditText1.setSummary(latEditText1.getText());
        longEditText1.setSummary(longEditText1.getText());

        // Location 2
        if (("").equals(locationEditText2.getText())) {
            locationEditText2.setSummary(R.string.location_null);
            latEditText2.setSummary("");
            longEditText2.setSummary("");}
        else {
            locationEditText2.setSummary(locationEditText2.getText());
            latEditText2.setSummary(latEditText2.getText());
            longEditText2.setSummary(longEditText2.getText());}

        // Location 3
        if (("").equals(locationEditText3.getText())) {
            locationEditText3.setSummary(R.string.location_null);
            latEditText3.setSummary("");
            longEditText3.setSummary("");}
        else {
            locationEditText3.setSummary(locationEditText3.getText());
            latEditText3.setSummary(latEditText3.getText());
            longEditText3.setSummary(longEditText3.getText());}

        // Location 4
        if (("").equals(locationEditText4.getText())) {
            locationEditText4.setSummary(R.string.location_null);
            latEditText4.setSummary("");
            longEditText4.setSummary("");}
        else {
            locationEditText4.setSummary(locationEditText4.getText());
            latEditText4.setSummary(latEditText4.getText());
            longEditText4.setSummary(longEditText4.getText());}

        // Location 5
        if (("").equals(locationEditText5.getText())) {
            locationEditText5.setSummary(R.string.location_null);
            latEditText5.setSummary("");
            longEditText5.setSummary("");}
        else {
            locationEditText5.setSummary(locationEditText5.getText());
            latEditText5.setSummary(latEditText5.getText());
            longEditText5.setSummary(longEditText5.getText());}

        updateLocParents();
    }

    void updateLocParents() {
        update_loc1.setSummary(locationEditText1.getSummary());
        update_loc2.setSummary(locationEditText2.getSummary());
        update_loc3.setSummary(locationEditText3.getSummary());
        update_loc4.setSummary(locationEditText4.getSummary());
        update_loc5.setSummary(locationEditText5.getSummary());
        updateLocationList();
    }

    // creates new array of user locations for use in viewingLocation listpreference
    void updateLocationList() {   // location when GPS/Network not in use
        int numLoc = 1;
        String tempInitItem[] = {"1","2","3","4","5"};
        String tempLocItem[] = new String[5];
        String tempLocList[] = new String[5];
        tempLocItem[0] = "1";
        tempLocList[0] = locationEditText1.getText();

        if (!("").equals(locationEditText2.getText())) {
            tempLocItem[numLoc] = tempInitItem[1];
            tempLocList[numLoc] = locationEditText2.getText();
            numLoc++;}

        if (!("").equals(locationEditText3.getText())) {
            tempLocItem[numLoc] = tempInitItem[2];
            tempLocList[numLoc] = locationEditText3.getText();
            numLoc++;}

        if (!("").equals(locationEditText4.getText())) {
            tempLocItem[numLoc] = tempInitItem[3];
            tempLocList[numLoc] = locationEditText4.getText();
            numLoc++;}

        if (!("").equals(locationEditText5.getText())) {
            tempLocItem[numLoc] = tempInitItem[4];
            tempLocList[numLoc] = locationEditText5.getText();
            numLoc++;}

        viewLocItem = Arrays.copyOf(tempLocItem,numLoc);
        viewLocList = Arrays.copyOf(tempLocList,numLoc);
        viewingLocation.setEntryValues(viewLocItem);
        viewingLocation.setEntries(viewLocList);
    }

    // set latitude and longitude preference values based on selected location
    public void setLatLongPref() {
        String locLat = null;
        String locLong = null;
        switch (viewingLocation.getValue()) {
            case "2":
                locLat = latEditText2.getText();
                locLong = longEditText2.getText();
                break;
            case "3":
                locLat = latEditText3.getText();
                locLong = longEditText3.getText();
                break;
            case "4":
                locLat = latEditText4.getText();
                locLong = longEditText4.getText();
                break;
            case "5":
                locLat = latEditText5.getText();
                locLong = longEditText5.getText();
                break;
            case "1":
                locLat = latEditText1.getText();
                locLong = longEditText1.getText();
                break;
        }
        SharedPreferences pref;
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("edit_text_pref_lat", locLat);
        edit.putString("edit_text_pref_long", locLong);
        edit.apply();
    }

        // set GPS/Network Location and Location summaries
    public void setLocSummary() {
        if (((SettingsActivity) getActivity()).useGPS) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Double gpsLat = Double.parseDouble(preferences.getString("last_gps_lat", getString(R.string.default_latitude)));
            Double gpsLong = Double.parseDouble(preferences.getString("last_gps_long", getString(R.string.default_longitude)));
            viewingLocation.setSummary("Latitude:  " + df.format(gpsLat) + "   /   Longitude:  " + df.format(gpsLong));
            useDeviceLocation.setSummary("Yes");
            viewingLocation.setEnabled(false);
        }
        else {
            useDeviceLocation.setSummary("No");
            viewingLocation.setSummary("%s");
            viewingLocation.setEnabled(true);
        }
    }

    // Set onPreferenceChangeListeners
    private void setLocationPrefListeners() {
        // Location 1 OnPreferenceChangeListeners
        locationEditText1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLocName) {
                String newLoc = newLocName.toString();
                if (newLoc.trim().length() == 0) {newLoc = "";}  // blank or all spaces
                if (("").equals(newLoc)) {
                    Toast.makeText(getActivity(), "Location Invalid / Required", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    locationEditText1.setText(newLocName.toString());
                    updateLocSummaries();
                    return true;
                }
            }
        });

        latEditText1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lat = Double.parseDouble(newLat.toString());
                if (lat > -90 && lat < 90) {
                    latEditText1.setSummary(newLat.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Latitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        longEditText1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLong) {
                try {
                    double longCheck = Double.parseDouble(newLong.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lon = Double.parseDouble(newLong.toString());
                if (lon >= -180 && lon <= 180) {
                    longEditText1.setSummary(newLong.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Longitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        // Location 2 OnPreferenceChangeListeners
        locationEditText2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLocName) {
                String newLoc = newLocName.toString();
                if (newLoc.trim().length() == 0) {newLoc = "";}  // blank or all spaces
                if (("").equals(newLoc) && viewingLocation.getValue() == "2") {
                    Toast.makeText(getActivity(), "Cannot null current viewing location", Toast.LENGTH_LONG).show();
                    return false;}
                locationEditText2.setText(newLoc);
                updateLocSummaries();
                return true;
            }
        });

        latEditText2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lat = Double.parseDouble(newLat.toString());
                if (lat > -90 && lat < 90) {
                    latEditText2.setSummary(newLat.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Latitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        longEditText2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLong) {
                try {
                    double longCheck = Double.parseDouble(newLong.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lon = Double.parseDouble(newLong.toString());
                if (lon >= -180 && lon <= 180) {
                    longEditText2.setSummary(newLong.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Longitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        // Location 3 OnPreferenceChangeListeners
        locationEditText3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLocName) {
                String newLoc = newLocName.toString();
                if (newLoc.trim().length() == 0) {newLoc = "";}  // blank or all spaces
                if (("").equals(newLoc) && viewingLocation.getValue() == "3") {
                    Toast.makeText(getActivity(), "Cannot null current viewing location", Toast.LENGTH_LONG).show();
                    return false;}
                locationEditText3.setText(newLoc);
                updateLocSummaries();
                return true;
            }
        });

        latEditText3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lat = Double.parseDouble(newLat.toString());
                if (lat > -90 && lat < 90) {
                    latEditText3.setSummary(newLat.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Latitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        longEditText3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLong) {
                try {
                    double longCheck = Double.parseDouble(newLong.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lon = Double.parseDouble(newLong.toString());
                if (lon >= -180 && lon <= 180) {
                    longEditText3.setSummary(newLong.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Longitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        // Location 4 OnPreferenceChangeListeners
        locationEditText4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLocName) {
                String newLoc = newLocName.toString();
                if (newLoc.trim().length() == 0) {newLoc = "";}  // blank or all spaces
                if (("").equals(newLoc) && viewingLocation.getValue() == "4") {
                    Toast.makeText(getActivity(), "Cannot null current viewing location", Toast.LENGTH_LONG).show();
                    return false;}
                locationEditText4.setText(newLoc);
                updateLocSummaries();
                return true;
            }
        });

        latEditText4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lat = Double.parseDouble(newLat.toString());
                if (lat > -90 && lat < 90) {
                    latEditText4.setSummary(newLat.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Latitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        longEditText4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLong) {
                try {
                    double longCheck = Double.parseDouble(newLong.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lon = Double.parseDouble(newLong.toString());
                if (lon >= -180 && lon <= 180) {
                    longEditText4.setSummary(newLong.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Longitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        // Location 5 OnPreferenceChangeListeners
        locationEditText5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLocName) {
                String newLoc = newLocName.toString();
                if (newLoc.trim().length() == 0) {newLoc = "";}  // blank or all spaces
                if (("").equals(newLoc) && viewingLocation.getValue() == "5") {
                    Toast.makeText(getActivity(), "Cannot null current viewing location", Toast.LENGTH_LONG).show();
                    return false;}
                locationEditText5.setText(newLoc);
                updateLocSummaries();
                return true;
            }
        });

        latEditText5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lat = Double.parseDouble(newLat.toString());
                if (lat > -90 && lat < 90) {
                    latEditText5.setSummary(newLat.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Latitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

        longEditText5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLong) {
                try {
                    double longCheck = Double.parseDouble(newLong.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                double lon = Double.parseDouble(newLong.toString());
                if (lon >= -180 && lon <= 180) {
                    longEditText5.setSummary(newLong.toString());
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Longitude", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });
    }

}