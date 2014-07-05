package com.reubenjohn.studytimer;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.reubenjohn.studytimer.preferences.STSP;
import com.reubenjohn.studytimer.timming.Time;
import com.reubenjohn.studytimer.timming.Timer;
import com.reubenjohn.studytimer.timming.frametimer.FrameIntervalListener;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimer;
import com.reubenjohn.studytimer.timming.frametimer.FrameTimerListener;

public class StudyTimer implements FrameTimerListener, OnClickListener {

	public static final class MODES {

		public static final int NORMAL = 0;
		public static final int SESSION_EDIT = 1;

	}

	public FrameTimer framer;
	Timer runtime;
	TimerElementsFragment timerElements;
	LapsFragment lapsF;
	EmptyLapFragment emptyLap;
	FillerFragment filler;
	SharedPreferences.OnSharedPreferenceChangeListener prefsListener;
	FragmentManager fragM;
	private SharedPreferences sessionPrefs;

	public static class defaults {
		public static long targetTime = 5000;
		public static int totalLaps = 20;

		public static class sounds {
			public static boolean lapProgress = true;
		}

		public static String getConcatenatedDefaults() {
			return "{ " + "targetTime:" + targetTime + "," + " totalLaps" + ":"
					+ totalLaps + "," + " sounds.lapProgress" + ":"
					+ sounds.lapProgress + " }";
		}

		public static void loadFromResources(Resources resources) {
			targetTime = resources.getInteger(R.integer.target_time);
			totalLaps = resources.getInteger(R.integer.total_laps);
			sounds.lapProgress = resources
					.getBoolean(R.bool.lap_progress_sounds);
			Log.d("StudyTimer", "Loaded defaults from rescources->"
					+ getConcatenatedDefaults());
		}
	}

	public static class keys {
		public static class settings {
			public static class sounds {
				public static final String lap_progress_switch = "key_sounds_lap_progress_switch";
			}
		}
	}

	private static class logging {
		static boolean status = false;
		static int loggingInterval = 300;
		static FrameIntervalListener listener = null;
	}

	public class SessionInfo {
		public long totalElapse;
		public long currentElapse;
		public int laps;
	}

	StudyTimer(Handler thisHandler, FragmentManager fragM,
			SharedPreferences sessionPrefs) {
		framer = new FrameTimer(thisHandler);
		bridgeFragments(fragM);
		this.sessionPrefs = sessionPrefs;
		initializeFeilds();
		setListeners(framer);
		runtime.start();

	}

	protected void bridgeFragments(FragmentManager fragM) {
		this.fragM = fragM;
		timerElements = (TimerElementsFragment) fragM
				.findFragmentById(R.id.home_timer_elements);
		lapsF = (LapsFragment) fragM.findFragmentById(R.id.home_laps);
		emptyLap = (EmptyLapFragment) fragM
				.findFragmentById(R.id.home_empty_laps);
		filler = (FillerFragment) fragM
				.findFragmentById(R.id.filler_background);
		assert timerElements != null;
		assert lapsF != null;
		assert emptyLap != null;
		assert filler != null;
	}

	protected void initializeFeilds() {
		timerElements.initializeLapCount(lapsF.getLapCount());
		timerElements.setAverage(lapsF.getAverage());
		Time.setDefaultFormat("%MM:%SS.%s");
		framer.setInterval(100);
		runtime = new Timer();
		runtime.setFormat("%MM:%SS.%sss");
		setNoLapMode(lapsF.hasNoLaps());

	}

	public void startNewSession(SessionInfo sessionInfo) {
		if (sessionInfo == null) {
			resetSession();
		}
	}

	public void start() {
		timerElements.start();
	}

	public void stop() {
		timerElements.stop();
	}

	public void toggle() {
		timerElements.toggle();
	}

	public void lap() {
		if (lapsF != null) {
			lapsF.addLap(timerElements.getElapse());
			setNoLapMode(lapsF.hasNoLaps());
		}
		timerElements.setAverage(lapsF.getAverage());
		timerElements.lap(lapsF.getLapCount());
	}

	public void resetSession() {
		Log.d("StudyTimer", "reset() called");
		stop();
		timerElements.reset();
		lapsF.resetSession();
		setNoLapMode(true);
	}

	public boolean isRunning() {
		return timerElements.isRunning();
	}

	protected void resetRuntime() {
		resetSession();
		runtime.reset();
	}

	public void onPause() {
		Log.d("StudyTimer", "onPause() called");
		framer.stop();
		runtime.stop();
		saveSessionToPrefs();
	}

	public void onResume() {
		Log.d("StudyTimer", "onResume()");
		loadSessionFromBundle(getSessionBundleFromPrefs());
		framer.start();
		runtime.start();
	}

	public void onStop() {
	}

	public void logStatus() {
		Log.i("StudyTimer", getStatus());
	}

	public String getStatus() {
		return "Status: Runtime[" + runtime.getFormattedTime() + "] Frame["
				+ framer.getFrame() + "] " + getTimerElementsStatus() + " "
				+ getLapsFStatus();
	}

	public String getTimerElementsStatus() {
		if (timerElements != null)
			return "Basic timer elements[OK]";
		else
			return "Basic timer elements[BAD]";
	}

	public String getDBFStatus() {
		if (lapsF != null)
			return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
		else
			return "Lap elements[BAD]";
	}

	public String getLapsFStatus() {
		if (lapsF != null)
			return "Lap elements[OK]: Laps[" + lapsF.getLapCount() + "]";
		else
			return "Lap elements[BAD]";
	}

