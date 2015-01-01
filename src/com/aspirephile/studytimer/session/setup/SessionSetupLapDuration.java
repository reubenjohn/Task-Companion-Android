package com.aspirephile.studytimer.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.debug.NullPointerAsserter;
import com.aspirephile.studytimer.timming.Time;

public class SessionSetupLapDuration extends Fragment {
	NullPointerAsserter asserter = new NullPointerAsserter(
			SessionSetupFragment.class.getName());

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
		
		asserter.assertPointer(lapDuration);
	}

	public long getLapDuration() {
		return Time.getTimeInMilliseconds(0, 0, lapDuration.getCurrentHour(),
				lapDuration.getCurrentMinute(), 0);
	}

	private void initializeFeilds() {
		lapDuration.setIs24HourView(true);
	}

	public void setLapDuration(long lapDuration) {
		Time duration = new Time(lapDuration);
		this.lapDuration.setCurrentHour((int) duration.getHours());
		this.lapDuration.setCurrentMinute((int) duration.getMinutes());

	}

}
