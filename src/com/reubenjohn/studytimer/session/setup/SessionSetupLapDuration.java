package com.reubenjohn.studytimer.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.timming.Time;

public class SessionSetupLapDuration extends Fragment {

	TimePicker lapDuration;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null)
			return null;

		View v = inflater.inflate(R.layout.fragment_lap_duration, container,
				false);

		bridgeXML(v);
		initializeFeilds();

		return v;
	}

	private void bridgeXML(View v) {
		lapDuration = (TimePicker) v.findViewById(R.id.tp_lap_duration);
	}

	public long getLapDuration() {
		return Time.getTimeInMilliseconds(0, 0, lapDuration.getCurrentHour(),
				lapDuration.getCurrentMinute(), 0);
	}

	private void initializeFeilds() {
		lapDuration.setIs24HourView(true);
	}

	public void setLapDuration(long sessionDuration) {
		Time duration = new Time(sessionDuration);
		lapDuration.setCurrentHour((int) duration.getHours());
		lapDuration.setCurrentMinute((int) duration.getMinutes());
	}

}
