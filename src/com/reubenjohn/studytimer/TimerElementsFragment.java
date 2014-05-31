package com.reubenjohn.studytimer;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.StudyTimer.MODES;
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
	private long targetTime, nextBeep;
	SoundPool soundPool;

	static class sounds {
		private static int beep = 0, cutOff = 0;
	}

	int beepNumber = 1, beepCutOff = 50;

	private static class layout {
		public static View total_elapse;
		public static View elapse;
	}

	static long defaultTargetTime = Time.getTimeInMilliseconds(0, 0, 1, 0, 0);

	protected static class keys {
		public static final String elapse = "ELAPSE";
		public static final String lapTimeUp = "LAP_TIMEUP";
		public static String totalElapse = "TOTAL_ELAPSE";
		public static String running = "RUNNING";
		public static String stopTime = "STOP_TIME_TIME";
		public static String targetTime = "TARGET_TIME";
	}

	public interface TimerElementsListener {
		public void onTotalElapseSetManually(long elapse);
	};

	public TimerElementsListener timerElementsListener;

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(keys.running, running);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.timer_elements_fragment, container,
				false);
		bridgeXML(v);
		initializeFeilds();
		setOnClickListeners();
		initializeAudio();
		if (savedInstanceState != null) {

			if (savedInstanceState.getBoolean(keys.running, false)) {
				start();
			}
		}
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		Log.d("StudyTimer", "Timer Elements resume state: running=" + running);
		elapse.setElapse(prefs.getLong(keys.elapse, 0));
		totalElapse.setElapse(prefs.getLong(keys.totalElapse, 0));
		targetTime = prefs.getLong(keys.targetTime, defaultTargetTime);
		lapTimeUp = prefs.getBoolean(keys.lapTimeUp, false);
		if (running) {
			elapse.setStartTime(prefs.getLong(keys.stopTime, 0));
			totalElapse.setStartTime(prefs.getLong(keys.stopTime, 0));
		}
		updateBeepTime();
	}

	@Override
	public void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	public void onStop() {
		saveState();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		saveState();
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
		if (running)
			running = false;
		else
			running = true;
		elapse.toggle();
		totalElapse.toggle();
	}

	public void lap(int lapCount) {
		elapse.reset();
		if (running)
			elapse.start();
		this.cached_lapCount = lapCount;
		beepNumber = 1;
		updateBeepTime();
		lapTimeUp = false;
	}

	public void reset() {
		Log.d("StudyTimer", "TimerElements reset");
		elapse.reset();
		totalElapse.reset();
		cached_lapCount = 0;
		average = 0;
		targetTime = getActivity().getPreferences(Context.MODE_PRIVATE)
				.getLong(keys.targetTime, defaultTargetTime);
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
		SharedPreferences prefs = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.remove(keys.elapse);
		editor.remove(keys.totalElapse);
		editor.remove(keys.stopTime);
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

	public void setTargetTIme(long targetTime) {
		this.targetTime = targetTime;
		SharedPreferences.Editor editor = getActivity().getPreferences(
				Context.MODE_PRIVATE).edit();
		editor.putLong(keys.targetTime, targetTime);
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

	protected void initializeAudio() {
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		sounds.beep = soundPool.load(getActivity(), R.raw.beep, 1);
		sounds.cutOff = soundPool.load(getActivity(),
				R.raw.detonator_interface, 1);
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
			handleBeeps();
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

	public long getBeepTime(int beepNumber) {
		long beepTime = Long.MAX_VALUE;
		beepTime = (long) (targetTime - targetTime
				/ (Math.pow(2.f, (float) beepNumber)));
		return beepTime;
	}

	public long getBeepTimeAfter(long ms) {
		if (ms != targetTime)
			return getBeepTime((int) (Math.log(targetTime / (targetTime - ms)) / Math
					.log(2)) + 1);
		else
			return Long.MAX_VALUE;
	}

	public void updateBeepTime() {
		nextBeep = getBeepTimeAfter(getElapse());
		Log.d("StudyTimer", "Beep time after " + getElapse() + " is "
				+ nextBeep);
	}

	public void handleBeeps() {
		if (getElapse() >= targetTime - beepCutOff) {
			if (sounds.cutOff != 0 && !lapTimeUp) {
				soundPool.play(sounds.cutOff, 1, 1, 5, 0, 1);
				lapTimeUp = true;
			}
		} else if (getElapse() >= nextBeep) {
			if (sounds.beep != 0)
				soundPool.play(sounds.beep, 1, 1, 5, 0, 1);
			updateBeepTime();
		}
	}

	public void saveState() {
		SharedPreferences prefs = getActivity().getPreferences(
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(keys.elapse, elapse.getElapse());
		editor.putLong(keys.totalElapse, totalElapse.getElapse());
		editor.putLong(keys.stopTime, System.currentTimeMillis());
		editor.putBoolean(keys.lapTimeUp, lapTimeUp);
		editor.commit();
	}

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

	private void showTotalElapseDialog() {
		Log.d("StudyTimer", "Showing total elapse dialog");
		TimePickerDialog picker = new TimePickerDialog(getActivity(),
				new OnTimeSetListener() {
					int callCount = 0;

					@Override
					public void onTimeSet(TimePicker picker, int minute,
							int second) {
						if (callCount == 1) {
							setTotalElapse(Time.getTimeInMilliseconds(0, 0,
									minute, second, 0));
						}
						callCount++;
					}
				}, 1, 0, true);
		picker.setTitle(R.string.session_edit_total_elapse_title);
		picker.setMessage(getResources().getString(
				R.string.session_edit_total_elapse_message));
		picker.show();
	}

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

}
