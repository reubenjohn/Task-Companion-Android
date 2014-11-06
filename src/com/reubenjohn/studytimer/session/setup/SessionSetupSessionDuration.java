package com.reubenjohn.studytimer.session.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.R;

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
		return v;
	}

	private void bridgeXML(View v) {
		durationPicker = (TimePicker) v.findViewById(R.id.tp_total_time);
	}

}
