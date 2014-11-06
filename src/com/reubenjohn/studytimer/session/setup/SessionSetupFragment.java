package com.reubenjohn.studytimer.session.setup;

import java.util.List;
import java.util.Vector;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.StudyTimer;
import com.reubenjohn.studytimer.data.SessionParams;
import com.reubenjohn.studytimer.welcome.WelcomePageAdapter;

public class SessionSetupFragment extends Fragment implements OnClickListener {

	private ViewPager durationPager;
	private DurationPageAdapter durationPageAdapter;
	Button totalLapsIncrement, totalLapsDecrement;
	EditText totalLaps;

	SessionSetupLapDuration lapDuration;
	SessionSetupSessionDuration sessionDuration;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_session_setup, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		return v;
	}

	private void bridgeXML(View v) {
		durationPager = (ViewPager) v
				.findViewById(R.id.vp_session_setup_duration);
		totalLapsIncrement = (Button) v
				.findViewById(R.id.b_total_laps_increment);
		totalLapsDecrement = (Button) v
				.findViewById(R.id.b_total_laps_decrement);
		totalLaps = (EditText) v.findViewById(R.id.et_total_laps);
	}

	private void initializeFeilds() {
		lapDuration = new SessionSetupLapDuration();
		sessionDuration = new SessionSetupSessionDuration();
		totalLapsIncrement.setOnClickListener(this);
		totalLapsDecrement.setOnClickListener(this);
		totalLaps.setText(String.valueOf(StudyTimer.defaults.totalLaps));
		initializePaging();
	}

	private void initializePaging() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(lapDuration);
		fragments.add(sessionDuration);

		durationPageAdapter = new DurationPageAdapter(getActivity()
				.getSupportFragmentManager(), fragments);

		Resources res = getResources();
		durationPageAdapter.setPageTitles(
				res.getString(R.string.session_create_target_title),
				res.getString(R.string.session_create_total_time_title));
		durationPager.setAdapter(durationPageAdapter);
	}

	private int getTotalLaps() {
		String raw = totalLaps.getText().toString();
		int processed;
		try {
			processed = Integer.parseInt(raw);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			processed = StudyTimer.defaults.totalLaps;
		} catch (Exception e) {
			e.printStackTrace();
			processed = StudyTimer.defaults.totalLaps;
		}
		if (processed < StudyTimer.prefs.minLaps)
			return StudyTimer.prefs.minLaps;
		if (processed > StudyTimer.prefs.maxLaps)
			return StudyTimer.prefs.maxLaps;
		return processed;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_total_laps_increment:
			totalLaps.setText(String.valueOf(incrementTotalLaps()));
			break;
		case R.id.b_total_laps_decrement:
			decrementTotalLaps();
			totalLaps.setText(String.valueOf(decrementTotalLaps()));
			break;
		default:
			Log.d(SessionSetupFragment.class.getName(), "Unknown item clicked");
			break;
		}
	}

	private int decrementTotalLaps() {
		int currentTotalLaps = getTotalLaps();
		if (currentTotalLaps <= StudyTimer.prefs.minLaps)
			return StudyTimer.prefs.minLaps;
		return --currentTotalLaps;

	}

	private int incrementTotalLaps() {

		int currentTotalLaps = getTotalLaps();
		if (currentTotalLaps >= StudyTimer.prefs.maxLaps)
			return StudyTimer.prefs.maxLaps;
		return ++currentTotalLaps;

	}

	public SessionParams getSessionParams() {
		SessionParams sessionParams = new SessionParams();
		sessionParams.setTotalLaps(getTotalLaps());
		sessionParams.setLapDuration(lapDuration.getLapDuration());
		return sessionParams;
	}

}
