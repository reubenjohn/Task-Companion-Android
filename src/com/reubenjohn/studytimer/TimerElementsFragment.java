package com.reubenjohn.studytimer;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.StudyTimer.MODES;
import com.reubenjohn.studytimer.preferences.STSP;
import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class TimerElementsFragment extends Fragment implements
		android.view.View.OnClickListener, FrameTimerListener {

	private int mode;
	private TextView tv_elapse, tv_total_elapse, tv_average;
	private TimerView elapse, totalElapse;
	int cached_lapCount;
	boolean realTimeAverageEnabled = true, running, lapTimeUp;
	int average;
	private long targetTime;

	private static class layout {
		public static View total_elapse;
		public static View elapse;
	}

	public interface TimerElementsListener {
		public void onTotalElapseSetManually(long elapse);
	};

	public TimerElementsListener timerElementsListener;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STSP.keys.running, running);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.timer_elements_fragment, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		setOnClickListeners();
		if (savedInstanceState != null) {

			if (savedInstanceState.getBoolean(STSP.keys.running, false)) {
				start();
			}
		}
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void start() {
		elapse.start();
		totalElapse.start();
		running = true;
	}

	public void stop() {
		elapse.stop();
		totalElapse.stop();
		running = false;
	}

	public void toggle() {
		if (running) {
			running = false;
		} else {
			running = true;
		}
		elapse.toggle();
		totalElapse.toggle();
	}

	public void lap(int lapCount) {
		elapse.reset();
		if (running) {
			elapse.start();
		}
		this.cached_lapCount = lapCount;
		lapTimeUp = false;
	}

	public void reset() {
		Log.d("StudyTimer", "TimerElements reset");
		elapse.reset();
		totalElapse.reset();
		cached_lapCount = 0;
		average = 0;
		targetTime = getActivity().getSharedPreferences(
				STSP.fileNames.currentSession, Context.MODE_PRIVATE).getLong(
				STSP.keys.targetTime, StudyTimer.defaults.targetTime);
		resetSavedData();
	}

	public void setTotalElapse(long elapse) {
		Log.d("StudyTimer", "Total elapse set: " + elapse);
		totalElapse.setElapse(elapse);
		timerElementsListener.onTotalElapseSetManually(elapse);
	}

	public boolean isRunning() {
		return running;
	}

	protected void resetSavedData() {
		SharedPreferences prefs = getActivity().getSharedPreferences(
				STSP.fileNames.currentSession, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(STSP.keys.elapse);
		editor.remove(STSP.keys.totalElapse);
		editor.remove(STSP.keys.stopTime);
		editor.commit();
	}

	public void initializeLapCount(int lapCount) {
		this.cached_lapCount = lapCount;
	}

	public String getFormatedElapse() {
		return elapse.timer.getFormattedTime();
	}

	public void setAverage(int average) {
		this.average = average;
		if (!realTimeAverageEnabled)
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss", average));
	}

	public void setTargetTime(long targetTime) {
		this.targetTime = targetTime;
		SharedPreferences.Editor editor = getActivity().getSharedPreferences(
				STSP.fileNames.currentSession, Context.MODE_PRIVATE).edit();
		editor.putLong(STSP.keys.targetTime, targetTime);
		editor.commit();
	}

	public void addFrameTimerListenersTo(FrameTimer framer) {
		framer.addFrameTimerListener(elapse);
		framer.addFrameTimerListener(totalElapse);
		framer.addFrameTimerListener(this);
	}

	public void removeFrameTimerListenersFrom(FrameTimer framer) {
		framer.removeFrameTimerListener(elapse);
		framer.removeFrameTimerListener(totalElapse);
	}

	protected void bridgeXML(View v) {
		tv_elapse = (TextView) v.findViewById(R.id.tv_elapse);
		tv_total_elapse = (TextView) v.findViewById(R.id.tv_total_elapse);
		tv_average = (TextView) v.findViewById(R.id.tv_average);
		layout.total_elapse = (View) v.findViewById(R.id.total_elapse);
		layout.elapse = (View) v.findViewById(R.id.elapse);
	}

	protected void initializeFeilds() {
		TimerViewFactory factory = new TimerViewFactory();
		factory.setDefaultFormat("%MM:%SS.%s");

		elapse = factory.produceTimerView(tv_elapse);
		totalElapse = factory.produceTimerView(tv_total_elapse);

	}

	protected void setOnClickListeners() {
		layout.total_elapse.setOnClickListener(this);
		layout.elapse.setOnClickListener(this);
	}

	@Override
	public void onNewFrame() {
		if (realTimeAverageEnabled) {
			int realTimeAverage = (int) (average * cached_lapCount + elapse
					.getElapse()) / (cached_lapCount + 1);
			tv_average.setText(Time.getFormattedTime("%MM:%SS.%sss",
					realTimeAverage));
		}

	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {
	}

	public long getElapse() {
		return elapse.getElapse();
	}

	public long getTargetTime() {
		return targetTime;
	}

	public float getLapProgress() {
		if (targetTime == 0) {
			return -1;
		} else {
			return (100.f * elapse.getElapse()) / targetTime;
		}
	}

	/*
	 * private long getRemainingLapTime() { return targetTime - getElapse(); }
	 */
	@Override
	public void onClick(View v) {
		Log.d("StudyTimer", "Timer Element clicked");
		if (mode == MODES.SESSION_EDIT) {
			switch (v.getId()) {
			case R.id.total_elapse:
				// showTotalElapseDialog();
				break;
			case R.id.elapse:
				showElapseDialog();
				break;
			}
		}
	}

	private void showElapseDialog() {
		Log.d("StudyTimer", "Showing elapse dialog");
		Time time = new Time(getElapse());
		TimePickerDialog picker = new TimePickerDialog(getActivity(),
				new OnTimeSetListener() {
					int callCount = 0;

					@Override
					public void onTimeSet(TimePicker picker, int minute,
							int second) {
						if (callCount == 1) {
							elapse.setElapse(Time.getTimeInMilliseconds(0, 0,
									minute, second, 0));
						}
						callCount++;
					}
				}, time.getMinutes(), time.getSeconds(), true);
		picker.setTitle(R.string.session_edit_elapse_title);
		picker.setMessage(getResources().getString(
				R.string.session_edit_elapse_message));
		picker.show();
	}

	/*
	 * private void showTotalElapseDialog() { Log.d("StudyTimer",
	 * "Showing total elapse dialog"); TimePickerDialog picker = new
	 * TimePickerDialog(getActivity(), new OnTimeSetListener() { int callCount =
	 * 0;
	 * 
	 * @Override public void onTimeSet(TimePicker picker, int minute, int
	 * second) { if (callCount == 1) {
	 * setTotalElapse(Time.getTimeInMilliseconds(0, 0, minute, second, 0)); }
	 * callCount++; } }, 1, 0, true);
	 * picker.setTitle(R.string.session_edit_total_elapse_title);
	 * picker.setMessage(getResources().getString(
	 * R.string.session_edit_total_elapse_message)); picker.show(); }
	 */
	public void setMode(int MODE) {
		switch (MODE) {
		case MODES.NORMAL:
		case MODES.SESSION_EDIT:
			mode = MODE;
			Log.d("StudyTimer", "TimerElement mode set: " + mode);
			break;
		default:
			Log.d("StudyTimer", "Unknown TimerElement mode request received: "
					+ MODE);
		}
	}

	public void setTimerElementsListener(TimerElementsListener listener) {
		timerElementsListener = listener;
	}

	public void createNewSession(Bundle sessionInfo) {
		long newTarget = sessionInfo.getLong(STSP.keys.targetTime);
		if (newTarget > 0) {
			setTargetTime(newTarget);
		} else {
			Log.d("StudyTimer", "New target is invalid or not set");
		}
	}

	public void putSessionInfo(Bundle sessionInfo) {
		sessionInfo.putLong(STSP.keys.targetTime, getTargetTime());
		sessionInfo.putLong(STSP.keys.elapse, elapse.getElapse());
		sessionInfo.putLong(STSP.keys.totalElapse, totalElapse.getElapse());
		sessionInfo.putLong(STSP.keys.stopTime, System.currentTimeMillis());
		sessionInfo.putBoolean(STSP.keys.lapTimeUp, lapTimeUp);
	}

	public void loadSessionFromBundle(Bundle sessionInfo) {
		Log.d("StudyTimer", "Timer Elements resume state: running=" + running);
		elapse.setElapse(sessionInfo.getLong(STSP.keys.elapse, 0));
		totalElapse.setElapse(sessionInfo.getLong(STSP.keys.totalElapse, 0));
		targetTime = sessionInfo.getLong(STSP.keys.targetTime,
				StudyTimer.defaults.targetTime);
		lapTimeUp = sessionInfo.getBoolean(STSP.keys.lapTimeUp, false);
		if (running) {
			elapse.setStartTime(sessionInfo.getLong(STSP.keys.stopTime, 0));
			totalElapse
					.setStartTime(sessionInfo.getLong(STSP.keys.stopTime, 0));
		}

	}

}
