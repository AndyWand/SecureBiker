package com.example.andreas.securebiker.Listener;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.andreas.securebiker.Fragments.AllPreferencesFragment;
import com.example.andreas.securebiker.R;

/**
 * Created by Andreas on 20.12.2015.
 */
public class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

    PreferenceActivity prefActivity;

    public PreferenceChangeListener(PreferenceActivity prefActivity) {
        this.prefActivity = prefActivity;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {


        if (key.equals(AllPreferencesFragment.KEY_SYNC_FREQUENZ)) {
            Preference syncPref = prefActivity.findPreference(key);
            syncPref.setSummary(sharedPreferences.getString(AllPreferencesFragment.KEY_SYNC_FREQUENZ, ""));

        }
        if (key.equals(AllPreferencesFragment.KEY_FENCES_RADIUS)) {
            Preference radiusPref = prefActivity.findPreference(key);
            int radius = sharedPreferences.getInt(AllPreferencesFragment.KEY_FENCES_RADIUS, 50)+50;
            radiusPref.setSummary(prefActivity.getString(R.string.settings_summary).replace("$1", "" + radius)+prefActivity.getString(R.string.settings_unit));
        }

        if (key.equals(AllPreferencesFragment.KEY_ALARMDIALOGTIMER)) {
            Preference alarmTimerPref = prefActivity.findPreference(key);
            alarmTimerPref.setSummary(sharedPreferences.getString(AllPreferencesFragment.KEY_ALARMDIALOGTIMER,"10")+" "+prefActivity.getString(R.string.pref_alarm_timer_summary_unit));
        }

        /** Das macht keinen sinn: gibt den Pfad aus
        if (key.equals(AllPreferencesFragment.KEY_NOTIFI_MESSAGE_RING)) {
            Preference notiRingPref = prefActivity.findPreference(key);
            notiRingPref.setSummary(sharedPreferences.getString(key, ""));
        }
        **/
        if (key.equals(AllPreferencesFragment.KEY_NOTIFI_MESSAGE_VIB)) {
            Preference notiVibPref = prefActivity.findPreference(key);

            if (sharedPreferences.getBoolean(key, false)) {
                notiVibPref.setSummary(R.string.pref_vibrate_on);
            }
            else{notiVibPref.setSummary(R.string.pref_vibrate_off);

            }

        }

    }


}

