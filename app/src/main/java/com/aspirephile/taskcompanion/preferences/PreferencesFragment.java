package com.aspirephile.taskcompanion.preferences;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;

@TargetApi(11)
public class PreferencesFragment extends PreferenceFragment {
    private static Logger l = new Logger(PreferencesFragment.class);
    private NullPointerAsserter asserter = new NullPointerAsserter(l);

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(Preferences.prefs); // outer class
        // private members seem to be visible for inner class, and
        // making it static made things so much easier
    }

    public void setPrefListener(OnPreferenceChangeListener listener) {
        Preference p = findPreference("key_shake_to_lap");
        if (asserter.assertPointer(p)) {
            p.setOnPreferenceChangeListener(listener);
        }
    }
}