package com.reubenjohn.studytimer.preferences;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.reubenjohn.studytimer.R;

public class SessionSetup extends PreferenceActivity {
	private static int layout = R.xml.new_session;

	SessionCreateFragment sf;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("StudyTimer", "Creating SessionSetup");
		try {
			getClass().getMethod("getFragmentManager");
			AddResourceApi11AndGreater();
		} catch (NoSuchMethodException e) { // Api < 11
			AddResourceApiLessThan11();
		}
	}

	@SuppressWarnings("deprecation")
	protected void AddResourceApiLessThan11() {
		addPreferencesFromResource(layout);
	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		sf = new SessionCreateFragment();
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, sf).commit();
	}

}