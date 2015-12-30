package com.example.andreas.securebiker.Fragments;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;

import com.example.andreas.securebiker.Listener.PreferenceChangeListener;
import com.example.andreas.securebiker.R;

/**
 * Created by Andreas on 16.12.2015.
 */
public class AllPreferencesFragment extends PreferenceFragment {

    public static final String KEY_SYNC_FREQUENZ = "sync_frequency";
    public static final String KEY_FENCES_RADIUS = "SEEKBAR_VALUE";
    public static final String KEY_ALARMDIALOGTIMER = "ALARMDIALOG";
    public static final String KEY_ALARMSWITCH ="ALARMSWITCH";
    public static final String KEY_NOTIFI_MESSAGE_RING = "notifications_new_message_ringtone";
    public static final String KEY_NOTIFI_MESSAGE_VIB = "notifications_new_message_vibrate";

    private SeekBarPreference seekBarPref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_all);
        /**
         * Instanziiert SeekBar-Stuff
         */

        // Get widgets :
        seekBarPref = (SeekBarPreference) this.findPreference(KEY_FENCES_RADIUS);

        // Set listener :
        //listener = new PreferenceChangeListener(this);
        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);

        // Set seekbar summary :
        int radius = getRadius();
        seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius));
        /**
         * Ende SeekBar-Stuff
         */
    }

    private Preference.OnPreferenceChangeListener seekbarPrefValueChangeListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            // Set seekbar summary :
            int radius = getRadius();
            preference.setSummary(getString(R.string.settings_summary).replace("$1", "" + radius));

            preference.setSummary("Current Value is: " + stringValue);

            return true;
        }
    };


    private static Preference.OnPreferenceChangeListener syncPrefValueChangeListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                doIfPrefIsList(preference, stringValue);

            } else if (preference instanceof RingtonePreference) {
                doIfPrefIsRing(preference, stringValue);

            } else if (preference instanceof SwitchPreference) {
                doIfPrefIsSwitch(preference, stringValue);


            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }


    };

    /**
     * Method is called when preference is an instance of RintonePreference
     *
     * @param preference
     * @param stringValue
     */

    private static void doIfPrefIsRing(Preference preference, String stringValue) {
        // For ringtone preferences, look up the correct display value
        // using RingtoneManager.
        if (TextUtils.isEmpty(stringValue)) {
            // Empty values correspond to 'silent' (no ringtone).
            preference.setSummary(R.string.pref_ringtone_silent);

        } else {
            Ringtone ringtone = RingtoneManager.getRingtone(
                    preference.getContext(), Uri.parse(stringValue));

            if (ringtone == null) {
                // Clear the summary if there was a lookup error.
                preference.setSummary(null);
            } else {
                // Set the summary to reflect the new ringtone display
                // name.
                String name = ringtone.getTitle(preference.getContext());
                preference.setSummary(name);

            }
        }
    }

    private static void doIfPrefIsList(Preference preference, String stringValue) {
        // For list preferences, look up the correct display value in
        // the preference's 'entries' list.
        ListPreference listPreference = (ListPreference) preference;
        int index = listPreference.findIndexOfValue(stringValue);

        // Set the summary to reflect the new value.
        preference.setSummary(
                index >= 0
                        ? listPreference.getEntries()[index]
                        : null);

    }

    private void doIfPrefIsSeekBar(Preference preference, String stringValue) {

        if (TextUtils.isEmpty(stringValue)) {
            preference.setSummary("");
        } else {

            SeekBarPreference seekBarPref = (SeekBarPreference) preference;
            int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);

            // Set seekbar summary :
            seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius));
        }
    }


    private static void doIfPrefIsSwitch(Preference preference, String stringValue) {

    }

    //TODO Add Preference for Attention-Timer

    private int getRadius() {
        // Adds 50 as minimum-value
        int r = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);
        return r + 50;
    }
}