	public void putSessionInfo(Bundle sessionInfo) {
		timerElements.putSessionInfo(sessionInfo);
		lapsF.putSessionInfo(sessionInfo);
	}

	protected void setListeners(FrameTimer framer) {
		timerElements.addFrameTimerListenersTo(framer);
		framer.addFrameTimerListener(this);
		timerElements.setTimerElementsListener(lapsF);
	}

	public void setStatusLogging(boolean status) {
		if (status) {
			if (!logging.status) {
				logging.status = true;
				logging.listener = new FrameIntervalListener() {
					@Override
					public void OnFrameReached() {
						logStatus();
					}
				};
				framer.addFrameReachListener(logging.listener,
						logging.loggingInterval);
			}
		} else {
			if (logging.status) {
				if (logging.listener != null)
					logging.status = false;
				framer.removeFrameIntervalListenerContainer(logging.listener);
			}
		}
	}

	public void setTargetTime(long timeInMilliseconds) {
		Log.d("StudyTimer", "Target time set: " + timeInMilliseconds);
		timerElements.setTargetTIme(timeInMilliseconds);
	}

	public float getLapProgress() {
		assert timerElements != null;
		if (timerElements != null)
			return timerElements.getLapProgress();
		else
			return -1;
	}

	@Override
	public void onNewFrame() {
		filler.updateFillerProgress(timerElements.getLapProgress());
		/*
		 * float lapProgress=timerElements.getLapProgress(); if(lapProgress!=-1)
		 * lapsF.updateCurrentLapProgress(lapProgress);
		 */
	}

	@Override
	public void onEndFrame() {

	}

	@Override
	public void onReset() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_toggle:
			toggle();
			break;
		case R.id.b_lap:
			lap();
			break;

		}
	}

	public void setNoLapMode(boolean noLaps) {
		FragmentTransaction transaction = fragM.beginTransaction();
		if (noLaps) {
			transaction.hide(lapsF);
			transaction.show(emptyLap);
			emptyLap.getView().invalidate();
		} else {
			transaction.hide(emptyLap);
			transaction.show(lapsF);
			lapsF.getView().invalidate();
		}
		transaction.commit();
	}

	public void setMode(int MODE) {
		switch (MODE) {
		case MODES.NORMAL:
		case MODES.SESSION_EDIT:
			Log.d("StudyTimer", "Setting mode to: " + MODE);
			timerElements.setMode(MODE);
			break;
		default:
			Log.d("StudyTimer", "Unknown TimerElement mode request received: "
					+ MODE);
		}
	}

	public void createNewSessionFromBundle(Bundle sessionInfo) {
		saveSessionToPrefsFromBundle(sessionInfo);
		Log.i("Sessions", "Creating new session:");
		resetSession();
		lapsF.createNewSession(sessionInfo);
		timerElements.createNewSession(sessionInfo);
		Log.i("Sessions", "Session created");
	}

	public void putSessionInfoToBundle(Bundle sessionInfo) {
		timerElements.putSessionInfo(sessionInfo);
		lapsF.putSessionInfo(sessionInfo);
		Log.i("Sessions", "Session info put into bundle");
	}

	public void saveSessionToPrefsFromBundle(Bundle sessionInfo) {
		SharedPreferences.Editor editor = sessionPrefs.edit();
		editor.putLong(STSP.keys.targetTime,
				sessionInfo.getLong(STSP.keys.targetTime, defaults.targetTime));
		editor.putInt(STSP.keys.totalLaps,
				sessionInfo.getInt(STSP.keys.totalLaps, defaults.totalLaps));

		editor.putLong(STSP.keys.elapse,
				sessionInfo.getLong(STSP.keys.elapse, 0));
		editor.putLong(STSP.keys.totalElapse,
				sessionInfo.getLong(STSP.keys.totalElapse, 0));
		editor.putLong(
				STSP.keys.stopTime,
				sessionInfo.getLong(STSP.keys.stopTime,
						System.currentTimeMillis()));
		editor.putBoolean(STSP.keys.lapTimeUp,
				sessionInfo.getBoolean(STSP.keys.lapTimeUp, false));
		editor.commit();
		Log.i("Sessions",
				"Session saved to shared session preferences from bundle");
	}

	public void saveSessionToPrefs() {
		Bundle sessionInfo = new Bundle();
		timerElements.putSessionInfo(sessionInfo);
		lapsF.putSessionInfo(sessionInfo);
		Log.i("Sessions",
				"Session bundled and about to be parceled to save session to preferences from bundle");
		saveSessionToPrefsFromBundle(sessionInfo);
	}

	public Bundle getSessionBundleFromPrefs() {
		Bundle sessionInfo = new Bundle();
		sessionInfo
				.putLong(STSP.keys.targetTime, sessionPrefs.getLong(
						STSP.keys.targetTime, defaults.targetTime));
		sessionInfo.putInt(STSP.keys.totalLaps,
				sessionPrefs.getInt(STSP.keys.totalLaps, defaults.totalLaps));
		Log.i("Sessions",
				"Session loaded from shared session preferences to bundle");
		return sessionInfo;
	}

	public void loadSessionFromBundle(Bundle sessionInfo) {
		timerElements.loadSessionFromBundle(sessionInfo);
		lapsF.loadSessionFromBundle(sessionInfo);
		Log.i("Sessions", "Session loaded from bundle");
	}
}
