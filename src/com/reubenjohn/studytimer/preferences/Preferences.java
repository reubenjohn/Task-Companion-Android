package com.reubenjohn.studytimer.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.reubenjohn.studytimer.R;

public class Preferences extends PreferenceActivity {
	private static int prefs = R.xml.preferences;

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
	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PF()).commit();
	}

	@TargetApi(11)
	public static class PF extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(Preferences.prefs); // outer class
			// private members seem to be visible for inner class, and
			// making it static made things so much easier
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.d("StudyTimer", "Preference "+position+" clicked");
		if (position == 1) {
			Intent i = new Intent();
			i.putExtra("KEY_RESET_REQUESTED", true);
			setResult(RESULT_OK, i);
			finish();
		}
		super.onListItemClick(l, v, position, id);
	}

}