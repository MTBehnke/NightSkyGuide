package com.mikesrv9a.nightskyguide;

import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    //public SettingsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // load the preferences from the XML resource
        addPreferencesFromResource(R.xml.preferences);

        // display current value for latitude
        final EditTextPreference latEditText = (EditTextPreference) findPreference("edit_text_pref_lat");
        latEditText.setSummary(latEditText.getText());

        // display current value for longitude
        final EditTextPreference longEditText = (EditTextPreference) findPreference("edit_text_pref_long");
        longEditText.setSummary(longEditText.getText());

        // set latitude preference change listener and validate input
        latEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double latCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                latEditText.setSummary(newLat.toString());
                return true;
            }
        });

        // set longitude preference change listener and validate input
        longEditText.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newLat) {
                try {
                    double longCheck = Double.parseDouble(newLat.toString());
                } catch (NumberFormatException nfe) {
                    return false;
                }
                longEditText.setSummary(newLat.toString());
                return true;
            }
        });


        // set Display Previously Observed SwitchPreference to on/off
        final SwitchPreference displayPrevObserved = (SwitchPreference) findPreference("pref_show_observed");
        displayPrevObserved.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });

        final SwitchPreference displayBelowHoriz = (SwitchPreference) findPreference("pref_show_below_horiz");
        displayBelowHoriz.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return true;
            }
        });

    }
}
