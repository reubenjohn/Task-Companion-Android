package com.reubenjohn.studytimer.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.reubenjohn.studytimer.R;

public class Preferences extends PreferenceActivity {
	private static int prefs = R.xml.preferences;

	PF pf;

	public final OnPreferenceChangeListener prefListener = new OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Intent i = new Intent();
			i.putExtra("KEY_RESET_REQUESTED", true);
			setResult(RESULT_OK, i);
			finish();
			return false;
		}
	};

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			getClass().getMethod("getFragmentManager");
			AddResourceApi11AndGreater();
		} catch (NoSuchMethodException e) { // Api < 11
			AddResourceApiLessThan11();
		}
	}

	@SuppressWarnings("deprecation")
	protected void AddResourceApiLessThan11() {
		addPreferencesFromResource(prefs);
		Preference p = findPreference("checkbox_key");
		Log.d("StudyTimer", "PreferenceListener set to Activity: " + (p != null));
		if (p != null) {
			p.setOnPreferenceChangeListener(prefListener);
		}
	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		pf = new PF();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, pf).commit();
		pf.setPrefListener(prefListener);
	}

}