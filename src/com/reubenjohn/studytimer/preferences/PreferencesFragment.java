package com.reubenjohn.studytimer.preferences;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;

@TargetApi(11)
public class PreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(Preferences.prefs); // outer class
		// private members seem to be visible for inner class, and
		// making it static made things so much easier
	}

	public void setPrefListener(OnPreferenceChangeListener listener) {
		Preference p = findPreference("checkbox_key");
		Log.d("StudyTimer", "PreferenceListener set to fragment: " + (p != null));
		if (p != null) {
			p.setOnPreferenceChangeListener(listener);
		}
	}
}