package com.example.andreas.securebiker.Fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.example.andreas.securebiker.R;

/**
 * Created by Andreas on 13.12.2015.
 */
public class GeneralFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
        PreferenceCategory fakeHeader = new PreferenceCategory(getContext());
        fakeHeader.setTitle(R.string.pref_header_notifications);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);
**/
        addPreferencesFromResource(R.xml.pref_general);
    }
}
