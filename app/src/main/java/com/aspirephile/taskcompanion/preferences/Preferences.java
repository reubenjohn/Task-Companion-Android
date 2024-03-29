package com.aspirephile.taskcompanion.preferences;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.taskcompanion.R;

public class Preferences extends PreferenceActivity {
    private Logger l = new Logger(Preferences.class);

    public static final int prefs = R.xml.settings;

    PreferencesFragment pf;
    public final OnPreferenceChangeListener prefListener = new OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Intent i = new Intent();
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
    }

    @TargetApi(11)
    protected void AddResourceApi11AndGreater() {
        l.d("Using Preferences fragment instead since API>=11");
        pf = new PreferencesFragment();
        pf.setPrefListener(prefListener);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, pf).commit();
    }

}