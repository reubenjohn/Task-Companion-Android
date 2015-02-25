package com.aspirephile.studytimer.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.aspirephile.shared.debug.Logger;
import com.aspirephile.shared.debug.NullPointerAsserter;
import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.StudyTimer;
import com.aspirephile.studytimer.timming.Time;

public class SessionSetupSessionDuration extends Fragment {
	private Logger l = new Logger(SessionSetupLapDuration.class);
	private NullPointerAsserter asserter = new NullPointerAsserter(
			SessionSetupLapDuration.class);

	long duration;

	TimePicker durationPicker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		l.d("Creating " + SessionSetupFragment.class.toString());
		if (container == null)
			return null;
		l.d("Inflating layout");
		View v = inflater.inflate(R.layout.fragment_session_duration,
				container, false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void initializeFeilds() {
		l.d("Initializing feilds");
		durationPicker.setIs24HourView(true);
		updateSessionDuration();
	}

	private void bridgeXML(View v) {
		l.d("Bridging XML");
		durationPicker = (TimePicker) v.findViewById(R.id.tp_session_duration);
		if (asserter.assertPointer(durationPicker))
			l.d("Bridging sucessful");
	}

	void setSessionDuration(long sessionDuration) {
		duration = sessionDuration;
		updateSessionDuration();

	}

	private void updateSessionDuration() {
		if (asserter.assertPointer(durationPicker)) {
			durationPicker.setCurrentHour((int) Time.getHours(duration));
			durationPicker.setCurrentMinute((int) Time.getMinutes(duration));
		}
	}

	public long getSessionDuration() {
		if (asserter.assertPointer(durationPicker))
			return Time.getTimeInMilliseconds(0,
					durationPicker.getCurrentHour(),
					durationPicker.getCurrentMinute(), 0, 0);
		else
			return StudyTimer.defaults.sessionDuration;
	}

}
