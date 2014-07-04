package com.reubenjohn.studytimer;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.reubenjohn.studytimer.StudyTimer.MODES;
import com.reubenjohn.studytimer.StudyTimer.defaults;
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
	WakeLock wakeLock;

	private static class beepManager {
		static boolean enabled = true;
		static Handler handler;
		static LapProgressSoundPool player;
		static float decay = 2;
		static int cutOff = 100;
	}

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
		loadSettings();
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
		removeAllLapProgressSounds();
		super.onDestroy();
	}

	public void start() {
		elapse.start();
		totalElapse.start();
		running = true;
		if (beepManager.enabled) {
			postAllLapProgressSounds();
		}
	}

	public void stop() {
		elapse.stop();
		totalElapse.stop();
		running = false;
		if (beepManager.enabled)
			removeAllLapProgressSounds();
	}

	public void toggle() {
		if (running) {
			running = false;
			if (beepManager.enabled)
				removeAllLapProgressSounds();
		} else {
			running = true;
			if (beepManager.enabled)
				postAllLapProgressSounds();
		}
		elapse.toggle();
		totalElapse.toggle();
	}

	public void lap(int lapCount) {
		elapse.reset();
		if (running) {
			elapse.start();
			if (beepManager.enabled) {
				removeAllLapProgressSounds();
				postAllLapProgressSounds();
			}
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
		removeAllLapProgressSounds();
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

	public void setTargetTIme(long targetTime) {
		this.targetTime = targetTime;
		if (isRunning()) {
			removeAllLapProgressSounds();
			postAllLapProgressSounds();
		}
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
		wakeLock = ((PowerManager) getActivity().getSystemService(
				Context.POWER_SERVICE)).newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "StudyTimer running wakeLock");
		beepManager.handler = new Handler();
		beepManager.player = new LapProgressSoundPool(getActivity(), wakeLock);

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

	public long getBeepTime(int beepNumber) {
		if (beepNumber == Integer.MAX_VALUE)
			return targetTime;
		else
			return (long) (targetTime - targetTime
					/ (Math.pow(beepManager.decay, (float) beepNumber)));
	}

	public long getBeepDelay(int beepNumber) {
		return getBeepTime(beepNumber) - getElapse();
	}

	public int getBeepNumberAfter(long ms) {
		if (ms != targetTime)
			return (int) (Math.log(targetTime / (targetTime - ms)) / Math
					.log(beepManager.decay)) + 1;
		else
			return Integer.MAX_VALUE;
	}

	public long getBeepTimeAfter(long ms) {
		if (ms != targetTime)
			return getBeepTime(getBeepNumberAfter(ms));
		else
			return Long.MAX_VALUE;
	}

	public long getBeepDelayAfter(long ms) {
		if (ms <= targetTime)
			return getBeepTime((int) (Math.log(targetTime / (targetTime - ms)) / Math
					.log(2)) + 1) - getElapse();
		else
			return Long.MAX_VALUE;
	}

	private void postAllBeeps() {
		long beep;
		String delayList = Boolean.toString(targetTime
				- (beep = getBeepTime(1)) >= beepManager.cutOff)
				+ "&&" + Boolean.toString(beep >= getElapse()) + ": ";
		for (int i = getBeepNumberAfter(getElapse()); targetTime
				- (beep = getBeepTime(i)) >= beepManager.cutOff; i++) {
			if (beep >= getElapse()) {
				beepManager.handler.postDelayed(beepManager.player.beep, beep
						- getElapse());
				delayList += beep + ",";

			}
		}
		Log.d("StudyTimer", "Beeps are scheduled is " + delayList);
	}

	private void removeAllBeeps() {
		beepManager.handler.removeCallbacks(beepManager.player.beep);
	}

	private long getRemainingLapTime() {
		return targetTime - getElapse();
	}

	private void postCutOffSound() {
		long delay;
		if ((delay = getRemainingLapTime()) >= 0)
			beepManager.handler.postDelayed(beepManager.player.cutOff, delay);
	}

	private void removeCutOffSound() {
		beepManager.handler.removeCallbacks(beepManager.player.cutOff);
	}

	private void postAllLapProgressSounds() {
		Log.d("StudyTimer", "WakeLock acquired");
		wakeLock.acquire();
		postAllBeeps();
		postCutOffSound();
	}

	private void removeAllLapProgressSounds() {
		if (wakeLock.isHeld()){
			wakeLock.release();
			Log.d("StudyTimer", "WakeLock released");
		}
		removeAllBeeps();
		removeCutOffSound();
	}

	public void loadSettings() {
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getBaseContext());
		boolean newBeepEnabledValue = settings.getBoolean(
				StudyTimer.keys.settings.sounds.lap_progress_switch,
				StudyTimer.defaults.sounds.lapProgress);
		if (newBeepEnabledValue != beepManager.enabled) {
			beepManager.enabled = newBeepEnabledValue;
			Log.d("TimerElementsFragment",
					"Lap progress sound setting changed to "
							+ newBeepEnabledValue);
			if (running) {
				if (newBeepEnabledValue == true)
					postAllLapProgressSounds();
				else
					removeAllLapProgressSounds();
			}
		}
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

	public void createNewSession(Bundle sessionInfo) {
		long newTarget = sessionInfo.getLong(STSP.keys.targetTime);
		if (newTarget > 0) {
			setTargetTIme(newTarget);
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
			totalElapse.setStartTime(sessionInfo.getLong(STSP.keys.stopTime, 0));
		}
		
	}

}
