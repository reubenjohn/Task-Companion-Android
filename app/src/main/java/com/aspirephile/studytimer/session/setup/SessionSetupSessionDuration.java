package com.aspirephile.studytimer.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.timming.Time;

public class SessionSetupSessionDuration extends Fragment {

	TimePicker durationPicker;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;
		View v = inflater.inflate(R.layout.fragment_session_duration,
				container, false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void initializeFeilds() {
		durationPicker.setIs24HourView(true);
	}

	private void bridgeXML(View v) {
		durationPicker = (TimePicker) v.findViewById(R.id.tp_session_duration);
	}

	void setSessionDuration(long sessionDuration) {
		Time duration = new Time(sessionDuration);
		durationPicker.setCurrentHour((int) duration.getMinutes());
		durationPicker.setCurrentMinute((int) duration.getSeconds());

	}

	public long getSessionDuration() {
		return Time.getTimeInMilliseconds(0, durationPicker.getCurrentHour(),
				durationPicker.getCurrentMinute(), 0, 0);
	}

}
