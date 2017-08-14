package com.lordlobo.loboweather.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.lordlobo.loboweather.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
    }

    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.app_preferences);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListenersBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object value) {
//                    String stringValue = value.toString();
//
//                    if (preference instanceof ListPreference) {
//                        // For list preferences, look up the correct display value in
//                        // the preference's 'entries' list.
//                        ListPreference listPreference = (ListPreference) preference;
//                        int index = listPreference.findIndexOfValue(stringValue);
//
//                        // Set the summary to reflect the new value.
//                        preference.setSummary(
//                                index >= 0
//                                        ? listPreference.getEntries()[index]
//                                        : null);
//
//                    } else if (preference instanceof RingtonePreference) {
//                        // For ringtone preferences, look up the correct display value
//                        // using RingtoneManager.
//                        if (TextUtils.isEmpty(stringValue)) {
//                            // Empty values correspond to 'silent' (no ringtone).
//                            preference.setSummary(R.string.pref_ringtone_silent);
//
//                        } else {
//                            Ringtone ringtone = RingtoneManager.getRingtone(
//                                    preference.getContext(), Uri.parse(stringValue));
//
//                            if (ringtone == null) {
//                                // Clear the summary if there was a lookup error.
//                                preference.setSummary(null);
//                            } else {
//                                // Set the summary to reflect the new ringtone display
//                                // name.
//                                String name = ringtone.getTitle(preference.getContext());
//                                preference.setSummary(name);
//                            }
//                        }
//
//                    } else {
//                        // For all other preferences, set the summary to the value's
//                        // simple string representation.
//                        preference.setSummary(stringValue);
//                    }
                    return true;
                }
            };

}
