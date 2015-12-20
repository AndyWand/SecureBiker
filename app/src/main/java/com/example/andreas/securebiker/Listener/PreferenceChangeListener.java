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


            if (key.equals(AllPreferencesFragment.KEY_FENCES_RADIUS)) {
                Preference radiusPref = prefActivity.findPreference(key);
                int radius = sharedPreferences.getInt(AllPreferencesFragment.KEY_FENCES_RADIUS,50);
                radiusPref.setSummary(prefActivity.getString(R.string.settings_summary).replace("$1", "" + radius));
            }

            //   int radius = PreferenceManager.getDefaultSharedPreferences(prefActivity.getActivity()).getInt("SEEKBAR_VALUE", 50);

        }


    }
}
