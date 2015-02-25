package com.aspirephile.studytimer.session.setup;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.aspirephile.studytimer.R;
import com.aspirephile.studytimer.StudyTimer;
import com.aspirephile.studytimer.StudyTimer.defaults;
import com.aspirephile.studytimer.data.SessionParams;
import com.aspirephile.studytimer.debug.NullPointerAsserter;
import com.aspirephile.studytimer.preferences.STSP;

public class SessionSetupFragment extends Fragment implements OnClickListener {

	NullPointerAsserter asserter = new NullPointerAsserter(
			SessionSetupFragment.class.getName());

	private ViewPager durationPager;
	private DurationPageAdapter durationPageAdapter;
	Button totalLapsIncrement, totalLapsDecrement;
	EditText totalLaps;

	SessionSetupLapDuration lapDuration;
	SessionSetupSessionDuration sessionDuration;
	private OnPageChangeListener durationPageListener;

	private static class DurationPagerProperties {
		static int position;

		public static class FragmentPositions {

			protected static int lapDuration = 0;
			protected static int sessionDuration;

		}
	}

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
		setSessionParams(getSessionParamsFromPreferences());
	}

	private void setSessionParams(SessionParams params) {
		if (asserter.assertPointer(params)) {
			setTotalLaps(params.getTotalLaps());
			setLapDuration(params.getLapDuration());
		}
	}

	private void setLapDuration(long lapDuration) {
		this.lapDuration.setLapDuration(lapDuration);

	}

	private SessionParams getSessionParamsFromPreferences() {
		SharedPreferences sessionPrefs = getActivity().getSharedPreferences(
				STSP.fileNames.currentSession, Context.MODE_PRIVATE);
		long lapDuration = sessionPrefs.getLong(STSP.keys.targetTime,
				StudyTimer.defaults.lapDuration);
		int totalLaps = sessionPrefs.getInt(STSP.keys.totalLaps,
				defaults.totalLaps);
		Log.d("SessionSetupFragment", "Retreived lapDuration=" + lapDuration);
		Log.d("SessionSetupFragment", "Retreived totalLaps=" + totalLaps);
		SessionParams params = new SessionParams();
		params.setLapDuration(lapDuration);
		params.setTotalLaps(totalLaps);
		return params;

	}

	private void initializePaging() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(lapDuration);
		DurationPagerProperties.FragmentPositions.lapDuration = 0;
		fragments.add(sessionDuration);
		DurationPagerProperties.FragmentPositions.sessionDuration = 1;

		durationPageAdapter = new DurationPageAdapter(getActivity()
				.getSupportFragmentManager(), fragments);

		Resources res = getResources();
		durationPageAdapter.setPageTitles(
				res.getString(R.string.session_create_target_title),
				res.getString(R.string.session_create_total_time_title));
		durationPageListener = new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				DurationPagerProperties.position = position;

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == 1) {
					if (DurationPagerProperties.position == DurationPagerProperties.FragmentPositions.lapDuration) {
						sessionDuration.setSessionDuration(lapDuration
								.getLapDuration() * getTotalLaps());
					} else if (DurationPagerProperties.position == DurationPagerProperties.FragmentPositions.sessionDuration) {
						lapDuration.setLapDuration(sessionDuration
								.getSessionDuration() / getTotalLaps());
					}
				}
			}
		};
		durationPager.setOnPageChangeListener(durationPageListener);
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

	private void setTotalLaps(int totalLaps) {
		if (totalLaps <= StudyTimer.prefs.minLaps)
			totalLaps = StudyTimer.prefs.minLaps;
		else if (totalLaps >= StudyTimer.prefs.maxLaps)
			totalLaps = StudyTimer.prefs.maxLaps;
		this.totalLaps.setText(String.valueOf(totalLaps));
	}

	public SessionParams getSessionParams() {
		SessionParams sessionParams = new SessionParams();
		sessionParams.setTotalLaps(getTotalLaps());
		sessionParams.setLapDuration(lapDuration.getLapDuration());
		return sessionParams;
	}

}
